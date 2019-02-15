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

import com.atex.onecms.app.dam.integration.camel.component.redfact.RedFactImageBean;
import com.atex.onecms.app.dam.integration.camel.component.redfact.RedFactProperties;
import com.atex.onecms.app.dam.integration.camel.component.redfact.client.WPHttpClient;
import com.atex.onecms.app.dam.integration.camel.component.redfact.json.DateDeserializer;
import com.atex.onecms.app.dam.integration.camel.component.redfact.json.PostResponse;
import com.atex.onecms.content.ContentId;
import com.atex.onecms.content.ContentManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.polopoly.user.server.Caller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public SendToPostProcessor() {
    }

    @Override
    public void process(final Exchange exchange) throws Exception {

        log.error("TEST");
        log.info("SendToPostProcessor - start work");

        try {
            final Message msg = exchange.getIn();

            final Caller caller = msg.getHeader("caller", Caller.class);
            final ContentId contentId = msg.getHeader("contentId", ContentId.class);
            final ContentManager contentManager = msg.getHeader("contentManager", ContentManager.class);
            RedFactArticleBean redFactArticleBean = msg.getBody(RedFactArticleBean.class);

            final PostResponse response = sendArticleToRedFact(redFactArticleBean);
            exchange.getOut().setBody(response);


        } finally {
            log.info("SendToPostProcessor - end work");
        }
    }
    
    private PostResponse sendArticleToRedFact(final RedFactArticleBean articleBean) throws IOException {

        String articleJson = gson.toJson(articleBean);

        RedFactProperties properties = RedFactProperties.getInstance();
        String url = properties.getAPIUrl();
        final PostResponse response = sendJSON(url, articleJson, PostResponse.class);
        log.info("response: " + response.toString());
        return response;
    }
        
    private ByteArrayOutputStream getImageBinaryData(final RedFactImageBean imageBean) throws IOException {
        RedFactProperties properties = RedFactProperties.getInstance();
        String url = imageBean.getUrl();
        if (url.startsWith("/")) {
            url = properties.getOneCMSImagePrefix() + url;
        }

        log.info("Getting image from " + url);

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final WPHttpClient httpClient = new WPHttpClient(gson);
        final int statusCode = httpClient.doGet(url, stream);
        if (statusCode != 200) {
            log.error( "cannot get image from " + url + " due to " + statusCode + " status code");
            return null;
        }
        return stream;
    }

    private <T> T sendJSON(final String url, final Object jsonReq, final Class<T> expectedResponse) throws IOException {
        try (final OutputStream stream = new ByteArrayOutputStream()) {

            final WPHttpClient httpClient = new WPHttpClient(gson);

            final int statusCode = httpClient.doPostJSON(url, jsonReq, stream);
            if (statusOk(statusCode)) {
                final String responseBody = stream.toString();
                log.debug("Got " + responseBody);
                final T response = gson.fromJson(responseBody, expectedResponse);
                return response;
            } else {
                log.error("failed to call " + url + ": " + statusCode);
                log.error("response: " + stream.toString());
                throw new IOException("Post failed with " + statusCode);
            }
        }
    }

    private boolean statusOk(final int statusCode) {
        if (statusCode == 200) {
            return true;
        } else if (statusCode == 201) {
            return true;
        }
        return false;
    }

}
