package com.atex.onecms.app.dam.integration.camel.component.redfact.json;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Simple wp object.
 *
 * @author mnova
 */
public class WPObject {

    @SerializedName("raw")
    private String raw;

    @SerializedName("rendered")
    private String rendered;

    public String getRaw() {
        return raw;
    }

    public void setRaw(final String raw) {
        this.raw = raw;
    }

    public String getRendered() {
        return rendered;
    }

    public void setRendered(final String rendered) {
        this.rendered = rendered;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("raw", raw)
                      .add("rendered", rendered)
                      .toString();
    }
}
