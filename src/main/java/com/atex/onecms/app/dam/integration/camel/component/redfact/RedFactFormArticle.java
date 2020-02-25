package com.atex.onecms.app.dam.integration.camel.component.redfact;

import org.apache.http.NameValuePair;

import java.util.List;

public class RedFactFormArticle {

    private List<NameValuePair> formArticle;
    private String contentIdString;

    private List<RedFactFormImage> redFactFormImages;

    public RedFactFormArticle(String contentIdString, List<NameValuePair> formArticle, List<RedFactFormImage> redFactFormImages) {
        this.formArticle = formArticle;
        this.redFactFormImages = redFactFormImages;
        this.contentIdString = contentIdString;
    }

    public List<NameValuePair> getFormArticle() {
        return formArticle;
    }

    public void setFormArticle(List<NameValuePair> formArticle) {
        this.formArticle = formArticle;
    }

    public List<RedFactFormImage> getRedFactFormImages() {
        return redFactFormImages;
    }

    public void setRedFactFormImages(List<RedFactFormImage> redFactFormImages) {
        this.redFactFormImages = redFactFormImages;
    }

    public String getContentIdString() {
        return contentIdString;
    }

    public void setContentIdString(String contentIdString) {
        this.contentIdString = contentIdString;
    }
}
