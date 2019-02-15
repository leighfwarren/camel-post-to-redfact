package com.atex.onecms.app.dam.integration.camel.component.redfact.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.interceptor.Tracer;
import org.springframework.stereotype.Component;

import com.atex.onecms.app.dam.camel.CamelContextConfigurator;
import com.atex.onecms.app.dam.integration.camel.component.redfact.RedFactProperties;

/**
 * Camel Configurator
 *
 * @author mnova
 */
@Component
public class CamelConfigurator implements CamelContextConfigurator {

    @Override
    public void configure(final CamelContext camelContext) throws Exception {

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
