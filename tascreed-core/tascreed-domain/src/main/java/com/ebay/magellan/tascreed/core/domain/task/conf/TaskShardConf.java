package com.ebay.magellan.tascreed.core.domain.task.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TaskShardConf implements TaskConf {
    @JsonProperty("total")
    private int total;
    @JsonProperty("index")
    private int index;

    public String buildName() {
        return String.format("shard-%d", index);
    }
}
