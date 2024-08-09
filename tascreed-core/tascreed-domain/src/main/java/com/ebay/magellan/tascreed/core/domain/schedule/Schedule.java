package com.ebay.magellan.tascreed.core.domain.schedule;

import com.ebay.magellan.tascreed.core.domain.request.JobRequest;
import com.ebay.magellan.tascreed.core.domain.schedule.conf.ScheduleConf;
import com.ebay.magellan.tascreed.core.domain.schedule.mid.ScheduleMidState;
import com.ebay.magellan.tascreed.core.domain.schedule.mid.TriggerState;
import com.ebay.magellan.tascreed.core.domain.schedule.var.Var;
import com.ebay.magellan.tascreed.depend.common.util.JsonParseUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class Schedule {
    @JsonProperty("scheduleName")
    private String scheduleName;

    @JsonProperty("jobRequest")
    private JobRequest jobRequest;

    @JsonProperty("conf")
    private ScheduleConf conf;

    // variables
    @JsonProperty("variables")
    private Map<String, Var> variables;

    // wrap all the mid state in an object
    @JsonUnwrapped
    private ScheduleMidState midState = new ScheduleMidState();

    @JsonProperty("lastTriggerState")
    private TriggerState lastTriggerState;

    @JsonIgnore
    private String fromValue;

    // -----

    public String validate() {
        if (StringUtils.isBlank(scheduleName)) return "schedule name is illegal";
        if (jobRequest == null) return "job request is null";

        if (conf == null) return "schedule conf is null";
        String err = conf.validate();
        if (StringUtils.isNotBlank(err)) return err;

        if (MapUtils.isNotEmpty(variables)) {
            for (Var v : variables.values()) {
                err = v.validate();
                if (StringUtils.isNotBlank(err)) return err;
            }
        }

        return null;
    }

    // -----

    public Trigger trigger(Date triggerTime) {
        if (jobRequest == null) return null;
        Trigger trigger = new Trigger();
        trigger.setScheduleName(scheduleName);
        trigger.setJobRequest(jobRequest.cloneSelf());
        trigger.setTriggerTime(triggerTime);
        trigger.setVariables(variableValues(triggerTime));
        trigger.replace();
        return trigger;
    }

    Map<String, String> variableValues(Date triggerTime) {
        long triggerTimestamp = triggerTime.getTime();
        Map<String, String> vars = new HashMap<>();
        for (Map.Entry<String, Var> entry : variables.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue().value(triggerTimestamp);
            vars.put(k, v);
        }
        return vars;
    }

    // -----

    public boolean validTriggerTime(Date triggerTime) {
        if (triggerTime == null || conf == null) return false;
        return conf.validTriggerTimestamp(triggerTime.getTime());
    }

    public boolean canTrigger(Date triggerTime) {
        if (triggerTime == null) return false;
        if (lastTriggerState == null) return true;
        Date lastTriggerTime = lastTriggerState.getTime();
        if (lastTriggerTime == null) return true;
        return lastTriggerTime.before(triggerTime);
    }

    public void updateTriggerState(TriggerState state) {
        if (state == null || state.getTime() == null) return;
        if (lastTriggerState == null
                || lastTriggerState.getTime() == null
                || lastTriggerState.getTime().before(state.getTime())) {
            lastTriggerState = state;
        }
    }

    // -----

    /**
     * json serialize and deserialize
     */
    private static ObjectReader reader = JsonParseUtil.getReader(Schedule.class);
    private static ObjectWriter writer = JsonParseUtil.getWriter(Schedule.class);

    public static Schedule fromJson(String json) throws IOException {
        Schedule pin = null;
        if (StringUtils.isNotBlank(json)) {
            pin = reader.readValue(json);
        }
        if (pin != null) {
            pin.setFromValue(json);
        }
        return pin;
    }
    public String toJson() throws JsonProcessingException {
        return writer.writeValueAsString(this);
    }
}
