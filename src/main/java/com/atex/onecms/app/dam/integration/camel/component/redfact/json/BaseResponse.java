package com.atex.onecms.app.dam.integration.camel.component.redfact.json;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Base response.
 *
 * @author mnova
 */
public abstract class BaseResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("date")
    private Date date;

    @SerializedName("date_gmt")
    private Date dateGMT;

    @SerializedName("guid")
    private WPObject guid;

    @SerializedName("modified")
    private Date modified;

    @SerializedName("modified_gmt")
    private Date modifiedGMT;

    @SerializedName("password")
    private String password;

    @SerializedName("slug")
    private String slug;

    @SerializedName("status")
    private String status;

    @SerializedName("link")
    private String link;

    @SerializedName("title")
    private WPObject title;

    @SerializedName("author")
    private int author;

    @SerializedName("comment_status")
    private WPCommentStatus commentStatus;

    @SerializedName("ping_status")
    private WPPingStatus pingStatus;

    @SerializedName("_links")
    private Map<String, List<WPLink>> links;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public Date getDateGMT() {
        return dateGMT;
    }

    public void setDateGMT(final Date dateGMT) {
        this.dateGMT = dateGMT;
    }

    public WPObject getGuid() {
        return guid;
    }

    public void setGuid(final WPObject guid) {
        this.guid = guid;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(final Date modified) {
        this.modified = modified;
    }

    public Date getModifiedGMT() {
        return modifiedGMT;
    }

    public void setModifiedGMT(final Date modifiedGMT) {
        this.modifiedGMT = modifiedGMT;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(final String slug) {
        this.slug = slug;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public WPObject getTitle() {
        return title;
    }

    public void setTitle(final WPObject title) {
        this.title = title;
    }

    public int getAuthor() {
        return author;
    }

    public void setAuthor(final int author) {
        this.author = author;
    }

    public WPCommentStatus getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(final WPCommentStatus commentStatus) {
        this.commentStatus = commentStatus;
    }

    public WPPingStatus getPingStatus() {
        return pingStatus;
    }

    public void setPingStatus(final WPPingStatus pingStatus) {
        this.pingStatus = pingStatus;
    }

    public Map<String, List<WPLink>> getLinks() {
        return links;
    }

    public void setLinks(final Map<String, List<WPLink>> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("id", id)
                      .add("date", date)
                      .add("dateGMT", dateGMT)
                      .add("guid", guid)
                      .add("modified", modified)
                      .add("modifiedGMT", modifiedGMT)
                      .add("password", password)
                      .add("slug", slug)
                      .add("status", status)
                      .add("link", link)
                      .add("title", title)
                      .add("author", author)
                      .add("commentStatus", commentStatus)
                      .add("pingStatus", pingStatus)
                      .add("links", links)
                      .toString();
    }
}
