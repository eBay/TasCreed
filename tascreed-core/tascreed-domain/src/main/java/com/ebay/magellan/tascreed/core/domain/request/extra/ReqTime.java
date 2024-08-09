package com.ebay.magellan.tascreed.core.domain.request.extra;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

@Getter
@Setter
@ToString
public class ReqTime {
    private static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String DEFAULT_TIME_ZONE = "UTC";

    @JsonProperty("timeString")
    @JsonAlias({"string", "timeStr"})
    private String timeString;
    @JsonProperty("timePattern")
    @JsonAlias({"pattern", "format", "timeFormat"})
    private String timePattern = DEFAULT_TIME_FORMAT;
    @JsonProperty("timeZone")
    @JsonAlias({"zone"})
    private String timeZone = DEFAULT_TIME_ZONE;

    @JsonProperty("timestamp")
    @JsonAlias({"long", "timeLong"})
    private Long timeLong;

    @JsonIgnore
    public Date getTime() {
        if (timeLong != null) {
            return new Date(timeLong.longValue());
        } else if (StringUtils.isNotBlank(timeString)) {
            try {
                DateTimeFormatter formatter = DateTimeFormat.forPattern(timePattern)
                        .withZone(DateTimeZone.forID(timeZone));
                return formatter.parseDateTime(timeString).toDate();
            } catch (Exception e) {
                throw new RuntimeException("parse time format fails");
            }
        } else {
            return null;
        }
    }
}
