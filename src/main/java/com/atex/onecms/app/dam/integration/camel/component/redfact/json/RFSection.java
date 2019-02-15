package com.atex.onecms.app.dam.integration.camel.component.redfact.json;

import com.google.gson.annotations.SerializedName;

public class RFSection {

    @SerializedName("Name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
