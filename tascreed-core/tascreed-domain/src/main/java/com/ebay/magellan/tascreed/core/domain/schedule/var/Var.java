package com.ebay.magellan.tascreed.core.domain.schedule.var;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = ConstVar.class, name = "const"),
        @JsonSubTypes.Type(value = TimeVar.class, name = "time"),
        @JsonSubTypes.Type(value = CountVar.class, name = "count"),
})
public abstract class Var {
    @JsonProperty("type")
    private String type;

    // -----

    public abstract String validate();

    public abstract String value(long triggerTimestamp);
}
