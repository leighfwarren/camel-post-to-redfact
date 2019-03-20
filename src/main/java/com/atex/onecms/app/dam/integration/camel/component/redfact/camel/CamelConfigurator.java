package com.atex.onecms.app.dam.integration.camel.component.redfact.camel;

import com.atex.onecms.app.dam.integration.camel.component.redfact.RedfactApplication;
import com.atex.onecms.app.dam.integration.camel.component.redfact.RedfactConfig;
import com.atex.onecms.app.dam.integration.camel.component.redfact.config.RedfactConfigPolicy;
import com.polopoly.application.Application;
import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.policy.PolicyCMServer;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.interceptor.Tracer;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.atex.onecms.app.dam.camel.CamelContextConfigurator;

/**
 * Camel Configurator
 *
 * @author mnova
 */
@Component
public class CamelConfigurator implements CamelContextConfigurator {

    private Logger log = Logger.getLogger(CamelConfigurator.class);

    @Override
    public void configure(final CamelContext camelContext) throws Exception {

        RedfactConfig config;
        try {
            Application application = RedfactApplication.getApplication();
            final CmClient cmClient = application.getPreferredApplicationComponent(CmClient.class);
            final PolicyCMServer cmServer = cmClient.getPolicyCMServer();
            RedfactConfigPolicy policy = (RedfactConfigPolicy) cmServer.getPolicy(new ExternalContentId(RedfactConfigPolicy.CONFIG_EXTERNAL_ID));
            if (policy == null) throw new CMException("no redfact config");
            config = policy.getConfig();
            RedfactConfig.setInstance(config);

        } catch (CMException | IllegalApplicationStateException e) {
            log.debug("RedfactApplication: "+e.getMessage());
        }

        final SendToPostProcessor processor = new SendToPostProcessor();

        Tracer tracer = new Tracer();
        tracer.setEnabled(true);
        camelContext.addInterceptStrategy(tracer);

        camelContext.setTracing(true);
        camelContext.addRoutes(
                new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from("seda:postToRedFact")
                                .process(processor);
                    }
                }
        );
    }

}
