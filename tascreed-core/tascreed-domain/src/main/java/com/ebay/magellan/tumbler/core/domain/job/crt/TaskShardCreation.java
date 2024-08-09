package com.ebay.magellan.tumbler.core.domain.job.crt;

import com.ebay.magellan.tumbler.core.domain.define.conf.StepShardConf;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TaskShardCreation implements TaskCreation<StepShardConf> {
    @JsonProperty("lastIndex")
    private int lastShardIndex = -1;

    public boolean isDone(StepShardConf sc) {
        return getLastShardIndex() >= sc.getShard() - 1;
    }
}
