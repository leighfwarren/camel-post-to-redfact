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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

        final RedFactArticleBean articleBean = new RedFactArticleBean();

        articleBean.setContentId(source.getContentId().getContentId());

        String headline = damArticle.getHeadline().getText();
        String body = damArticle.getBody().getText();

        articleBean.setHeading(headline);
        articleBean.setBaseText(body);
        articleBean.setInternetAttr(getInternetAddr(damArticle));
        articleBean.setMetadata(getMetaData(damArticle));
        articleBean.setTeaser(damArticle.getLead().getText());
        articleBean.setPictures(new ArrayList<>());
        articleBean.setAutor(damArticle.getAuthor());

        // try to get the associated images.
        final List<ContentId> images = damArticle.getImages();
        if (images != null) {
            for (final ContentId contentId : images) {
                final RedFactImageBean image = getImageVariant(context.getContentManager(), contentId);
                if (image != null) {
                    articleBean.getPictures().add(image);
                }
            }
        }

        return new ContentResult<>(source, articleBean);
    }

    private RFMetaData getMetaData(OneArticleBean damArticle) {
        RFMetaData metaData = new RFMetaData();

        metaData.setName(damArticle.getName());
        metaData.setFactBox(0);

        Date creationdate = damArticle.getCreationdate();

        if (creationdate != null) {
            metaData.setCreatedDate(creationdate);
        }

        try {
            Method getLastAmendedTime = damArticle.getClass().getMethod("getLastAmendedTime");
            Object modificationdate = getLastAmendedTime.invoke(damArticle);
            if (modificationdate instanceof Long && ((Long)modificationdate) > 0) {
                Date dt = new Date((Long) modificationdate);
                metaData.setLastUpdate(dt);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.debug("unable to get modification date",e);
        }

        return metaData;
    }

    private RFInternetAttr getInternetAddr(OneArticleBean damArticle) {
        RFInternetAttr internetAttr = new RFInternetAttr();
        internetAttr.setFactBox("0");
        boolean h1 = damArticle.getName().startsWith("H1");
        String topStory = (h1) ? "1":"0";
        internetAttr.setTopStory(topStory);
        internetAttr.setPrio(calculatePriority(damArticle.getName(), damArticle.getWords()));
        com.atex.onecms.app.dam.types.TimeState t = damArticle.getTimeState();
        if (t != null) {
            Long offtime = t.getOfftime();
            Long ontime = t.getOfftime();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss");
            if (offtime != null && offtime != 0) {
                LocalDateTime dt = LocalDateTime.ofEpochSecond(offtime,0, ZoneOffset.UTC);
                internetAttr.setWebBegin(dt);
            }
            if (ontime != null && ontime != 0) {
                LocalDateTime dt = LocalDateTime.ofEpochSecond(ontime,0, ZoneOffset.UTC);
                internetAttr.setWebEnd(dt);
            }
        }
        List<RFSection> rfSections = new ArrayList<>();
        String section = damArticle.getSection();
        RFSection rfSection = new RFSection();
        rfSection.setName(section);
        rfSections.add(rfSection);
        internetAttr.setSections(rfSections);

        return internetAttr;
    }

    private int calculatePriority(String name, int wordCount) {
        if (name.startsWith("H4")) return 5;
        if (wordCount < 201) return 4;
        if (wordCount < 401) return 3;
        if (wordCount < 601) return 2;
        return 1;
    }


    private RedFactImageBean getImageVariant(final ContentManager contentManager, final ContentId contentId) {
        final ContentVersionId latestVersion = contentManager.resolve(contentId, Subject.NOBODY_CALLER);
        final ContentResult<RedFactImageBean> cr = contentManager.get(latestVersion, RedFactImageBean.VARIANTNAME, RedFactImageBean.class, null, Subject.NOBODY_CALLER);
        if (cr != null && (cr.getStatus() == Status.OK) && (cr.getContent() != null)) {
            return cr.getContent().getContentData();
        }
        return null;
    }

    private String removeHTML(String value) {
        if (!Strings.isNullOrEmpty(value)) {
            value = value.replaceAll("<(.|\\n)+?>", "");
            value = value.replaceAll("\\s+", " ");
            value = value.replaceAll("&nbsp;", " ");
        }
        return value;
    }

    private String truncateWords(final String value, int blankCount) {
        return truncateWords(value, blankCount, " ...");
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
