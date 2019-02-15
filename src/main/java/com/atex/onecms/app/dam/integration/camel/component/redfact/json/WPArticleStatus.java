package com.atex.onecms.app.dam.integration.camel.component.redfact.json;

import com.google.gson.annotations.SerializedName;

/**
 * Article status
 *
 * @author mnova
 */
public enum WPArticleStatus {

    @SerializedName("publish")
    PUBLISH,

    @SerializedName("future")
    FUTURE,

    @SerializedName("draft")
    DRAFT,

    @SerializedName("pending")
    PENDING,

    @SerializedName("private")
    PRIVATE;
}
