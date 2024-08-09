package com.ebay.magellan.tascreed.core.domain.routine;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoutineDefine {
    @JsonProperty("routineName")
    @JsonAlias({"name"})
    private String routineName;

    // the scale/number of routine thread
    @JsonProperty("scale")
    private int scale = 1;

    // the priority of routine, the larger the higher
    @JsonProperty("priority")
    private int priority = 1;

    // interval of routine, in millisecond
    @JsonProperty("interval")
    private long interval = 60 * 1000L; // default 1 min

    // -----

    public int getScale() {
        return scale > 1 ? scale : 1;
    }
}
