package com.ebay.magellan.tumbler.core.domain.schedule.var;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CountVar extends Var {
    @JsonProperty("next")
    private long next;

    // -----

    @Override
    public String validate() {
        return null;
    }

    @Override
    public String value(long triggerTimestamp) {
        String s = String.format("%d", next);
        next++;
        return s;
    }

}
