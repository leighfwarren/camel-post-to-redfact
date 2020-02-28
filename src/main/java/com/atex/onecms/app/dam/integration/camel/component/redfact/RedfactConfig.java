package com.atex.onecms.app.dam.integration.camel.component.redfact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Redfact properties.
 *
 * @author leighfwarren
 */
public class RedfactConfig {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private String apiUrl;
    private String username;
    private String password;
    private String imagePrefix;
    private String imageSecret;
    private String imageFormat;
    private String externalImageStoreHost;
    private String externalImageStoreUsername;
    private String privateSshKeyPath;
    private String externalImageStorePort;
    private String externalImageStorePath;
    private String externalImageStoreUrl;

    public String getExternalImageStoreHost() {
        return externalImageStoreHost;
    }

    public void setExternalImageStoreHost(String externalImageStoreHost) {
        this.externalImageStoreHost = externalImageStoreHost;
    }

    public String getExternalImageStoreUsername() {
        return externalImageStoreUsername;
    }

    public void setExternalImageStoreUsername(String externalImageStoreUsername) {
        this.externalImageStoreUsername = externalImageStoreUsername;
    }

    public String getPrivateSshKeyPath() {
        return privateSshKeyPath;
    }

    public void setPrivateSshKeyPath(String privateSshKeyPath) {
        this.privateSshKeyPath = privateSshKeyPath;
    }

    public RedfactConfig() {
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImagePrefix() {
        return imagePrefix;
    }

    public void setImagePrefix(String imagePrefix) {
        this.imagePrefix = imagePrefix;
    }

    public String getImageSecret() {
        return imageSecret;
    }

    public void setImageSecret(String imageSecret) {
        this.imageSecret = imageSecret;
    }

    public String getOneCMSImageSecret() {
        return imageSecret;
    }

    public String getOneCMSImagePrefix() {
        return imagePrefix;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    public String getExternalImageStorePort() {
        return externalImageStorePort;
    }

    public void setExternalImageStorePort(String externalImageStorePort) {
        this.externalImageStorePort = externalImageStorePort;
    }

    public String getExternalImageStoreUrl() {
        return externalImageStoreUrl;
    }

    public void setExternalImageStoreUrl(String externalImageStoreUrl) {
        this.externalImageStoreUrl = externalImageStoreUrl;
    }

    public String getExternalImageStorePath() {
        return externalImageStorePath;
    }

    public void setExternalImageStorePath(String externalImageStorePath) {
        this.externalImageStorePath = externalImageStorePath;
    }
}
