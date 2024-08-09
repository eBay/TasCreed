package com.ebay.magellan.tascreed.core.domain.schedule.conf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.support.CronExpression;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class ScheduleCronConf extends ScheduleConf {
    @JsonProperty("cron")
    private String cron;

    @JsonProperty("startDate")
    private Date startDate;
    @JsonProperty("endDate")
    private Date endDate;

    @JsonIgnore
    private CronExpression cronExpression;

    public void setCron(String cron) {
        this.cron = cron;
        try {
            cronExpression = CronExpression.parse(cron);
        } catch (Exception e) {
            e.printStackTrace();
            cronExpression = null;
        }
    }

    // -----

    @Override
    public String validate() {
        if (StringUtils.isBlank(cron)) return "cron is blank";
        if (cronExpression == null) return String.format("cron expression %s is illegal", cron);
        if (startDate != null && endDate != null) {
            if (endDate.before(startDate)) return "endDate is before startDate";
        }
        return null;
    }

    @Override
    public List<Long> nextTriggerTimestamps(long currentTimestamp, long duration) {
        List<Long> ts = new ArrayList<>();
        if (cronExpression != null) {
            long s = currentTimestamp;
            if (startDate != null && s < startDate.getTime()) s = startDate.getTime();
            long e = currentTimestamp + duration;
            if (endDate != null && e > endDate.getTime()) e = endDate.getTime();
            long a = s;
            while (a < e) {
                // the next time will be after the current one
                Instant instant = Instant.ofEpochMilli(a);
                ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
                long t = cronExpression.next(zonedDateTime).toInstant().toEpochMilli();
                if (t < e) {
                    ts.add(t);
                }
                a = t;
            }
        }
        return ts;
    }

}
