package com.ebay.magellan.tascreed.core.domain.schedule.var;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ConstVar extends Var {
    @JsonProperty("value")
    private String value;

    // -----

    @Override
    public String validate() {
        if (value == null) return "value is null";
        return null;
    }

    @Override
    public String value(long triggerTimestamp) {
        return value;
    }

}
