package com.ebay.magellan.tascreed.core.domain.define.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class StepConf {
    private static final int MAX_TASK_COUNT = 50;

    @JsonProperty("maxTaskCount")
    private Integer maxTaskCount;

    public int taskCount() {
        return (maxTaskCount != null && maxTaskCount > 0) ? maxTaskCount : MAX_TASK_COUNT;
    }
}
