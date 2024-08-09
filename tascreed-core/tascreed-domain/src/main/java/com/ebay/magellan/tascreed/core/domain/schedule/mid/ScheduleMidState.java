package com.ebay.magellan.tascreed.core.domain.schedule.mid;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Getter
@Setter
@ToString
public class ScheduleMidState {
    // optional, schedule works after a time, by default no limit
    @JsonProperty("after")
    private Date afterTime;

    @JsonProperty("createTime")
    private Date createTime;
    @JsonProperty("modifyTime")
    private Date modifyTime;
}
