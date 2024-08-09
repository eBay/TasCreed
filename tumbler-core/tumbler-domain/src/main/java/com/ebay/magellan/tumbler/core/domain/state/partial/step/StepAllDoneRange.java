package com.ebay.magellan.tumbler.core.domain.state.partial.step;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class StepAllDoneRange {
    @JsonProperty("shardDoneRange")
    private ShardDoneRange shardDoneRange;
    @JsonProperty("packDoneRange")
    private PackDoneRange packDoneRange;

    // -----

    public ShardDoneRange shard() {
        if (shardDoneRange == null) {
            shardDoneRange = new ShardDoneRange();
        }
        return shardDoneRange;
    }

    public PackDoneRange pack() {
        if (packDoneRange == null) {
            packDoneRange = new PackDoneRange();
        }
        return packDoneRange;
    }
}
