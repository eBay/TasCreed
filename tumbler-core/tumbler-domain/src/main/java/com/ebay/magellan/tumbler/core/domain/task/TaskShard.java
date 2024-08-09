package com.ebay.magellan.tumbler.core.domain.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TaskShard {
    @JsonProperty("total")
    private int total;
    @JsonProperty("index")
    private int index;
}