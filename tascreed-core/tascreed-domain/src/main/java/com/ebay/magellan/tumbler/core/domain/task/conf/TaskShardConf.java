package com.ebay.magellan.tumbler.core.domain.task.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TaskShardConf implements TaskConf {
    @JsonProperty("total")
    private int total;
    @JsonProperty("index")
    private int index;

    public String buildName() {
        return String.format("shard-%d", index);
    }
}
