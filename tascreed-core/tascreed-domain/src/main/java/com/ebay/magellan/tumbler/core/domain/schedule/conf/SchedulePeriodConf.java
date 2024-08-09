package com.ebay.magellan.tumbler.core.domain.schedule.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class SchedulePeriodConf extends ScheduleConf {
    @JsonProperty("intervalMs")
    private long intervalMs;

    @JsonProperty("startDate")
    private Date startDate;
    @JsonProperty("endDate")
    private Date endDate;

    // -----

    @Override
    public String validate() {
        if (intervalMs <= 0) return String.format("intervalMs is not positive");
        if (startDate != null && endDate != null) {
            if (endDate.before(startDate)) return "endDate is before startDate";
        }
        return null;
    }

    @Override
    public List<Long> nextTriggerTimestamps(long currentTimestamp, long duration) {
        List<Long> ts = new ArrayList<>();
        long s = 0;
        if (startDate != null) s = startDate.getTime();
        long e = currentTimestamp + duration;
        if (endDate != null && e > endDate.getTime()) e = endDate.getTime();
        if (s < e) {
            long a = s + (currentTimestamp - s) / intervalMs * intervalMs;
            while (a < e) {
                if (a >= currentTimestamp && a >= s) {
                    ts.add(a);
                }
                a += intervalMs;
            }
        }
        return ts;
    }

}
