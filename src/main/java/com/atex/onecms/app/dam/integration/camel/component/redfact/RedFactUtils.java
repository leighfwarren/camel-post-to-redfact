package com.atex.onecms.app.dam.integration.camel.component.redfact;

import com.atex.onecms.app.dam.standard.aspects.OneArticleBean;
import com.atex.onecms.content.ContentResult;
import com.atex.onecms.content.callback.CallbackException;
import com.atex.onecms.content.mapping.ContentComposer;
import com.atex.onecms.content.mapping.Context;
import com.atex.onecms.content.mapping.Request;
import com.atex.onecms.content.metadata.MetadataInfo;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.polopoly.metadata.Dimension;
import com.polopoly.metadata.Entity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class RedFactUtils {

    private static Logger log = LoggerFactory.getLogger(RedFactUtils.class);

    public List<NameValuePair> convert(final ContentResult<OneArticleBean> source)
            throws CallbackException {

        final OneArticleBean damArticle = source.getContent().getContentData();

        List<NameValuePair> params = getNameValuePairs(source, damArticle);

        return params;
    }

    public List<NameValuePair> getNameValuePairs(ContentResult<OneArticleBean> source, OneArticleBean damArticle) {
        List<NameValuePair> params = new ArrayList<>();
        // fixed params
        MetadataInfo metadataInfo = (MetadataInfo)source.getContent().getAspect("atex.Metadata").getData();
        Dimension ressort1Dimension = metadataInfo.getMetadata().getDimensionById("dimension.ressort1");
        if (ressort1Dimension != null && ressort1Dimension.getEntities() != null && ressort1Dimension.getEntities().size() > 0) {
            Entity entity = (Entity)ressort1Dimension.getEntities().get(0);
            params.add(new BasicNameValuePair("catchline_atex", entity.getName())); // fixed
        }
        params.add(new BasicNameValuePair("category", "2014")); // fixed
        params.add(new BasicNameValuePair("status", "pu_all#wo_0")); // fixed
        // end fixed
        params.add(new BasicNameValuePair("name",damArticle.getHeadline().getText()));
//        params.add(new BasicNameValuePair("subheadline",redFactArticleBean.?????()));
        params.add(new BasicNameValuePair("editor_teaser",damArticle.getLead().getText()));
        params.add(new BasicNameValuePair("editor_text",damArticle.getBody().getText()));
        params.add(new BasicNameValuePair("author",damArticle.getAuthor()));
        params.add(new BasicNameValuePair("id_atex",source.getContentId().getContentId().getKey()));
//        params.add(new BasicNameValuePair("version_id",source.getContentId().getContentId().getDelegationId())); // number only
        params.add(new BasicNameValuePair("priority",Integer.toString(calculatePriority(damArticle.getName(), damArticle.getWords()))));
        String topStory = getTopStory(damArticle);
        params.add(new BasicNameValuePair("topstory",topStory));
        com.atex.onecms.app.dam.types.TimeState t = damArticle.getTimeState();
        if (t != null) {
            Long offtime = t.getOfftime();
            Long ontime = t.getOntime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (offtime != null && offtime != 0) {
                LocalDateTime dt = LocalDateTime.ofEpochSecond(offtime/1000,0,ZoneOffset.UTC);
                params.add(new BasicNameValuePair("valid_from",formatter.format(dt)));
            }
            if (ontime != null && ontime != 0) {
                LocalDateTime dt = LocalDateTime.ofEpochSecond(offtime/1000,0,ZoneOffset.UTC);
                params.add(new BasicNameValuePair("valid_till",formatter.format(dt)));
            }
        }
        params.add(new BasicNameValuePair("lastchgdate",getLastModifiedDate(damArticle)));
        return params;
    }

    private String getTopStory(OneArticleBean damArticle) {
        boolean h1 = damArticle.getName().startsWith("H1");
        return (h1) ? "1" : "0";
    }

    private String getLastModifiedDate(OneArticleBean damArticle) {
        try {
            Method getLastAmendedTime = damArticle.getClass().getMethod("getLastAmendedTime");
            if (getLastAmendedTime == null) {
                log.warn("Unable to get getLastAmendedTime method, on article.  No modified time available");
                return "";
            }
            Object modificationdate = getLastAmendedTime.invoke(damArticle);
            if (modificationdate instanceof Long && ((Long)modificationdate) > 0) {
                LocalDateTime dt = LocalDateTime.ofEpochSecond((Long)modificationdate,0, ZoneOffset.UTC);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss");
                return formatter.format(dt);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.debug("unable to get modification date",e);
        }
        return "";
    }

    private int calculatePriority(String name, int wordCount) {
        if (name.startsWith("H4")) return 5;
        if (wordCount < 201) return 4;
        if (wordCount < 401) return 3;
        if (wordCount < 601) return 2;
        return 1;
    }

    private String truncateWords(final String value, int blankCount, final String suffix) {
        if (!Strings.isNullOrEmpty(value)) {
            final StringBuilder sb = new StringBuilder();
            int spaceCount = 0;
            for (final String text : Splitter.on(" ").split(value.trim())) {
                if (Strings.isNullOrEmpty(text)) {
                    sb.append(" ");
                    continue;
                }
                sb.append(text);
                if (spaceCount++ >= blankCount) {
                    break;
                }
                sb.append(" ");
            }
            String text = sb.toString().trim();
            if (text.length() < (value.trim()).length()) {
                text += suffix;
            }
            return text;
        }
        return value;
    }


}
