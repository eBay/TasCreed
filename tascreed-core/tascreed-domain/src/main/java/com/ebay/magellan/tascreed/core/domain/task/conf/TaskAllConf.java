package com.ebay.magellan.tascreed.core.domain.task.conf;

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
