package com.ebay.magellan.tascreed.core.domain.schedule.mid;

import java.util.Date;

import com.ebay.magellan.tascreed.core.domain.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class TriggerState {
    @JsonProperty("time")
    private Date time;

    @JsonProperty("success")
    private boolean success = true;

    @JsonProperty("error")
    private String error;

    // -----

    public void fail(String error) {
        this.success = false;
        this.error = StringUtil.shortStr(error);
    }

}
