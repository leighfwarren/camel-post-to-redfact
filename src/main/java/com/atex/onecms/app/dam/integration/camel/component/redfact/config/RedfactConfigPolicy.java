package com.atex.onecms.app.dam.integration.camel.component.redfact.config;

import com.atex.onecms.app.dam.integration.camel.component.redfact.RedfactConfig;
import com.polopoly.cm.app.policy.SingleValuePolicy;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.ContentPolicy;
import com.polopoly.model.DescribesModelType;

@DescribesModelType
public class RedfactConfigPolicy extends ContentPolicy{

    public static final String CONFIG_EXTERNAL_ID = "plugins.com.atex.plugins.camel-post-to-redfact.Config";

    protected static final String USERNAME = "username";
    protected static final String PASSWORD = "password";
    protected static final String API_URL = "apiUrl";
    protected static final String ONECMS_IMAGE_PREFIX = "onecmsImagePrefix";
    protected static final String ONECMS_IMAGE_SECRET = "onecmsImageSecret";
    protected static final String ONECMS_IMAGE_FORMAT = "onecmsImageFormat";

    @Override
    protected void initSelf() {
        super.initSelf();
    }

    public String getUsername() throws CMException {
        return ((SingleValuePolicy) getChildPolicy(USERNAME)).getValue();
    }

    public String getPassword() throws CMException {
        return ((SingleValuePolicy) getChildPolicy(PASSWORD)).getValue();
    }

    public String getApiUrl() throws CMException {
        return ((SingleValuePolicy) getChildPolicy(API_URL)).getValue();
    }

    public String getOnecmsImagePrefix() throws CMException {
        return ((SingleValuePolicy) getChildPolicy(ONECMS_IMAGE_PREFIX)).getValue();
    }

    public String getOnecmsImageSecret() throws CMException {
            return ((SingleValuePolicy) getChildPolicy(ONECMS_IMAGE_SECRET)).getValue();
    }

    public String getOnecmsImageFormat() throws CMException {
            return ((SingleValuePolicy) getChildPolicy(ONECMS_IMAGE_FORMAT)).getValue();
    }

    public RedfactConfig getConfig() throws CMException {

        RedfactConfig bean = new RedfactConfig();
        bean.setUsername(getUsername());
        bean.setPassword(getPassword());
        bean.setApiUrl(getApiUrl());
        bean.setImageFormat(getOnecmsImageFormat());
        bean.setImagePrefix(getOnecmsImagePrefix());
        bean.setImageSecret(getOnecmsImageSecret());

        return bean;
    }
}
