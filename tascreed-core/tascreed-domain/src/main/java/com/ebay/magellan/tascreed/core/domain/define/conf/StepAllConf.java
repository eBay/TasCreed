package com.ebay.magellan.tascreed.core.domain.define.conf;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class StepAllConf {
    @JsonProperty("shardConf")
    private StepShardConf shardConf;

    @JsonProperty("packConf")
    private StepPackConf packConf;

    // optional, max task pick times to overwrite, negative denotes infinity
    @JsonProperty("maxPickTimes")
    @JsonAlias({"maxErrorTimes"})
    private Integer maxPickTimes;

    // -----

    public void assembleOther(StepAllConf other) {
        if (other == null) return;
        setShardConf(StepShardConf.merge(getShardConf(), other.getShardConf()));
        setPackConf(StepPackConf.merge(getPackConf(), other.getPackConf()));
    }
}
