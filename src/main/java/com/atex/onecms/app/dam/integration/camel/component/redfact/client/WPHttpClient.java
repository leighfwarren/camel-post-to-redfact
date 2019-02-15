package com.atex.onecms.app.dam.integration.camel.component.redfact.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Custom HTTP client.
 *
 * @author mnova
 */
public class WPHttpClient {

    private static final Logger LOGGER = Logger.getLogger(WPHttpClient.class.getName());

    private int connectionTimeout = 5 * 1000;
    private int socketTimeout = 60 * 1000;
    private String proxyHost = null;
    private int proxyPort = 8080;

    private boolean preEmptiveAuthorization;
    private boolean sslTrustAll;

    private final Gson gson;

    private transient HttpClientBuilder clientBuilder;

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(final int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(final int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public boolean isSslTrustAll() {
        return sslTrustAll;
    }

    public void setSslTrustAll(final boolean sslTrustAll) {
        this.sslTrustAll = sslTrustAll;
    }

    public WPHttpClient() {
        this(new GsonBuilder().create());
    }

    public WPHttpClient(final Gson gson) {
        clientBuilder = HttpClientBuilder.create();
        clientBuilder.useSystemProperties();

        preEmptiveAuthorization = false;
        sslTrustAll = false;

        this.gson = gson;
    }



    public boolean isPreEmptiveAuthorization() {
        return preEmptiveAuthorization;
    }

    public void setPreEmptiveAuthorization(final boolean preEmptiveAuthorization) {
        this.preEmptiveAuthorization = preEmptiveAuthorization;
    }

    public void setCredentials(final Credentials credentials) {

        log(Level.INFO, "setCredentials " + credentials);

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, checkNotNull(credentials));
        clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
    }

    private AuthCache createAuthCache(final URI uri) {

        // Create AuthCache instance
        final AuthCache authCache = new BasicAuthCache();

        // Generate BASIC scheme object and add it to the local auth cache
        final BasicScheme basicAuth = new BasicScheme();

        final HttpHost targetHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        authCache.put(targetHost, basicAuth);

        return authCache;
    }

    public int doGet(final String url, final OutputStream outputStream) throws IOException {
        final HttpGet get = new HttpGet(url);
        return execute(get, outputStream);
    }

    public <T> T doGet(final String url, final HttpResponseHandler<T> handler) throws IOException {
        return doGet(url, null, handler);
    }

    public <T> T doGet(final String url, final Map<String, String> headers, final HttpResponseHandler<T> handler) throws IOException {
        final HttpGet get = new HttpGet(url);
        if (headers != null) {
            for (final Entry<String, String> entry : headers.entrySet()) {
                get.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return execute(get, handler);
    }

    public int doPostForm(final String url, final Map<String, String> values, final OutputStream outputStream) throws IOException {

        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        for (Entry<String, String> entry : values.entrySet()) {
            urlParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            log(Level.FINE, "parameter " + entry.getKey() + " value " + entry.getValue());
        }
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        return execute(post, outputStream);
    }

    public int doPostJSON(final String url, final Object jsonContent, final OutputStream outputStream) throws IOException {

        final Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        return doPostJSON(url, headers, jsonContent, outputStream);
    }

    public int doPostJSON(final String url, final Map<String, String> headers, final Object jsonContent, final OutputStream outputStream) throws IOException {

        HttpPost post = new HttpPost(url);
        if (headers != null) {
            if (!headers.containsKey("Content-Type")) {
                headers.put("Content-Type", "application/json");
            }
            for (Entry<String, String> entry : headers.entrySet()) {
                post.setHeader(entry.getKey(), entry.getValue());
            }
        }

        final String jsonString = gson.toJson(jsonContent);

        post.setEntity(new StringEntity(jsonString));
        return execute(post, outputStream);
    }

    public int execute(final HttpRequestBase request, final OutputStream outputStream) throws IOException {
        return execute(request, new HttpResponseHandler<Integer>() {

            @Override
            public Integer apply(final HttpResponse response) throws IOException {
                final HttpEntity responseEntity = response.getEntity();

                final int statusCode;
                try (final InputStream in = responseEntity.getContent()) {

                    statusCode = response.getStatusLine().getStatusCode();

                    try {
                        final byte[] content = EntityUtils.toByteArray(responseEntity);
                        if (content != null) {
                            IOUtils.write(content, outputStream);
                        }
                    } finally {
                        IOUtils.closeQuietly(outputStream);
                    }
                } finally {
                    EntityUtils.consumeQuietly(responseEntity);
                }
                return statusCode;
            }

        });
    }

    public <T> T execute(final HttpRequestBase request, final HttpResponseHandler<T> handler) throws IOException {

        log(Level.INFO, "calling url " + checkNotNull(request).getURI().toString());

        final long startTime = System.currentTimeMillis();

        HttpEntity responseEntity = null;

        try (final CloseableHttpClient httpClient = createHttpClient()) {
            final HttpResponse response;

            if (isPreEmptiveAuthorization()) {
                final AuthCache authCache = createAuthCache(request.getURI());

                // Add AuthCache to the execution context
                final HttpClientContext localContext = HttpClientContext.create();
                localContext.setAuthCache(authCache);

                response = httpClient.execute(request, localContext);
            } else {
                response = httpClient.execute(request);
            }

            responseEntity = response.getEntity();

            final int statusCode = response.getStatusLine().getStatusCode();

            final long endTime = System.currentTimeMillis();
            log(Level.INFO, "status code " + checkNotNull(request).getURI().toString() + " (" + statusCode + ") in " + Long.toString(endTime - startTime) + " ms");

            return handler.apply(response);

        } finally {
            EntityUtils.consumeQuietly(responseEntity);

            final long endTime = System.currentTimeMillis();
            log(Level.INFO, "completed in " + Long.toString(endTime - startTime) + " ms");
        }

    }

    public CloseableHttpClient createHttpClient() {

        log(Level.FINE, "connectionTimeout: " + connectionTimeout + " - socketTimeout: " + socketTimeout);

        final RequestConfig.Builder requestBuilder = RequestConfig
                .custom()
                .setConnectTimeout(connectionTimeout)
                .setSocketTimeout(socketTimeout);
        clientBuilder.setDefaultRequestConfig(requestBuilder.build());

        if (!Strings.isNullOrEmpty(proxyHost)) {
            log(Level.FINE, "Setting proxy for this client " + proxyHost + ":" + proxyPort);
            clientBuilder.setProxy(new HttpHost(proxyHost, proxyPort));
        }

        if (isSslTrustAll()) {
            try {
                log(Level.FINE, "force trust all ssl");
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
                clientBuilder.setSslcontext(ctx);
            } catch (Exception e) {
                log(Level.SEVERE, e.getMessage(), e);
                throw new RuntimeException(e);

            }
        }

        return clientBuilder.build();
    }

    private void log(final Level level, final String msg) {
        final long threadId = Thread.currentThread().getId();
        LOGGER.log(level, "[" + threadId + "] " + msg);
    }

    private void log(final Level level, final String msg, final Throwable thrown) {
        final long threadId = Thread.currentThread().getId();
        LOGGER.log(level, "[" + threadId + "] " + msg, thrown);
    }

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(final java.security.cert.X509Certificate[] x509Certificates, final String s)
                throws java.security.cert.CertificateException {

        }

        @Override
        public void checkServerTrusted(final java.security.cert.X509Certificate[] x509Certificates, final String s)
                throws java.security.cert.CertificateException {

        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    public interface HttpResponseHandler<T> {
        T apply(final HttpResponse response) throws IOException;
    }

}
