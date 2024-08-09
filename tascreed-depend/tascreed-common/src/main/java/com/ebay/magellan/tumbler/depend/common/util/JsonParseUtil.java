package com.ebay.magellan.tumbler.depend.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class JsonParseUtil {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static ObjectMapper getObjectMapper(){
        return new ObjectMapper()
                .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setDateFormat(new SimpleDateFormat(DATE_FORMAT))
                .setTimeZone(TimeZone.getTimeZone("UTC"))
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
    }

    public static ObjectReader getReader(Class<?> T){
        return getObjectMapper().readerFor(T);
    }
    public static ObjectWriter getWriter(Class<?> T){
        return getObjectMapper().writerFor(T);
    }

    public static String extractString(String json, String key) {
        String ret = null;
        try {
            if (StringUtils.isNotBlank(json)) {
                JsonNode parent= getObjectMapper().readTree(json);
                JsonNode d = parent.path(key);
                if (!d.isMissingNode() && !d.isNull()) {
                    ret = d.asText();
                }
            }
        } catch (Exception e) {
            // do nothing
        }
        return ret;
    }

}
