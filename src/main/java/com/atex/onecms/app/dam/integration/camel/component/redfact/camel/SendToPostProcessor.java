package com.atex.onecms.app.dam.integration.camel.component.redfact.camel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.atex.onecms.app.dam.integration.camel.component.redfact.RedFactArticleBean;
import com.atex.onecms.app.dam.integration.camel.component.redfact.json.GSONCustomExclusionStrategy;
import com.atex.onecms.app.dam.standard.aspects.OneArticleBean;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import com.atex.onecms.app.dam.integration.camel.component.redfact.RedfactConfig;
import com.atex.onecms.app.dam.integration.camel.component.redfact.json.DateDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Simple processor that send content to Red Fact.
 *
 * @author mnova
 */
public class SendToPostProcessor implements Processor {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void process(final Exchange exchange) throws Exception {

        log.info("SendToPostProcessor - start work");

        try {
            final Message msg = exchange.getIn();
            RedFactArticleBean redFactArticleBean = msg.getBody(RedFactArticleBean.class);

            String response = sendArticleToRedFact(redFactArticleBean);
            exchange.getOut().setBody(response);


        } finally {
            log.info("SendToPostProcessor - end work");
        }
    }
    
    private String sendArticleToRedFact(final RedFactArticleBean articleBean) throws IOException {

        String url = RedfactConfig.getInstance().getApiUrl();
        CloseableHttpResponse response = sendForm(url, articleBean.getParams());
        log.info("response: " + response.getEntity().toString());
        if (response.getStatusLine().getStatusCode() == 412) {
            log.info("create failed, sending update");
            // either article failed or needs exists
            // try to update instead
            String updateUrl = url;
            if (!updateUrl.endsWith("/")) updateUrl += "/";
            for (NameValuePair param : articleBean.getParams()) {
                if (param.getName().equals("id_atex")) {
                    updateUrl += param.getValue();
                    break;
                }
            }
            log.info("sending update url:"+updateUrl);
            response = sendForm(updateUrl, articleBean.getParams());
            log.info("update response: " + response.getEntity().toString());
        }
        log.info("status = "+response.getStatusLine().getStatusCode());
        return response.getEntity().toString();
    }

    private CloseableHttpResponse sendForm(final String url, final List<NameValuePair> params) throws IOException {
        try (final OutputStream stream = new ByteArrayOutputStream()) {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost method = new HttpPost(url);
            method.setEntity(new UrlEncodedFormEntity(params));
            try (CloseableHttpResponse response = httpclient.execute(method)) {
                System.out.println(response.getStatusLine());
                HttpEntity responseEntity = response.getEntity();
                EntityUtils.consume(responseEntity);
                return response;
            }
        }
    }

}
