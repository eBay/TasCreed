package com.ebay.magellan.tascreed.core.domain.request;

import com.ebay.magellan.tascreed.core.domain.request.extra.ReqTime;
import com.ebay.magellan.tascreed.core.domain.trait.TraitsAmend;
import com.ebay.magellan.tascreed.depend.common.util.JsonParseUtil;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.*;

@Getter
@Setter
@ToString
public class JobRequest {
    @JsonProperty("jobName")
    @JsonAlias({"name"})
    private String jobName;
    @JsonProperty("trigger")
    private String trigger;
    @JsonProperty("priority")
    private Integer priority;

    @JsonProperty("params")
    private Map<String, String> params;
    @JsonProperty("steps")
    private List<StepRequest> steps;

    @JsonProperty("traits")
    private TraitsAmend traitsAmend = new TraitsAmend();

    // optional, job create task after a time, by default no limit
    @JsonProperty("after")
    private ReqTime after;

    // expect execute duration of the job, in milliseconds
    @JsonProperty("duration")
    private Long duration;

    // -----

    public StepRequest findStepRequestByName(String stepName) {
        if (CollectionUtils.isNotEmpty(steps) && StringUtils.isNotBlank(stepName)) {
            for (StepRequest step : steps) {
                if (stepName.equals(step.getStepName())) {
                    return step;
                }
            }
        }
        return null;
    }

    // -----

    @JsonIgnore
    public Date getAfterTime() {
        if (after != null) {
            return after.getTime();
        }
        return null;
    }

    // -----

    /**
     * json serialize and deserialize
     */
    private static ObjectReader reader = JsonParseUtil.getReader(JobRequest.class);
    private static ObjectWriter writer = JsonParseUtil.getWriter(JobRequest.class);

    public static JobRequest fromJson(String json) throws IOException {
        JobRequest pin = null;
        if (StringUtils.isNotBlank(json)) {
            pin = reader.readValue(json);
        }
        return pin;
    }
    public String toJson() throws JsonProcessingException {
        return writer.writeValueAsString(this);
    }

    // -----

    public JobRequest cloneSelf() {
        try {
            String s = this.toJson();
            return JobRequest.fromJson(s);
        } catch (Exception e) {
            return null;
        }
    }

}
