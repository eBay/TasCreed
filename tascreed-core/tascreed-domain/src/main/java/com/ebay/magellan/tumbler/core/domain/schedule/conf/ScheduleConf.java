package com.ebay.magellan.tumbler.core.domain.schedule.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = ScheduleCronConf.class, name = "cron"),
        @JsonSubTypes.Type(value = SchedulePeriodConf.class, name = "period"),
        @JsonSubTypes.Type(value = SchedulePointConf.class, name = "point"),
})
public abstract class ScheduleConf {
    @JsonProperty("type")
    private String type;

    // -----

    public abstract String validate();

    public abstract List<Long> nextTriggerTimestamps(long currentTimestamp, long duration);

    // -----

    public boolean validTriggerTimestamp(long timestamp) {
        final long duration = 5 * 1000L;
        List<Long> ts = nextTriggerTimestamps(timestamp - duration, duration * 2);
        return ts.contains(timestamp);
    }
}
