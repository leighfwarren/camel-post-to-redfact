package com.atex.onecms.app.dam.integration.camel.component.redfact.camel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import com.atex.onecms.app.dam.camel.processor.InstagramToNoSqlProcessor;
import com.atex.onecms.app.dam.camel.processor.TweetsToNoSqlProcessor;
import com.atex.onecms.app.dam.engagement.EngagementDesc;
import com.atex.onecms.app.dam.engagement.EngagementElement;
import com.atex.onecms.app.dam.integration.camel.component.redfact.RedFactArticleBean;
import com.atex.onecms.app.dam.util.ContentWriteSerializer;
import com.atex.onecms.app.dam.util.DamCamelUtils;
import com.atex.onecms.app.dam.util.DamEngagementUtils;
import com.atex.onecms.app.dam.workflow.WFStatusBean;
import com.atex.onecms.app.dam.workflow.WFStatusUtils;
import com.atex.onecms.app.dam.workflow.WebContentStatusAspectBean;
import com.atex.onecms.content.*;
import com.atex.onecms.content.repository.ContentModifiedException;
import com.polopoly.application.Application;
import com.polopoly.application.ApplicationInitEvent;
import com.polopoly.application.ApplicationOnAfterInitEvent;
import com.polopoly.cm.client.CmClient;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.UserId;
import com.polopoly.util.StringUtil;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import com.atex.onecms.app.dam.integration.camel.component.redfact.RedfactConfig;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.util.jndi.JndiContext;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletContext;

/**
 * Simple processor that send content to Red Fact.
 *
 * @author mnova
 */
@ApplicationInitEvent
public class SendToPostProcessor implements Processor, ApplicationOnAfterInitEvent {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final String REDFACT_APPTYPE = "redfact";
    private static final String REDFACT_ID_PREFIX = "rf-";

    private static ContentManager contentManager;
    private Application application;
		private Caller latestCaller = null;

		@Override
		public void onAfterInit(final ServletContext ctx,
		                        final String name,
		                        final Application application) {

			log.info("Initializing dam camel context");

			try {
				if (name.equals("integration-server")) this.application = application;
			} catch (Exception e) {
				log.error("cannot initialise camel context: " + e.getMessage(), e);
			} finally {
				log.info("Dam camel context initialisation complete");
			}
		}

		@Override
    public void process(final Exchange exchange) throws Exception {

        log.info("SendToPostProcessor - start work");

        try {
            final Message msg = exchange.getIn();
            RedFactArticleBean redFactArticleBean = msg.getBody(RedFactArticleBean.class);
            final Caller caller = msg.getHeader("caller", Caller.class);
            latestCaller = caller;
            final ContentId contentId = msg.getHeader("contentId", ContentId.class);
            if (contentManager == null) {
                contentManager = msg.getHeader("contentManager", ContentManager.class);
            }

            final DamEngagementUtils utils = new DamEngagementUtils(contentManager);

            CloseableHttpResponse response = sendArticleToRedFact(redFactArticleBean);

//            String newStatus;
//            if (response.getStatusLine().getStatusCode() == 200) {
//                newStatus = "online";
//
//                String redFactId = "";
//                final EngagementDesc engagement = createEngagementObject((redFactId != null) ? redFactId : "", caller);
////            engagement.getAttributes().add(createElement("link", response.getLink()));
//
//                if (redFactId != null) {
//                    utils.updateEngagement(contentId, engagement);
//                } else {
//                    utils.addEngagement(contentId, engagement);
//                }
//
//            } else {
//                newStatus = "errorposttoredfact";
//            }
//            final ContentVersionId id = contentManager.resolve(contentId, Subject.NOBODY_CALLER);
//            final ContentResult<Object> cr = contentManager.get(id, Object.class, Subject.NOBODY_CALLER);
//            setWebStatus(cr,newStatus);
            exchange.getOut().setBody(response);
        } finally {
            log.info("SendToPostProcessor - end work");
        }
    }

    private String onlineContentViewName = ContentManager.SYSTEM_VIEW_PUBLIC;

    private void setWebStatus(ContentResult<Object> cr, String newStatus) {

        WebContentStatusAspectBean status = createWebContentStatus(newStatus);
        final ContentWrite<Object> cw = new ContentWriteBuilder<>()
            .origin(cr.getContent().getId())
            .type(cr.getContent().getContentDataType())
            .aspects(cr.getContent().getAspects())
            .aspect(WebContentStatusAspectBean.ASPECT_NAME, status)
            .mainAspect(cr.getContent().getContentAspect())
            .operation(new AssignToViewsOperation(onlineContentViewName))
            .buildUpdate();

        applyContentWrite(cr.getContentId(), cw);
    }

    private <T> void applyContentWrite(final ContentVersionId contentId, final ContentWrite<T> cw) {
        try {
            contentManager.update(contentId.getContentId(), cw, SubjectUtil.fromCaller(getCurrentCaller()));
        } catch (ContentModifiedException e) {
            String json = "";
            try {
                json = new ContentWriteSerializer(application).serialize(cw).getAsString();
            } catch (Exception ignoreE) {
                log.error("cannot serialize content write");
            }
            log.error("cannot apply contentWrite to " + contentId + ": " + e.getMessage(), e);
            if (!StringUtil.isEmpty(json)) {
                log.error("the contentWrite json is " + json);
            }
        }
    }

    private Caller getCurrentCaller() {
        return Optional
            .ofNullable(latestCaller)
            .orElse(new Caller(new UserId("98")));
    }

    private CloseableHttpResponse sendArticleToRedFact(final RedFactArticleBean articleBean) throws IOException {

        String url = RedfactConfig.getInstance().getApiUrl();
        CloseableHttpResponse response = sendForm(url, articleBean.getParams());
        log.info("response: " + response.getEntity().toString());
        if (response.getStatusLine().getStatusCode() == 412) {
            log.info("create failed, sending update");
            // either article failed or needs exists
            // try to update instead
            String updateUrl = url;
            if (!updateUrl.endsWith("/")) updateUrl += "/";
            for (NameValuePair param : articleBean.getParams()) {
                if (param.getName().equals("id_atex")) {
                    updateUrl += param.getValue();
                    break;
                }
            }
            log.info("sending update url:"+updateUrl);
            response = sendForm(updateUrl, articleBean.getParams());
            log.info("update response: " + response.getEntity().toString());
        }
        log.info("status = "+response.getStatusLine().getStatusCode());
        if (response.getStatusLine().getStatusCode() == 200) {
            log.info("Article send successful, setting status");
        }
        return response;
    }

    private CloseableHttpResponse sendForm(final String url, final List<NameValuePair> params) throws IOException {
        try (final OutputStream stream = new ByteArrayOutputStream()) {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost method = new HttpPost(url);
            method.setEntity(new UrlEncodedFormEntity(params));
            try (CloseableHttpResponse response = httpclient.execute(method)) {
                System.out.println(response.getStatusLine());
                HttpEntity responseEntity = response.getEntity();
                EntityUtils.consume(responseEntity);
                return response;
            }
        }
    }

    private EngagementDesc createEngagementObject(final String wpId, final Caller caller) {
        final EngagementDesc engagement = new EngagementDesc();
        engagement.setAppType(REDFACT_APPTYPE);
        engagement.setAppPk(REDFACT_ID_PREFIX + wpId);
        engagement.setUserName(caller.getLoginName());
        return engagement;
    }

    private EngagementElement createElement(final String name, final String value) {
        final EngagementElement element = new EngagementElement();
        element.setName(name);
        element.setValue(value);
        return element;
    }

    private WebContentStatusAspectBean createWebContentStatus(final String statusId) {
        final WFStatusUtils statusUtils = new WFStatusUtils(contentManager);
        final WFStatusBean status = statusUtils.getStatusById(statusId);
        final WebContentStatusAspectBean bean = new WebContentStatusAspectBean();
        bean.setStatus(status);
        return bean;
    }


}
