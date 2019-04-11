package com.atex.onecms.app.dam.integration.camel.component.redfact.camel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import com.atex.onecms.app.dam.integration.camel.component.redfact.RedFactArticleBean;
import com.atex.onecms.app.dam.integration.camel.component.redfact.json.GSONCustomExclusionStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import com.atex.onecms.app.dam.integration.camel.component.redfact.RedfactConfig;
import com.atex.onecms.app.dam.integration.camel.component.redfact.json.DateDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateDeserializer())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .setExclusionStrategies(new GSONCustomExclusionStrategy())
            .create();

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

        String articleJson = gson.toJson(articleBean);

        String url = RedfactConfig.getInstance().getApiUrl();
        String response = sendJSON(url, articleJson);
        log.info("response: " + response);
        return response;
    }

    private String sendJSON(final String url, final String jsonReq) throws IOException {
        try (final OutputStream stream = new ByteArrayOutputStream()) {

            CloseableHttpClient httpclient = HttpClients.createDefault();

            HttpPost method = new HttpPost(url);
            StringEntity entity = new StringEntity(jsonReq, "UTF-8");
            method.setEntity(entity);

            try (CloseableHttpResponse response = httpclient.execute(method)) {
                System.out.println(response.getStatusLine());
                HttpEntity responseEntity = response.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(responseEntity);
                return responseEntity.toString();
            }

        }
    }

}
