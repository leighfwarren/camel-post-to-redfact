package com.atex.onecms.app.dam.integration.camel.component.redfact;

import com.atex.onecms.annotations.Composer;
import com.atex.onecms.app.dam.integration.camel.component.redfact.json.*;
import com.atex.onecms.app.dam.standard.aspects.OneArticleBean;
import com.atex.onecms.content.ContentId;
import com.atex.onecms.content.ContentManager;
import com.atex.onecms.content.ContentResult;
import com.atex.onecms.content.ContentVersionId;
import com.atex.onecms.content.Status;
import com.atex.onecms.content.Subject;
import com.atex.onecms.content.callback.CallbackException;
import com.atex.onecms.content.mapping.ContentComposer;
import com.atex.onecms.content.mapping.Context;
import com.atex.onecms.content.mapping.Request;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Composer used to create a {@link RedFactArticleBean} from a {@link OneArticleBean}.
 *
 * @author mnova
 */
@Composer(
        variant = "com.atex.onecms.app.dam.integration.camel.component.redfact.article",
        type = "atex.onecms.article",
        variantId = "com.atex.onecms.app.dam.integration.camel.component.redfact.article.variantconfig")
public class DamArticleToRedFactArticleComposer implements ContentComposer<OneArticleBean, RedFactArticleBean, Object> {

    private static Logger log = LoggerFactory.getLogger(DamArticleToRedFactArticleComposer.class);

    @Override
    public ContentResult<RedFactArticleBean> compose(final ContentResult<OneArticleBean> source,
                                                     final String variant,
                                                     final Request request,
                                                     final Context<Object> context)
            throws CallbackException {

        final OneArticleBean damArticle = source.getContent().getContentData();

        final RedFactArticleBean redFactArticleBean = new RedFactArticleBean();

        List<NameValuePair> params = new ArrayList<>();
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddZHH:mm:ss");
            if (offtime != null && offtime != 0) {
                LocalDateTime dt = LocalDateTime.ofEpochSecond(offtime,0, ZoneOffset.UTC);
                params.add(new BasicNameValuePair("valid_from",formatter.format(dt)));
            }
            if (ontime != null && ontime != 0) {
                LocalDateTime dt = LocalDateTime.ofEpochSecond(ontime,0, ZoneOffset.UTC);
                params.add(new BasicNameValuePair("valid_till",formatter.format(dt)));
            }
        }
        params.add(new BasicNameValuePair("lastchgdate",getLastModifiedDate(damArticle)));
        params.add(new BasicNameValuePair("catchline_atex",damArticle.getSection()));
        redFactArticleBean.setParams(params);

        return new ContentResult<>(source, redFactArticleBean);
    }

    public String getTopStory(OneArticleBean damArticle) {
        boolean h1 = damArticle.getName().startsWith("H1");
        return (h1) ? "1" : "0";
    }

    private RFMetaData getMetaData(OneArticleBean damArticle) {
        RFMetaData metaData = new RFMetaData();

        metaData.setName(damArticle.getName());
        metaData.setFactBox(0);

        Date creationdate = damArticle.getCreationdate();

        if (creationdate != null) {
            metaData.setCreatedDate(creationdate);
        }


        return metaData;
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
