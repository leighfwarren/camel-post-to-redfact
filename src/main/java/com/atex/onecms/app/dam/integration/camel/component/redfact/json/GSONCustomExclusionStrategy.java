package com.atex.onecms.app.dam.integration.camel.component.redfact.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Use it with:
 *
 *  Gson gson = new GsonBuilder()
*    .setExclusionStrategies(new GSONCustomExclusionStrategy())
*    .create();
 *
 * @author mnova
 */
public class GSONCustomExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(final FieldAttributes f) {
        return f.getAnnotation(GSONCustomExclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(final Class<?> clazz) {
        return clazz.getAnnotation(GSONCustomExclude.class) != null;
    }

}
