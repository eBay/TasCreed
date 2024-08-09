package com.ebay.magellan.tumbler.core.domain.state.partial;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskCheckpoint {
    @JsonProperty("checkpoint")
    private String value;
}
