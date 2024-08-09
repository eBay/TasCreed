package com.ebay.magellan.tascreed.core.domain.schedule.conf;

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
public class SchedulePointConf extends ScheduleConf {
    @JsonProperty("points")
    private List<Date> points;

    // -----

    @Override
    public String validate() {
        return null;
    }

    @Override
    public List<Long> nextTriggerTimestamps(long currentTimestamp, long duration) {
        List<Long> ts = new ArrayList<>();
        long s = currentTimestamp;
        long e = currentTimestamp + duration;
        for (Date point : points) {
            if (point != null) {
                long t = point.getTime();
                if (t >= s && t < e) {
                    ts.add(t);
                }
            }
        }
        return ts;
    }

}
