package com.atex.onecms.app.dam.integration.camel.component.redfact;

import com.atex.onecms.app.dam.integration.camel.component.redfact.json.GSONCustomExclude;
import com.atex.onecms.content.ContentId;
import com.google.gson.annotations.SerializedName;

/**
 * Bean representing a redfact image.
 *
 * @author leighfwarren
 */
public class RedFactImageBean {

    public static final String VARIANTNAME = "com.atex.onecms.app.dam.integration.camel.component.redfact.image";

    @GSONCustomExclude
    private ContentId contentId;

    @SerializedName("PicTitle")
    private String picTitle;

    @SerializedName("PicInfo")
    private String picInfo;

    @SerializedName("Pathname")
    private String pathname;

    @SerializedName("Url")
    private String url;

    @SerializedName("Id")
    private String id;

    public String getPicTitle() {
        return picTitle;
    }

    public void setPicTitle(String picTitle) {
        this.picTitle = picTitle;
    }

    public String getPicInfo() {
        return picInfo;
    }

    public void setPicInfo(String picInfo) {
        this.picInfo = picInfo;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String version) {
        this.url = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContentId getContentId() {
        return contentId;
    }

    public void setContentId(ContentId contentId) {
        this.contentId = contentId;
    }
}
