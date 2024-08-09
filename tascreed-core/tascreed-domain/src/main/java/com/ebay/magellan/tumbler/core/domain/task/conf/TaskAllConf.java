package com.ebay.magellan.tumbler.core.domain.task.conf;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskAllConf {
    @JsonProperty("shardConf")
    private TaskShardConf shardConf;

    @JsonProperty("packConf")
    private TaskPackConf packConf;

    // optional, max task pick times to overwrite, negative denotes infinity
    @JsonProperty("maxPickTimes")
    @JsonAlias({"maxErrorTimes"})
    private Integer maxPickTimes;
}
