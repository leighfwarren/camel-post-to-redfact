package com.atex.onecms.app.dam.integration.camel.component.redfact.json;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class RFMetaData {

    @SerializedName("Id")
    private String id;

    @SerializedName("Version")
    private String version;

    @SerializedName("Name")
    private String name;

    @SerializedName("CreatedDate")
    private Date createdDate;

    @SerializedName("LastUpdate")
    private Date lastUpdate;
    private int factBox;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setFactBox(int factBox) {
        this.factBox = factBox;
    }

    public int getFactBox() {
        return factBox;
    }
}
