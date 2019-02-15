package com.atex.onecms.app.dam.integration.camel.component.redfact.json;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

/**
 * Date Deserializer for Gson.
 *
 * @author mnova
 */
public class DateDeserializer implements JsonDeserializer<Date> {

    private static final Logger LOGGER = Logger.getLogger(DateDeserializer.class.getName());

    private static final List<String> DATE_FORMATS = Lists.newArrayList(
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            "yyyy-MM-dd'T'HH:mm:ssX",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
    );

    public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) {

        final String value = json.getAsJsonPrimitive().getAsString();

        for (final String format : DATE_FORMATS) {
            try {
                final SimpleDateFormat sdf = new SimpleDateFormat(format);
                return sdf.parse(value);
            } catch (ParseException e) {
                LOGGER.log(Level.FINE, e.getMessage());
            }
        }

        // if we cannot parse it than simple log a warning and return a null
        // date, since this is used only for registration it is better to
        // allow a registration even if we are not able to get all the
        // users parameters such as a date.
        // The log format used below is the same used by the catch exception
        // above which will be logged once for each date format (so I lowered
        // it to fine level).

        LOGGER.log(Level.WARNING, "WARNING: Unparseable date: \"" + value + "\"");

        return null;
    }
}