package com.ebay.magellan.tumbler.core.domain.duty;

import com.ebay.magellan.tumbler.depend.common.util.JsonParseUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.List;

@Getter
@Setter
public class NodeDutyRules {
    @JsonProperty("rules")
    private List<NodeDutyRule> rules;

    @JsonIgnore
    private String fromValue;

    // -----

    public boolean sameStr(NodeDutyRules other) {
        if (other == null) return false;
        return StringUtils.equals(getFromValue(), other.getFromValue());
    }

    // -----

    /**
     * json serialize and deserialize
     */
    private static ObjectReader reader = JsonParseUtil.getReader(NodeDutyRules.class);
    private static ObjectWriter writer = JsonParseUtil.getWriter(NodeDutyRules.class);

    public static NodeDutyRules fromJson(String json) throws IOException {
        NodeDutyRules pin = null;
        if (StringUtils.isNotBlank(json)) {
            pin = reader.readValue(json);
        }
        if (pin != null) {
            pin.setFromValue(json);
        }
        return pin;
    }
    public String toJson() throws JsonProcessingException {
        return writer.writeValueAsString(this);
    }


}
