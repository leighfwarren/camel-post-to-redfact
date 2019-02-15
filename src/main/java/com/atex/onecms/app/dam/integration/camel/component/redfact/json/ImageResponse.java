package com.atex.onecms.app.dam.integration.camel.component.redfact.json;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Response for a new or updated image.
 *
 * @author mnova
 */
public class ImageResponse extends BaseResponse {

    @SerializedName("mime_type")
    private String mimeType;

    @SerializedName("post")
    private String post;

    @SerializedName("source_url")
    private String source_url;

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    public String getPost() {
        return post;
    }

    public void setPost(final String post) {
        this.post = post;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(final String source_url) {
        this.source_url = source_url;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("super", super.toString())
                      .add("mimeType", mimeType)
                      .add("post", post)
                      .add("source_url", source_url)
                      .toString();
    }

}
