package com.atex.onecms.app.dam.integration.camel.component.redfact;

import com.atex.onecms.content.ContentManager;
import com.atex.onecms.content.RepositoryClient;
import com.atex.onecms.content.files.FileService;
import com.atex.onecms.ws.search.SearchClient;
import com.atex.onecms.ws.search.SolrHttpClient;
import com.polopoly.application.*;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

/**
 * Redfact properties.
 *
 * @author leighfwarren
 */
public class RedfactConfig {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static RedfactConfig instance;

    private String apiUrl;
    private String username;
    private String password;
    private String imagePrefix;
    private String imageSecret;
    private String imageFormat;

    public RedfactConfig() {
    }

    public static RedfactConfig getInstance() {
        return instance;
    }

    public static void setInstance(RedfactConfig instance) {
        RedfactConfig.instance = instance;
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
}
