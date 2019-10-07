package com.atex.plugins.redfact;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SLF4JLogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ApiTest
{

    private Log log = SLF4JLogFactory.getLog(ApiTest.class);

//    @Test
//    public void testGetMetaData() throws Exception {
//        String url = "https://qs.teckbote.de/atex/REST/backend/module/ar/metadata";
//        String result = sendGetJSON(url);
//        String expected = IOUtils.toString(this.getClass().getResourceAsStream("metadata-output.json"));
////        Assert.assertEquals(expected,result);
//        JSONAssert.assertEquals(expected,result,false);
//    }
//
//    @Test
//    public void testUpdateArticle() throws Exception {
//        String url = "https://qs.teckbote.de/atex/REST/backend/module/ar/bcb77816-a247-478b-b834-3609cb23331d";
//
//        RedFactArticleBean redFactArticleBean = getRedFactUpdateArticle();
//        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(redFactArticleBean.getParams());
//        String result = sendPost(url, entity);
//        System.out.println("result="+result);
//        String expected = IOUtils.toString(this.getClass().getResourceAsStream("updateArticle-output.json"));
//        Assert.assertEquals(expected,result);
//    }
//
//    @Test
//    public void testCreateArticle() throws Exception {
//      String url = "https://qs.teckbote.de/atex/REST/backend/module/ar";
//      String id = "bcb77816-a247-478b-b834-3609cb23331d";
//
//      String result = sendPost(url, new UrlEncodedFormEntity(getRedFactArticle(id).getParams()));
//
//      String getUrl = "https://qs.teckbote.de/atex/REST/backend/module/ar/"+id;
//
//	    String result2 = sendGetJSON(getUrl);
//	    System.out.println("result="+result);
//	    System.out.println("result2="+result2);
//    }
//
//    @Test
//    public void testGetArticle2() throws Exception {
//        String url = "https://qs.teckbote.de/atex/REST/backend/module/ar/7ed45796-6eba-4cce-b707-6e7bf1380124";
//
//        String result = sendGetJSON(url);
//        String expected = IOUtils.toString(this.getClass().getResourceAsStream("getArticle-output.json"));
//	      System.out.println("result="+result);
//        JSONAssert.assertEquals(expected,result,false);
//    }
//
//    @Test
//    public void testGetArticle() throws Exception {
//        String url = "https://qs.teckbote.de/atex/REST/backend/module/ar/bcb77816-a247-478b-b834-3609cb23331d";
//
//        String result = sendGetJSON(url);
//        String expected = IOUtils.toString(this.getClass().getResourceAsStream("getArticle-output.json"));
//        System.out.println("result="+result);
//        //JSONAssert.assertEquals(expected,result,false);
//    }
//
//    @Test
//    public void testGetArticle3() throws Exception {
//        String url = "https://qs.teckbote.de/atex/REST/backend/module/ar/4712";
//
//        String result = sendGetJSON(url);
//        System.out.println("result="+result);
//
//        String expected = IOUtils.toString(this.getClass().getResourceAsStream("getArticle-output.json"));
//        Assert.assertEquals(expected,result);
//    }
//
//    @Test
//    public void testDeleteArticle() throws Exception {
//        String url = "https://qs.teckbote.de/atex/REST/backend/module/ar/4712";
//
//        String result = sendDeleteJSON(url);
//        System.out.println("result="+result);
//        String expected = IOUtils.toString(this.getClass().getResourceAsStream("deleteArticle-output.json"));
//        JSONAssert.assertEquals(expected,result,false);
//    }
//
//    @Test
//    public void testSearchArticle() throws Exception {
//        String url = "https://qs.teckbote.de/atex/REST/backend/module/ar/search?q=AAA&limit=25";
//
//        String result = sendGetJSON(url);
//        String expected = IOUtils.toString(this.getClass().getResourceAsStream("searchArticle-output.json"));
//        JSONAssert.assertEquals(expected,result,false);
//    }
//
//    @Test
//    public void testSearchArticleWithFilter() throws Exception {
//        String url = "https://qs.teckbote.de/atex/REST/backend/module/ar/search?filter['name']=ZZZ";
//
//        String result = sendGetJSON(url);
//        String expected = IOUtils.toString(this.getClass().getResourceAsStream("searchArticleWithFilter-output.json"));
//        JSONAssert.assertEquals(expected,result,false);
//    }
//
//    private RedFactArticleBean getRedFactArticle(String id) {
//	    RedFactArticleBean ra = new RedFactArticleBean();
//	    ra.getParams().add(new BasicNameValuePair("catchline_atex", "nfy-test")); // fixed
//	    ra.getParams().add(new BasicNameValuePair("id_atex", id)); // onecms id (0-9,a-f)
//	    ra.getParams().add(new BasicNameValuePair("category", "2014")); // fixed
//	    ra.getParams().add(new BasicNameValuePair("status", "pu_all#wo_0")); // fixed
//	    ra.getParams().add(new BasicNameValuePair("name", "redFACT")); // ?
//	    ra.getParams().add(new BasicNameValuePair("editor_text", "Basetext")); // ?
//	    ra.getParams().add(new BasicNameValuePair("version_id", "88")); // number only
//
//      ra.getParams().add(new BasicNameValuePair("priority", "1")); // number only
//      ra.getParams().add(new BasicNameValuePair("topstory", "1")); // number only
//      ra.getParams().add(new BasicNameValuePair("valid_from", "1554415200")); // ok
//      ra.getParams().add(new BasicNameValuePair("valid_till", "1554415400")); // ok
//      ra.getParams().add(new BasicNameValuePair("lastchgdate", "1554405400")); // ok
//      ra.getParams().add(new BasicNameValuePair("description", "Heading")); // ok
//      ra.getParams().add(new BasicNameValuePair("subheadline", "sub heading")); // ok
//      ra.getParams().add(new BasicNameValuePair("editor_teaser", "Teaser")); // ok
//      ra.getParams().add(new BasicNameValuePair("author", "Autor")); // ok
//      return ra;
//    }
//
//    private RedFactArticleBean getRedFactUpdateArticle() {
//        RedFactArticleBean ra = new RedFactArticleBean();
//
////	    ra.getParams().add(new BasicNameValuePair("name", "redFACT"));
//
//        ra.getParams().add(new BasicNameValuePair("description", "Heading2")); // 200
////	      ra.getParams().add(new BasicNameValuePair("editor_text", "Basetext")); // 200
////        ra.getParams().add(new BasicNameValuePair("category", "200")); // 412
////        ra.getParams().add(new BasicNameValuePair("status", "pu_all#wo_0")); // 200
//
//        //        ra.getParams().add(new BasicNameValuePair("version_id", "ced77816-a247-478b-b834-3609cb23331"));
//
//        return ra;
//    }
//
//    private String sendPost(final String url, final UrlEncodedFormEntity entity) throws IOException {
//        try (final OutputStream stream = new ByteArrayOutputStream()) {
//
//            CloseableHttpClient httpclient = HttpClients.createDefault();
//
//            HttpPost method = new HttpPost(url);
//            method.setEntity(entity);
//
//            String result = "";
//            try (CloseableHttpResponse response = httpclient.execute(method)) {
//                System.out.println(response.getStatusLine());
//                HttpEntity responseEntity = response.getEntity();
//                // do something useful with the response body
//                result += IOUtils.toString(responseEntity.getContent());
//                // and ensure it is fully consumed
//                System.out.println("result="+result);
//                EntityUtils.consume(responseEntity);
//                return result;
//            }
//
//        }
//    }
//
//    public String sendGetJSON(final String url) throws IOException {
//        try (final OutputStream stream = new ByteArrayOutputStream()) {
//
//            CloseableHttpClient httpclient = HttpClients.createDefault();
//
//            HttpGet method = new HttpGet(url);
//
//            String result = "";
//            try (CloseableHttpResponse response = httpclient.execute(method)) {
//                System.out.println(response.getStatusLine());
//                HttpEntity responseEntity = response.getEntity();
//                // do something useful with the response body
//                result += IOUtils.toString(responseEntity.getContent());
//                // and ensure it is fully consumed
//                EntityUtils.consume(responseEntity);
//                return result;
//            }
//
//        }
//    }
//
//    public String sendDeleteJSON(final String url) throws IOException {
//        try (final OutputStream stream = new ByteArrayOutputStream()) {
//
//            CloseableHttpClient httpclient = HttpClients.createDefault();
//
//            HttpDelete method = new HttpDelete(url);
//
//            String result = "";
//            try (CloseableHttpResponse response = httpclient.execute(method)) {
//                System.out.println(response.getStatusLine());
//                HttpEntity responseEntity = response.getEntity();
//                // do something useful with the response body
//                result += IOUtils.toString(responseEntity.getContent());
//                // and ensure it is fully consumed
//                EntityUtils.consume(responseEntity);
//                return result;
//            }
//
//        }
//    }
}
