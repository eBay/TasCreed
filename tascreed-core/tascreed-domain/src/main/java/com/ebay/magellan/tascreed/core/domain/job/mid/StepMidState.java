package com.ebay.magellan.tascreed.core.domain.job.mid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class StepMidState {
    // optional, task pick after a time, by default no limit
    @JsonProperty("after")
    private Date afterTime;

    // expect execute duration of each task, in milliseconds
    @JsonProperty("duration")
    private Long duration;

    @JsonProperty("modifyTime")
    private Date modifyTime;
}
