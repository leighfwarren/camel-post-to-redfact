package com.atex.onecms.app.dam.integration.camel.component.redfact.json;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class RFInternetAttr {

    @SerializedName("TopStory")
    private String topStory;

    @SerializedName("Prio")
    private int prio;

    @SerializedName("FactBox")
    private String factBox;

    @SerializedName("WebBegin")
    private LocalDateTime webBegin;

    @SerializedName("WebEnd")
    private LocalDateTime webEnd;

    @SerializedName("Sections")
    private List<RFSection> sections;

    public String getTopStory() {
        return topStory;
    }

    public void setTopStory(String topStory) {
        this.topStory = topStory;
    }

    public int getPrio() {
        return prio;
    }

    public void setPrio(int prio) {
        this.prio = prio;
    }

    public String getFactBox() {
        return factBox;
    }

    public void setFactBox(String factBox) {
        this.factBox = factBox;
    }

    public LocalDateTime getWebBegin() {
        return webBegin;
    }

    public void setWebBegin(LocalDateTime webBegin) {
        this.webBegin = webBegin;
    }

    public LocalDateTime getWebEnd() {
        return webEnd;
    }

    public void setWebEnd(LocalDateTime webEnd) {
        this.webEnd = webEnd;
    }

    public List<RFSection> getSections() {
        return sections;
    }

    public void setSections(List<RFSection> sections) {
        this.sections = sections;
    }
}
