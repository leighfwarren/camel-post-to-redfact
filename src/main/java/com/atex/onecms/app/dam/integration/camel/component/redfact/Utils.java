package com.atex.onecms.app.dam.integration.camel.component.redfact;

import com.atex.onecms.app.dam.util.DamUtils;
import com.polopoly.application.ConnectionPropertiesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class Utils {

    protected static final Logger log = LoggerFactory.getLogger(Utils.class);

    private static final String IMAGE_SERVICE_URL_FALLBACK = "http://localhost:8080";

    private static String imageServiceUrl;

    public static String getImageServiceUrl() throws ConnectionPropertiesException {
        if (imageServiceUrl == null) {
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
        }
        return imageServiceUrl;
    }
}
