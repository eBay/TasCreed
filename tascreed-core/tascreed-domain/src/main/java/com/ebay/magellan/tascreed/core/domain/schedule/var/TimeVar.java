package com.ebay.magellan.tascreed.core.domain.schedule.var;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Getter
@Setter
@ToString
public class TimeVar extends Var {
    @JsonProperty("pattern")
    private String pattern;
    @JsonProperty("zone")
    private String zone;

    @JsonProperty("deltaMs")
    private Long deltaMs;

    // -----

    @Override
    public String validate() {
        if (pattern == null) return "pattern is null";
        return null;
    }

    @Override
    public String value(long triggerTimestamp) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            if (StringUtils.isNotBlank(zone)) simpleDateFormat.setTimeZone(TimeZone.getTimeZone(zone));
            long ts = triggerTimestamp;
            if (deltaMs != null) ts = ts + deltaMs;
            return simpleDateFormat.format(new Date(ts));
        } catch (Exception e) {
            return null;
        }
    }

}
