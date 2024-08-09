package com.ebay.magellan.tumbler.core.domain.task.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TaskPackConf implements TaskConf {
    @JsonProperty("id")
    private long id;
    @JsonProperty("start")
    private long start;     // include
    @JsonProperty("end")
    private long end;     // include

    public String buildName() {
        return String.format("pack-%d", id);
    }
}
