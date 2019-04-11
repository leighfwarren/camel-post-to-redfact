package com.atex.onecms.app.dam.integration.camel.component.redfact;

import com.polopoly.application.Application;
import com.polopoly.application.ApplicationInitEvent;
import com.polopoly.application.ApplicationOnAfterInitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

@ApplicationInitEvent
public class RedfactApplication implements ApplicationOnAfterInitEvent {

    private Logger log = LoggerFactory.getLogger(RedfactApplication.class);

    private static Application application;

    @Override
    public void onAfterInit(ServletContext servletContext, String name, Application _application) {

        application = _application;

    }

    public static Application getApplication() {
        return application;
    }
}
