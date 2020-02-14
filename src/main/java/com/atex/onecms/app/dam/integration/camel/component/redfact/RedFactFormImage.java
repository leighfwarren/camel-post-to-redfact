package com.atex.onecms.app.dam.integration.camel.component.redfact;

import org.apache.http.NameValuePair;

import java.util.List;

public class RedFactFormImage {

    public RedFactFormImage(String contentIdString, String onecmsImageUrl, List<NameValuePair> formImage) {
        this.formImage = formImage;
        this.onecmsImageUrl = onecmsImageUrl;
        this.contentIdString = contentIdString;
    }

    public List<NameValuePair> getFormImage() {
        return formImage;
    }

    public void setFormImage(List<NameValuePair> formImage) {
        this.formImage = formImage;
    }

    private List<NameValuePair> formImage;
    private String contentIdString;
    private String onecmsImageUrl;

    public String getContentIdString() {
        return contentIdString;
    }

    public void setContentIdString(String contentIdString) {
        this.contentIdString = contentIdString;
    }

    public String getOnecmsImageUrl() {
        return onecmsImageUrl;
    }

    public void setOnecmsImageUrl(String onecmsImageUrl) {
        this.onecmsImageUrl = onecmsImageUrl;
    }
}
