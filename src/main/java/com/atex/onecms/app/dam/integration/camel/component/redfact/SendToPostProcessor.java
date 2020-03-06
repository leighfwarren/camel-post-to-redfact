package com.atex.onecms.app.dam.integration.camel.component.redfact;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
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
import com.atex.onecms.app.dam.util.DamUtils;
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
import com.polopoly.cm.client.CmClient;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.UserId;
import com.polopoly.util.StringUtil;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
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

    private static CmClient cmClient;
    private static ContentManager contentManager;
    private static Application application;
    private Caller latestCaller = null;

    private static RedFactUtils redFactUtils;
    private RedfactConfig redFactConfig;

    private static final String IMAGE_SERVICE_URL_FALLBACK = "http://localhost:8080";
    private static String imageServiceUrl;


    private final static String STATUS_ONLINE = "online";
    private final static String STATUS_ERROR = "errorpostingtoredfact";

    private String onlineContentViewName = ContentManager.SYSTEM_VIEW_PUBLIC;

    @Override
    public void onAfterInit(final ServletContext ctx,
                            final String name,
                            final Application _application) {

        log.debug("Initializing SendToPostProcessor");

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
            if (cmClient == null) {
                cmClient = application.getPreferredApplicationComponent(CmClient.class);;
                if (cmClient == null) {
                    throw new CMException("No cmClient present in Application '"
                      + application.getName() + "'");
                }
            }

            try {
                if (com.polopoly.common.lang.StringUtil.isEmpty(DamUtils.getDamUrl())) {
                    imageServiceUrl = IMAGE_SERVICE_URL_FALLBACK;
                    log.warn("desk.config.damUrl is not configured in connection.properties");
                } else {
                    URL url = new URL(DamUtils.getDamUrl());
                    imageServiceUrl = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
                }
            } catch (MalformedURLException e) {
                log.error("Cannot configure the imageServiceUrl: " + e.getMessage());
                imageServiceUrl = IMAGE_SERVICE_URL_FALLBACK;
            }

            if (redFactUtils == null)
                redFactUtils = new RedFactUtils();

            log.debug("Started SendToPostProcessort");
        } catch (Exception e) {
            log.error("Cannot start SendToPostProcessor: " + e.getMessage(), e);
        } finally {
            log.debug("SendToPostProcessor complete");
        }
    }

    @Override
    public void process(final Exchange exchange) throws Exception {

        redFactConfig = RedfactApplication.getRedFactConfig();
        log.debug("SendToPostProcessor - start work");

        try {
            if (cmClient == null || contentManager == null) {
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

            RedFactFormArticle redFactFormArticle = redFactUtils.convert(imageServiceUrl, redFactConfig, cmClient, contentManager, cr);
            NameValuePair formDirectContentParam = null;
            for (RedFactFormImage redFactFormImage : redFactFormArticle.getRedFactFormImages()) {

                redFactUtils.sendUrlToSftp(redFactFormImage.getOnecmsImageUrl(), redFactConfig.getPrivateSshKeyPath(), redFactFormImage.getContentIdString(),
                  redFactConfig.getExternalImageStoreUsername(),
                  redFactConfig.getExternalImageStoreHost(),
                  Integer.parseInt(redFactConfig.getExternalImageStorePort()),
                  redFactConfig.getExternalImageStorePath());
                Pair<String, Integer> httpImageResult = redFactUtils.sendImageFormToRedFact(redFactConfig.getApiUrl(), redFactFormImage);
                log.debug("redfact image id ="+httpImageResult.getKey()+" status = "+httpImageResult.getValue());
                NameValuePair currentFormDirectContentParam = getFormDirectContentParam(httpImageResult.getKey());
                if (formDirectContentParam != null) formDirectContentParam = new BasicNameValuePair(formDirectContentParam.getName(),formDirectContentParam.getValue()+"--"+currentFormDirectContentParam.getValue());
            }
            if (formDirectContentParam != null) {
                redFactFormArticle.getFormArticle().add(formDirectContentParam);
            }
            Pair<String, Integer> httpArticleResult = redFactUtils.sendArticleFormToRedFact(redFactConfig.getApiUrl(), contentIdString, cr, redFactFormArticle);

            String newStatus;

            if (httpArticleResult.getValue() == 200) {
                newStatus = STATUS_ONLINE;
            } else {
                newStatus = STATUS_ERROR;
            }

            String redFactId = "";

            if (newStatus.equals(STATUS_ONLINE)) {
                final DamEngagementUtils utils = new DamEngagementUtils(contentManager);
                redFactId = "ar/"+httpArticleResult.getKey();
                final EngagementDesc engagement = createEngagementObject((redFactId != null) ? redFactId : "", getCurrentCaller());
                engagement.getAttributes().add(createElement("link", redFactId));

                final String existingRedFactId = getRedFactIdFromEngagement(utils, contentId);
                if (existingRedFactId != null) {
                    utils.updateEngagement(contentId, engagement);
                } else {
                    utils.addEngagement(contentId, engagement);
                }
            }
            setWebStatus(cr,newStatus);

            exchange.getOut().setBody(redFactId);
        } finally {
            log.debug("SendToPostProcessor - end work");
        }
    }

    private NameValuePair getFormDirectContentParam(String imageId) {
        return new BasicNameValuePair("direct_content", "img#"+imageId);
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
        ContentVersionId idStatusList = contentManager.resolve("atex.WebStatusList", SYSTEM_SUBJECT);
        ContentResult<WFStatusListBean> statusList = contentManager.get(idStatusList, WFStatusListBean.class, SYSTEM_SUBJECT);
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
