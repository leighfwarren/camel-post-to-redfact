package com.atex.onecms.app.dam.integration.camel.component.redfact;

import java.util.List;

import com.atex.onecms.app.dam.integration.camel.component.redfact.json.*;
import com.atex.onecms.content.ContentId;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

/**
 * Bean representing a redfact article.
 *
 * @author leighfwarren
 */
public class RedFactArticleBean {

    @GSONCustomExclude
    private ContentId contentId;

    @SerializedName("Metadata")
    private RFMetaData metadata;

    @SerializedName("InternetAttr")
    private RFInternetAttr internetAttr;

    @SerializedName("Heading")
    private String heading;

    @SerializedName("Basetext")
    private String baseText;

    @SerializedName("Subheading")
    private String subheading;

    @SerializedName("Teaser")
    private String teaser;

    @SerializedName("Location")
    private String location;

    @SerializedName("Pictures")
    private List<RedFactImageBean> pictures;

    @SerializedName("Autor")
    private String autor;

    public RFMetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(RFMetaData metadata) {
        this.metadata = metadata;
    }

    public RFInternetAttr getInternetAttr() {
        return internetAttr;
    }

    public void setInternetAttr(RFInternetAttr internetAttr) {
        this.internetAttr = internetAttr;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getBaseText() {
        return baseText;
    }

    public void setBaseText(String baseText) {
        this.baseText = baseText;
    }

    public String getSubheading() {
        return subheading;
    }

    public void setSubheading(String subheading) {
        this.subheading = subheading;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ContentId getContentId() {
        return contentId;
    }

    public void setContentId(ContentId contentId) {
        this.contentId = contentId;
    }

    public List<RedFactImageBean> getPictures() {
        return pictures;
    }

    public void setPictures(List<RedFactImageBean> pictures) {
        this.pictures = pictures;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getAutor() {
        return autor;
    }
}
