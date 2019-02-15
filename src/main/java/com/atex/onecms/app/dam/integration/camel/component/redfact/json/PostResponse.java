package com.atex.onecms.app.dam.integration.camel.component.redfact.json;

import java.util.List;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Response for a new or updated post.
 *
 * @author mnova
 */
public class PostResponse extends BaseResponse {

    @SerializedName("content")
    private WPObject content;

    @SerializedName("excerpt")
    private WPObject excerpt;

    @SerializedName("featured_media")
    private int featuredMedia;

    @SerializedName("sticky")
    private boolean sticky;

    @SerializedName("format")
    private WPArticleFormat format;

    @SerializedName("categories")
    private List<Integer> categories;

    @SerializedName("tags")
    private List<Integer> tags;

    public WPObject getContent() {
        return content;
    }

    public void setContent(final WPObject content) {
        this.content = content;
    }

    public WPObject getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(final WPObject excerpt) {
        this.excerpt = excerpt;
    }

    public int getFeaturedMedia() {
        return featuredMedia;
    }

    public void setFeaturedMedia(final int featuredMedia) {
        this.featuredMedia = featuredMedia;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(final boolean sticky) {
        this.sticky = sticky;
    }

    public WPArticleFormat getFormat() {
        return format;
    }

    public void setFormat(final WPArticleFormat format) {
        this.format = format;
    }

    public List<Integer> getCategories() {
        return categories;
    }

    public void setCategories(final List<Integer> categories) {
        this.categories = categories;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(final List<Integer> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("super", super.toString())
                      .add("content", content)
                      .add("excerpt", excerpt)
                      .add("featuredMedia", featuredMedia)
                      .add("sticky", sticky)
                      .add("format", format)
                      .add("categories", categories)
                      .add("tags", tags)
                      .toString();
    }
}
