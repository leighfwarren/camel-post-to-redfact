package com.atex.onecms.app.dam.integration.camel.component.redfact.camel;

import com.atex.onecms.app.dam.standard.aspects.CustomArticleBean;
import com.atex.plugins.structured.text.StructuredText;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class RedFactExport {
    public void export() {
        CustomArticleBean bean = new CustomArticleBean();
        String onecmsId = "";
        String version = "";

        JsonObject json = new JsonObject();
        JsonObject metaData = new JsonObject();
        json.add("MetaData", metaData);
        metaData.addProperty("Id", onecmsId);
        metaData.addProperty("Version", version);
        String name = bean.getName();
        StructuredText body = bean.getBody();
        metaData.addProperty("Name", name);
        metaData.addProperty("CreatedDate", "");
        metaData.addProperty("LastUpdate", bean.getName());
        metaData.addProperty("FactBox", 0);
        JsonObject internetAttr = new JsonObject();
        json.add("InternetAttr", internetAttr);
        com.atex.onecms.app.dam.types.TimeState t = bean.getTimeState();
        if (t != null) {
            Long offtime = t.getOfftime();
            Long ontime = t.getOfftime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss");
            if (offtime != null && offtime != 0) {
                LocalDateTime dt = LocalDateTime.ofEpochSecond(offtime,0, ZoneOffset.UTC);
                internetAttr.addProperty("WebBegin", dt.format(formatter));
            }
            if (ontime != null && ontime != 0) {
                LocalDateTime dt = LocalDateTime.ofEpochSecond(ontime,0, ZoneOffset.UTC);
                internetAttr.addProperty("WebEnd", dt.format(formatter));
            }
        }

        internetAttr.addProperty("Prio", calculatePriority(name,bean.getWords()));
        internetAttr.addProperty("TopStory", calculateTopStory(name));
        JsonObject sections = new JsonObject();
        internetAttr.add("Sections", sections);
        JsonObject section = new JsonObject();
        sections.add("Section", section);
        section.addProperty("Name", bean.getSection());
    }

    private int calculatePriority(String name, int wordCount) {
        if (name.startsWith("H4")) return 5;
        if (wordCount < 201) return 4;
        if (wordCount < 401) return 3;
        if (wordCount < 601) return 2;
        return 1;
    }

    private int calculateTopStory(String name) {
        if (name.startsWith("H1"))
            return 1;
        else
            return 0;
    }
}
