package com.ebay.magellan.tumbler.depend.ext.es.doc;

import com.ebay.magellan.tumbler.depend.common.util.JsonParseUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocValue {
    private String value;
    private Map<String, String> attrs;

    public void addAttr(String key, String value) {
        if (attrs == null) {
            attrs = new HashMap<>();
        }
        attrs.put(key, value);
    }

    public String readAttr(String key) {
        if (attrs != null) {
            return attrs.get(key);
        }
        return null;
    }

    /**
     * json serialize and deserialize
     */
    private static ObjectReader reader = JsonParseUtil.getReader(DocValue.class);
    private static ObjectWriter writer = JsonParseUtil.getWriter(DocValue.class);

    public static DocValue fromJson(String json) throws IOException {
        DocValue pin = null;
        if (StringUtils.isNotBlank(json)) {
            pin = reader.readValue(json);
        }
        return pin;
    }
    public String toJson() throws JsonProcessingException {
        return writer.writeValueAsString(this);
    }
}
