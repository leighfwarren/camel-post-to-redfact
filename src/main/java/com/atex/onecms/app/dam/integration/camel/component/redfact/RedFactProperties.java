package com.atex.onecms.app.dam.integration.camel.component.redfact;

import com.atex.onecms.content.ContentManager;
import com.atex.onecms.content.RepositoryClient;
import com.atex.onecms.content.files.FileService;
import com.atex.onecms.content.repository.CouchbaseClientComponent;
import com.atex.onecms.ws.search.SearchClient;
import com.atex.onecms.ws.search.SolrHttpClient;
import com.polopoly.application.*;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.*;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.integration.IntegrationServerApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.net.URL;

/**
 * Redfact properties.
 *
 * @author leighfwarren
 */
@ApplicationInitEvent
public class RedFactProperties implements ApplicationOnAfterInitEvent {

    public static final String REDFACT_PROFILE_EXTERNALID = "environment.profile";
    public static final String REDFACT_COMPONENT_NAME = "properties";;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static RedFactProperties INSTANCE = null;

    private Application application;
    private ServletContext sc;

    private FileService fileService;
    private PolicyCMServer cmServer;
    private ContentManager contentManager;
    private SearchClient searchClient;

    private String apiHost;
    private String apiUser;
    private String apiPass;
    private String imagePrefix;
    private String imageSecret;
    private String imageFormat;
    private ConnectionProperties connectionProperties;
    private CmClient cmClient;

    public static synchronized RedFactProperties getInstance() {
        return INSTANCE;
    }

    public RedFactProperties() {
        if (INSTANCE == null)
            INSTANCE = this;
    }

    public void onAfterInit(ServletContext sc, String name, Application application) {
        this.sc = sc;
        this.application = application;
        this.init();
    }

    private void init() {

        try {
            cmClient = (CmClient) application.getApplicationComponent(EjbCmClient.DEFAULT_COMPOUND_NAME);
            cmServer = cmClient.getPolicyCMServer();

            final RepositoryClient repoClient = (RepositoryClient) application.getApplicationComponent(RepositoryClient.DEFAULT_COMPOUND_NAME);
            if (repoClient == null) {
                throw new CMException("No RepositoryClient named '"
                        + RepositoryClient.DEFAULT_COMPOUND_NAME
                        + "' present in Application '"
                        + application.getName() + "'");
            }
            contentManager = repoClient.getContentManager();

            final SolrHttpClient solrHttpClient = (SolrHttpClient) application.getApplicationComponent(SolrHttpClient.DEFAULT_COMPOUND_NAME);
            searchClient = solrHttpClient.getSearchClient();

            final HttpFileServiceClient httpFileServiceClient = application.getPreferredApplicationComponent(HttpFileServiceClient.class);
            fileService = httpFileServiceClient.getFileService();

            contentManager = repoClient.getContentManager();

            initProperties();

        } catch (Exception e) {
            log.error("cannot initialized", e);
        }
    }

    private void initProperties() {

        ContentRead environmentProfile;
        try {
            environmentProfile = cmServer.getContent(new ExternalContentId(REDFACT_PROFILE_EXTERNALID));
            String[] properties = environmentProfile.getComponentNames(REDFACT_COMPONENT_NAME);

            for (String propertyName : properties) {

                String[] parts = propertyName.split("\\.");
                if (parts.length > 1) {
                    if (parts[0].equals("redfact")) {
                        String value = environmentProfile.getComponent(REDFACT_COMPONENT_NAME, propertyName);
                        switch (parts[1]) {
                            case "api-url": this.apiHost = value; break;
                            case "username": this.apiUser = value; break;
                            case "password": this.apiPass = value; break;
                            case "onecms-image-prefix": this.imagePrefix = value; break;
                            case "onecms-image-secret": this.imageSecret = value; break;
                            case "onecms-image-format": this.imageFormat = value; break;
                        }
                    }
                }
            }

            if (this.imagePrefix != null && this.imagePrefix.endsWith("/")) {
                this.imagePrefix = this.imagePrefix.substring(0, this.imagePrefix.length() - 1);
            }
        } catch (CMException e) {
            log.error("Unable to load RedFact properties",e);
        }

    }

    public String getUsername() {
        return apiUser;
    }

    public String getPassword() {
        return apiPass;
    }

    public String getAPIUrl() {
        return apiHost;
    }

    public String getOneCMSImageSecret() {
        return imageSecret;
    }

    public String getOneCMSImagePrefix() {
        return imagePrefix;
    }

    public ContentManager getContentManager() {
        return contentManager;
    }

    public SearchClient getSearchClient() {
        return searchClient;
    }

    public FileService getFileService() {
        return fileService;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }
}
