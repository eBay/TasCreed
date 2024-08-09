package com.ebay.magellan.tumbler.core.domain.job.crt;

import com.ebay.magellan.tumbler.core.domain.define.conf.StepPackConf;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TaskPackCreation implements TaskCreation<StepPackConf> {
    @JsonProperty("lastId")
    private long lastPackId = -1L;
    @JsonProperty("lastOffset")
    private long lastOffset = -1L;

    public boolean isDone(StepPackConf conf) {
        if (conf.isInfinite()) return false;
        return getLastOffset() >= conf.getEnd();
    }
}
