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
    protected static final String EXTERNAL_IMAGE_STORE_HOST = "externalImageStoreHost";
    protected static final String EXTERNAL_IMAGE_STORE_USERNAME = "externalImageStoreUsername";
    protected static final String PRIVATE_SSH_KEY_PATH = "privateSshKeyPath";
    protected static final String EXTERNAL_IMAGE_STORE_PORT = "externalImageStorePort";
    protected static final String EXTERNAL_IMAGE_STORE_PATH = "externalImageStorePath";
    protected static final String EXTERNAL_IMAGE_STORE_URL = "externalImageStoreUrl";
    protected static final String FRONT_END_URL = "frontEndUrl";

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

    public String getExternalImageStoreHost() throws CMException {
        return ((SingleValuePolicy) getChildPolicy(EXTERNAL_IMAGE_STORE_HOST)).getValue();
    }

    public String getExternalImageStoreUsername() throws CMException {
        return ((SingleValuePolicy) getChildPolicy(EXTERNAL_IMAGE_STORE_USERNAME)).getValue();
    }

    public String getPrivateSshKeyPath() throws CMException {
        return ((SingleValuePolicy) getChildPolicy(PRIVATE_SSH_KEY_PATH)).getValue();
    }

    public String getExternalImageStorePort() throws CMException {
        return ((SingleValuePolicy) getChildPolicy(EXTERNAL_IMAGE_STORE_PORT)).getValue();
    }

    public String getExternalImageStorePath() throws CMException {
        return ((SingleValuePolicy) getChildPolicy(EXTERNAL_IMAGE_STORE_PATH)).getValue();
    }

    public String getExternalImageStoreUrl() throws CMException {
        return ((SingleValuePolicy) getChildPolicy(EXTERNAL_IMAGE_STORE_URL)).getValue();
    }

    public String getFrontEndUrl() throws CMException {
        return ((SingleValuePolicy) getChildPolicy(FRONT_END_URL)).getValue();
    }

    public RedfactConfig getConfig() throws CMException {

        RedfactConfig bean = new RedfactConfig();
        bean.setUsername(getUsername());
        bean.setPassword(getPassword());
        bean.setApiUrl(getApiUrl());
        bean.setImageFormat(getOnecmsImageFormat());
        bean.setImagePrefix(getOnecmsImagePrefix());
        bean.setImageSecret(getOnecmsImageSecret());
        bean.setExternalImageStoreHost(getExternalImageStoreHost());
        bean.setExternalImageStoreUsername(getExternalImageStoreUsername());
        bean.setPrivateSshKeyPath(getPrivateSshKeyPath());
        bean.setExternalImageStorePort(getExternalImageStorePort());
        bean.setExternalImageStorePath(getExternalImageStorePath());
        bean.setExternalImageStoreUrl(getExternalImageStoreUrl());
        bean.setFrontEndUrl(getFrontEndUrl());

        return bean;
    }
}
