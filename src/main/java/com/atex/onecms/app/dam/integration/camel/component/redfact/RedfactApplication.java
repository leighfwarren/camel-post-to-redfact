package com.atex.onecms.app.dam.integration.camel.component.redfact;

import com.atex.onecms.app.dam.integration.camel.component.redfact.config.RedfactConfigPolicy;
import com.polopoly.application.Application;
import com.polopoly.application.ApplicationInitEvent;
import com.polopoly.application.ApplicationOnAfterInitEvent;
import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.policy.PolicyCMServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

@ApplicationInitEvent
public class RedfactApplication implements ApplicationOnAfterInitEvent {

    private static Logger log = LoggerFactory.getLogger(RedfactApplication.class);

    private static Application application;

    @Override
    public void onAfterInit(ServletContext servletContext, String name, Application _application) {

        application = _application;

    }

    public static Application getApplication() {
        return application;
    }

    public static RedfactConfig getRedFactConfig() {
        RedfactConfig config;
        try {
            final CmClient cmClient = application.getPreferredApplicationComponent(CmClient.class);
            final PolicyCMServer cmServer = cmClient.getPolicyCMServer();
            RedfactConfigPolicy policy = (RedfactConfigPolicy) cmServer.getPolicy(new ExternalContentId(RedfactConfigPolicy.CONFIG_EXTERNAL_ID));
            if (policy == null) throw new CMException("no redfact config");
            config = policy.getConfig();
            return config;

        } catch (CMException | IllegalApplicationStateException e) {
            log.debug("RedfactApplication: "+e.getMessage());
            return null;
        }
    }
}
