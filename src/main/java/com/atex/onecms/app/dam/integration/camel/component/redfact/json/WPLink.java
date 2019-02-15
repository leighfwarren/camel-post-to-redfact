package com.atex.onecms.app.dam.integration.camel.component.redfact.json;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * A link
 *
 * @author mnova
 */
public class WPLink {

    @SerializedName("taxonomy")
    private String taxonomy;

    @SerializedName("embeddable")
    private Boolean embeddable;

    @SerializedName("href")
    private String href;

    public String getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(final String taxonomy) {
        this.taxonomy = taxonomy;
    }

    public Boolean getEmbeddable() {
        return embeddable;
    }

    public void setEmbeddable(final Boolean embeddable) {
        this.embeddable = embeddable;
    }

    public String getHref() {
        return href;
    }

    public void setHref(final String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("taxonomy", taxonomy)
                      .add("embeddable", embeddable)
                      .add("href", href)
                      .toString();
    }
}
