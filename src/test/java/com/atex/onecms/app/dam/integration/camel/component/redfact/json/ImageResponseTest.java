package com.atex.onecms.app.dam.integration.camel.component.redfact.json;

import java.util.Date;

import com.atex.onecms.app.dam.integration.camel.component.redfact.json.DateDeserializer;
import com.atex.onecms.app.dam.integration.camel.component.redfact.json.GSONCustomExclusionStrategy;
import com.atex.onecms.app.dam.integration.camel.component.redfact.json.ImageResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Unit test for {@link ImageResponse}.
 *
 * @author mnova
 */
@RunWith(MockitoJUnitRunner.class)
public class ImageResponseTest {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateDeserializer())
            .setExclusionStrategies(new GSONCustomExclusionStrategy())
            .create();

    @Test
    public void testImageResponseDeserialize() {
        final String body = "{\n" +
                "    \"id\": 47,\n" +
                "    \"date\": \"2016-04-05T14:31:51\",\n" +
                "    \"date_gmt\": \"2016-04-05T13:31:51\",\n" +
                "    \"guid\": {\n" +
                "        \"rendered\": \"http://gui.wordpress.docker:8000/wp-content/uploads/2016/04/DSC01821.jpg\",\n" +
                "        \"raw\": \"http://gui.wordpress.docker:8000/wp-content/uploads/2016/04/DSC01821.jpg\"\n" +
                "    },\n" +
                "    \"modified\": \"2016-04-05T14:31:51\",\n" +
                "    \"modified_gmt\": \"2016-04-05T13:31:51\",\n" +
                "    \"password\": \"\",\n" +
                "    \"slug\": \"dsc01821\",\n" +
                "    \"status\": \"inherit\",\n" +
                "    \"type\": \"attachment\",\n" +
                "    \"link\": \"http://www.wordpress.docker:8000/dsc01821/\",\n" +
                "    \"title\": {\n" +
                "        \"raw\": \"DSC01821\",\n" +
                "        \"rendered\": \"DSC01821\"\n" +
                "    },\n" +
                "    \"author\": 2,\n" +
                "    \"comment_status\": \"open\",\n" +
                "    \"ping_status\": \"closed\",\n" +
                "    \"alt_text\": \"\",\n" +
                "    \"caption\": \"\",\n" +
                "    \"description\": \"\",\n" +
                "    \"media_type\": \"image\",\n" +
                "    \"mime_type\": \"image/jpeg\",\n" +
                "    \"media_details\": {\n" +
                "        \"width\": 1200,\n" +
                "        \"height\": 800,\n" +
                "        \"file\": \"2016/04/DSC01821.jpg\",\n" +
                "        \"image_meta\": {\n" +
                "            \"aperture\": \"0\",\n" +
                "            \"credit\": \"\",\n" +
                "            \"camera\": \"\",\n" +
                "            \"caption\": \"\",\n" +
                "            \"created_timestamp\": \"0\",\n" +
                "            \"copyright\": \"\",\n" +
                "            \"focal_length\": \"0\",\n" +
                "            \"iso\": \"0\",\n" +
                "            \"shutter_speed\": \"0\",\n" +
                "            \"title\": \"\",\n" +
                "            \"orientation\": \"0\",\n" +
                "            \"keywords\": []\n" +
                "        },\n" +
                "        \"sizes\": {}\n" +
                "    },\n" +
                "    \"post\": null,\n" +
                "    \"source_url\": \"http://gui.wordpress.docker:8000/wp-content/uploads/2016/04/DSC01821.jpg\",\n" +
                "    \"_links\": {\n" +
                "        \"self\": [\n" +
                "            {\n" +
                "                \"href\": \"http://www.wordpress.docker:8000/wp-json/wp/v2/media/47\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"collection\": [\n" +
                "            {\n" +
                "                \"href\": \"http://www.wordpress.docker:8000/wp-json/wp/v2/media\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"about\": [\n" +
                "            {\n" +
                "                \"href\": \"http://www.wordpress.docker:8000/wp-json/wp/v2/types/attachment\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"author\": [\n" +
                "            {\n" +
                "                \"embeddable\": true,\n" +
                "                \"href\": \"http://www.wordpress.docker:8000/wp-json/wp/v2/users/2\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"replies\": [\n" +
                "            {\n" +
                "                \"embeddable\": true,\n" +
                "                \"href\": \"http://www.wordpress.docker:8000/wp-json/wp/v2/comments?post=47\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        final ImageResponse response = gson.fromJson(body, ImageResponse.class);
        Assert.assertNotNull(response);
        Assert.assertEquals("47", response.getId());
        Assert.assertEquals("image/jpeg", response.getMimeType());
    }

}