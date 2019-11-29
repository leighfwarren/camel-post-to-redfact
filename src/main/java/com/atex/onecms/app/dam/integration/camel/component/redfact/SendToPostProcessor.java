package com.atex.onecms.app.dam.integration.camel.component.redfact;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.atex.onecms.app.dam.engagement.EngagementAspect;
import com.atex.onecms.app.dam.engagement.EngagementDesc;
import com.atex.onecms.app.dam.engagement.EngagementElement;
import com.atex.onecms.app.dam.standard.aspects.OneArticleBean;
import com.atex.onecms.app.dam.util.ContentWriteSerializer;
import com.atex.onecms.app.dam.util.DamEngagementUtils;
import com.atex.onecms.app.dam.workflow.WFStatusBean;
import com.atex.onecms.app.dam.workflow.WFStatusListBean;
import com.atex.onecms.app.dam.workflow.WebContentStatusAspectBean;
import com.atex.onecms.content.*;
import com.atex.onecms.content.repository.ContentModifiedException;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.polopoly.application.Application;
import com.polopoly.application.ApplicationInitEvent;
import com.polopoly.application.ApplicationOnAfterInitEvent;
import com.polopoly.cm.client.CMException;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.UserId;
import com.polopoly.util.StringUtil;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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

import javax.annotation.Nullable;
import javax.servlet.ServletContext;

/**
 * Simple processor that send content to Red Fact.
 *
 * @author mnova
 */
@ApplicationInitEvent
public class SendToPostProcessor implements Processor, ApplicationOnAfterInitEvent {

    private static final Subject SYSTEM_SUBJECT = new Subject("98", (String)null);

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final String REDFACT_APPTYPE = "redfact";
    private static final String REDFACT_ID_PREFIX = "rf-";

    private static ContentManager contentManager;
    private static Application application;
    private Caller latestCaller = null;

    @Override
    public void onAfterInit(final ServletContext ctx,
                            final String name,
                            final Application _application) {

        log.info("Initializing SendToPostProcessor");

        try {
            if (application == null) application = _application;
            if (contentManager == null) {
                final RepositoryClient repoClient = (RepositoryClient) application.getApplicationComponent(RepositoryClient.DEFAULT_COMPOUND_NAME);
                if (repoClient == null) {
                    throw new CMException("No RepositoryClient named '"
                      + RepositoryClient.DEFAULT_COMPOUND_NAME
                      + "' present in Application '"
                      + application.getName() + "'");
                }
                contentManager = repoClient.getContentManager();
            }
            log.info("Started SendToPostProcessort");
        } catch (Exception e) {
            log.error("Cannot start SendToPostProcessor: " + e.getMessage(), e);
        } finally {
            log.info("SendToPostProcessor complete");
        }
    }

    @Override
    public void process(final Exchange exchange) throws Exception {

        log.info("SendToPostProcessor - start work");

        try {
            if (contentManager == null) {
                exchange.getIn().setFault(true);
                return;
            }
            String contentIdString;
            if (exchange.getIn().getBody() instanceof String) {
                contentIdString = getContentId(exchange);
            }
            else {
                contentIdString = exchange.getIn().getHeader("contentId", ContentId.class).getKey();
            }

            ContentResult<OneArticleBean> cr = null;
            ContentId contentId = IdUtil.fromString(contentIdString);
            ContentVersionId contentVersionId = contentManager.resolve(contentId, Subject.NOBODY_CALLER);
            if (contentVersionId != null) {
                cr = contentManager.get(contentVersionId, null, OneArticleBean.class, null, Subject.NOBODY_CALLER);
            }

            if (cr == null) {
                exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
                return;
            }


            Pair<String, Integer> httpResult = sendArticleToRedFact(contentIdString, cr);
            String newStatus;
            if (httpResult.getValue() == 200) {
                newStatus = "online";
            } else {
                newStatus = "errorpostingtoredfact";
            }

            if (newStatus.equals("online")) {
                final DamEngagementUtils utils = new DamEngagementUtils(contentManager);
                String redFactId = httpResult.getKey();
                final EngagementDesc engagement = createEngagementObject((redFactId != null) ? redFactId : "", getCurrentCaller());
                engagement.getAttributes().add(createElement("link", httpResult.getKey()));

                final String existingRedFactId = getRedFactIdFromEngagement(utils, contentId);
                if (existingRedFactId != null) {
                    utils.updateEngagement(contentId, engagement);
                } else {
                    utils.addEngagement(contentId, engagement);
                }
            }
            setWebStatus(cr,newStatus);

            exchange.getOut().setBody(httpResult.getKey());
        } finally {
            log.info("SendToPostProcessor - end work");
        }
    }

    private String getRedFactIdFromEngagement(final DamEngagementUtils utils, final ContentId contentId) throws CMException {
        final EngagementAspect engAspect = utils.getEngagement(contentId);
        if (engAspect != null) {
            final EngagementDesc engagement = Iterables.getFirst(
              Iterables.filter(engAspect.getEngagementList(), new Predicate<EngagementDesc>() {
                  @Override
                  public boolean apply(@Nullable final EngagementDesc engagementDesc) {
                      return (engagementDesc != null) && com.polopoly.common.lang.StringUtil.equals(engagementDesc.getAppType(), REDFACT_APPTYPE);
                  }
              }), null);
            if (engagement != null) {
                final String pk = engagement.getAppPk();
                if (pk.startsWith(REDFACT_ID_PREFIX)) {
                    return pk.substring(3);
                }
            }
        }
        return null;
    }

    private String getContentId(Exchange exchange) {
        String contentIdStr = (String) exchange.getIn().getBody();
        String tidiedContentIdStr = contentIdStr.replace("mutation:", "");
        int lastColon = tidiedContentIdStr.lastIndexOf(':');
        String unversionedContentId = tidiedContentIdStr.substring(0, lastColon);
        return unversionedContentId;
    }

    private String onlineContentViewName = ContentManager.SYSTEM_VIEW_PUBLIC;

    private void setWebStatus(ContentResult<OneArticleBean> cr, String newStatus) {

        WebContentStatusAspectBean status = createWebContentStatus(newStatus);
        final ContentWrite<OneArticleBean> cw = new ContentWriteBuilder<OneArticleBean>()
            .origin(cr.getContent().getId())
            .type(OneArticleBean.ASPECT_NAME)
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

    private Pair<String, Integer> sendArticleToRedFact(final String contentIdString, final ContentResult<OneArticleBean> cr) throws IOException {

        String url = RedfactConfig.getInstance().getApiUrl();
        List<NameValuePair> nameValuePairs = new RedFactUtils().convert(cr);
        Pair<String,Integer> httpResult = sendForm(url, nameValuePairs);
        log.info("response: " + httpResult.getKey());
        if (httpResult.getValue() == 412) {
            log.info("create failed, sending update");
            // either article failed or needs exists
            // try to update instead
            String updateUrl = url;
            if (!updateUrl.endsWith("/")) updateUrl += "/";
            updateUrl += contentIdString.substring("onecms:".length());
            log.info("sending update url:"+updateUrl);
            httpResult = sendForm(updateUrl, nameValuePairs);
            log.info("update response: " + httpResult.getKey());
        }
        log.info("status = "+httpResult.getValue());
        if (httpResult.getValue() == 200) {
            log.info("Article send successful, setting status");
        }
        return httpResult;
    }

    private Pair<String,Integer> sendForm(final String url, final Collection<NameValuePair> params) throws IOException {
        try (final OutputStream stream = new ByteArrayOutputStream()) {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost method = new HttpPost(url);
            method.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            try (CloseableHttpResponse response = httpclient.execute(method)) {
                System.out.println(response.getStatusLine());
                HttpEntity responseEntity = response.getEntity();
                String left = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                String redFactId = "";
                try {
                    JsonObject jsonResult = new JsonParser().parse(left).getAsJsonObject();
                    redFactId = jsonResult.get("id").getAsString();
                } catch (Exception e) {};
                Integer right = response.getStatusLine().getStatusCode();
                Pair<String,Integer> result = new ImmutablePair<String,Integer>(redFactId, right);
                EntityUtils.consume(responseEntity);
                return result;
            }
        }
    }

    private EngagementDesc createEngagementObject(final String wpId, final Caller caller) {
        final EngagementDesc engagement = new EngagementDesc();
        engagement.setAppType(REDFACT_APPTYPE);
        engagement.setAppPk(REDFACT_ID_PREFIX + wpId);
        engagement.setUserName("sysadmin");
        return engagement;
    }

    private EngagementElement createElement(final String name, final String value) {
        final EngagementElement element = new EngagementElement();
        element.setName(name);
        element.setValue(value);
        return element;
    }

    private WebContentStatusAspectBean createWebContentStatus(final String statusId) {
        final WFStatusBean status = getWebStatusById(statusId);
        final WebContentStatusAspectBean bean = new WebContentStatusAspectBean();
        if (status != null) {
            bean.setStatus(status);
        }
        return bean;
    }

    private WFStatusBean getWebStatusById(String statusId) {
        ContentVersionId idStatusList = this.contentManager.resolve("atex.WebStatusList", SYSTEM_SUBJECT);
        ContentResult<WFStatusListBean> statusList = this.contentManager.get(idStatusList, WFStatusListBean.class, SYSTEM_SUBJECT);
        WFStatusListBean statusListBean = statusList.getContent().getContentData();
        List<WFStatusBean> statuses = statusListBean.getStatus();
        Iterator iterator = statuses.iterator();

        WFStatusBean wfStatusBean;
        do {
            if (!iterator.hasNext()) {
                return null;
            }

            wfStatusBean = (WFStatusBean)iterator.next();
        } while(!wfStatusBean.getStatusID().equals(statusId));

        return wfStatusBean;
    }



}
