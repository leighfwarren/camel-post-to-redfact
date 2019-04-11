package com.atex.onecms.app.dam.integration.camel.component.redfact.json;

import com.atex.onecms.app.dam.standard.aspects.OneArticleBean;
import com.atex.onecms.app.dam.standard.aspects.OneImageBean;
import com.atex.onecms.content.ContentId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Unit test
 *
 * @author lfw
 */
@RunWith(MockitoJUnitRunner.class)
public class ArticleTest {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateDeserializer())
            .setExclusionStrategies(new GSONCustomExclusionStrategy())
            .create();

    @Test
    public void testArticleSerialize() {
        OneArticleBean b = new OneArticleBean();
        b.setAuthor("TEST_AUTHOR");
        List<ContentId> imageList = new ArrayList<>();
        b.setImages(imageList);
        OneImageBean i1 = new OneImageBean();
        i1.setByline("BYLINE");
        i1.setCaption("CAPTION");
        i1.setLastPubdate(new Date());
        i1.setCreationdate(new Date());
    }

}