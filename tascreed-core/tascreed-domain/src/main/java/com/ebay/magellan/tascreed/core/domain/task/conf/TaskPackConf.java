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
