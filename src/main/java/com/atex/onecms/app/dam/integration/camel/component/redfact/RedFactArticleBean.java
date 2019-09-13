package com.atex.onecms.app.dam.integration.camel.component.redfact;

import java.util.ArrayList;
import java.util.List;

import com.atex.onecms.app.dam.integration.camel.component.redfact.json.*;
import com.atex.onecms.content.ContentId;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import org.apache.http.NameValuePair;

/**
 * Bean representing a redfact article.
 *
 * @author leighfwarren
 */
public class RedFactArticleBean {

    private List<NameValuePair> params = new ArrayList<>();


    public List<NameValuePair> getParams() {
        return params;
    }

    public void setParams(List<NameValuePair> params) {
        this.params = params;
    }
}
