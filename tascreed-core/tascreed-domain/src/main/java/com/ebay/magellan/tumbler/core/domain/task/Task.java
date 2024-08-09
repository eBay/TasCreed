package com.ebay.magellan.tumbler.core.domain.task;

import com.ebay.magellan.tumbler.core.domain.define.StepTypeEnum;
import com.ebay.magellan.tumbler.core.domain.define.conf.StepAllConf;
import com.ebay.magellan.tumbler.core.domain.state.StepStateEnum;
import com.ebay.magellan.tumbler.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tumbler.core.domain.task.conf.TaskAllConf;
import com.ebay.magellan.tumbler.core.domain.state.partial.TaskCheckpoint;
import com.ebay.magellan.tumbler.core.domain.task.mid.TaskMidState;
import com.ebay.magellan.tumbler.core.domain.trait.Trait;
import com.ebay.magellan.tumbler.core.domain.trait.Traits;
import com.ebay.magellan.tumbler.depend.common.util.DefaultValueUtil;
import com.ebay.magellan.tumbler.depend.common.util.JsonParseUtil;
import com.ebay.magellan.tumbler.core.domain.occupy.OccupyInfo;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class Task {
    @JsonProperty("jobName")
    private String jobName;
    @JsonProperty("trigger")
    private String trigger;
    @JsonProperty("priority")
    private Integer priority;
    @JsonProperty("stepName")
    private String stepName;

    @JsonProperty("exeClass")
    private String exeClass;

    @JsonProperty("stepType")
    private StepTypeEnum stepType;
    @JsonUnwrapped
    private TaskAllConf taskAllConf = new TaskAllConf();

    @JsonProperty("dependentStepStates")
    private Map<String, StepStateEnum> dependentStepStates; // the states of dependent steps
    @JsonProperty("prevPhaseStepStates")
    private Map<String, StepStateEnum> prevPhaseStepStates; // the states of recent prev phase steps

    @JsonProperty("params")
    private Map<String, String> params;     // input params
    @JsonProperty("updatedParams")
    private Map<String, String> updatedParams;      // output params

    @JsonProperty("updatedConfigs")
    private Map<String, StepAllConf> updatedConfigs;   // output updated step configs

    @JsonProperty("affinityRule")
    private String affinityRule;

//    @JsonProperty("archive")
//    private Boolean archive;
    @JsonProperty("traits")
    private List<String> traitStrList;
    @JsonIgnore
    private Traits traits = new Traits(Trait.TraitType.TASK);

    @JsonProperty("result")
    private TaskResult result;

    // wrap all the mid state in an object
    @JsonUnwrapped
    private TaskMidState midState = new TaskMidState();

    @JsonIgnore
    private String fromValue;

    @JsonIgnore
    private OccupyInfo occupyInfo;

    // -----

    @JsonIgnore
    public boolean isSimple() {
        return (stepType == null || stepType == StepTypeEnum.SIMPLE);
    }
    @JsonIgnore
    public boolean isShard() {
        return stepType == StepTypeEnum.SHARD;
    }
    @JsonIgnore
    public boolean isPack() {
        return stepType == StepTypeEnum.PACK;
    }

    // -----

    public void setTraitStrList(List<String> traitStrList) {
        this.traitStrList = traitStrList;
        traits.genTraitSetDirectly(traitStrList);
    }

    public List<String> getTraitStrList() {
        this.traitStrList = traits.genTraitStrList();
        return traitStrList;
    }

    //    @JsonIgnore
//    public boolean isArchive() {
//        return archive != null ? archive : false;
//    }

    // -----

    @JsonIgnore
    public TaskStateEnum getTaskState() {
        return result != null ? result.getState() : TaskStateEnum.UNDONE;
    }
    @JsonIgnore
    public TaskCheckpoint getTaskCheckpoint() {
        return result != null ? result.getCheckpoint() : null;
    }

    @JsonIgnore
    public String getStepFullName() {
        return String.format("%s:%s:%s", getJobName(), getTrigger(), getStepName());
    }
    @JsonIgnore
    public String getTaskFullName() {
        return String.format("%s:%s:%s", getJobName(), getTrigger(), getTaskName());
    }

    @JsonIgnore
    public String getTaskName() {
        if (isShard()) {
            return String.format("%s.%s", getStepName(), taskAllConf.getShardConf().buildName());
        } else if (isPack()) {
            return String.format("%s.%s", getStepName(), taskAllConf.getPackConf().buildName());
        } else {
            return getStepName();
        }
    }

    @JsonIgnore
    public int getPriorityWeight() {
        return DefaultValueUtil.intValue(priority);
    }

    public boolean hasOccupyInfo() {
        return occupyInfo != null;
    }

    // -----

    public void addDependentStepState(String stepName, StepStateEnum state) {
        if (StringUtils.isBlank(stepName)) return;
        if (dependentStepStates == null) {
            dependentStepStates = new HashMap<>();
        }
        dependentStepStates.put(stepName, state);
    }

    public void addPrevPhaseStepState(String stepName, StepStateEnum state) {
        if (StringUtils.isBlank(stepName)) return;
        if (prevPhaseStepStates == null) {
            prevPhaseStepStates = new HashMap<>();
        }
        prevPhaseStepStates.put(stepName, state);
    }

    // -----

    public String getParam(String k) {
        if (params == null) return null;
        return params.get(k);
    }

    public void addParam(String k, String v) {
        if (k == null) return;
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(k, v);
    }
    public void addUpdatedParam(String k, String v) {
        if (k == null) return;
        if (updatedParams == null) {
            updatedParams = new HashMap<>();
        }
        updatedParams.put(k, v);
    }

    public void addUpdatedConfig(String stepName, StepAllConf conf) {
        if (stepName == null) return;
        if (updatedConfigs == null) {
            updatedConfigs = new HashMap<>();
        }
        updatedConfigs.put(stepName, conf);
    }

    // -----

    public boolean resetForRetry() {
        boolean updated = false;
        if (getTaskState().canRetry()) {
            if (result != null) {
                result.setState(null);
                result.setReason(null);
                result.setProgression(null);
            }
            midState.resetAllRetryTimes();
            updated = true;
        }
        return updated;
    }

    // -----

    /**
     * json serialize and deserialize
     */
    private static ObjectReader reader = JsonParseUtil.getReader(Task.class);
    private static ObjectWriter writer = JsonParseUtil.getWriter(Task.class);

    public static Task fromJson(String json) throws IOException {
        Task pin = null;
        if (StringUtils.isNotBlank(json)) {
            pin = reader.readValue(json);
        }
        if (pin != null) {
            pin.setFromValue(json);
        }
        return pin;
    }
    public static String toJson(Task pin, Class<?> view) throws JsonProcessingException {
        if (pin != null) {
            return pin.toJson(view);
        }
        return null;
    }
    public String toJson(Class<?> view) throws JsonProcessingException {
        ObjectWriter wr = writer;
        if (view != null) {
            wr = writer.withView(view);
        }
        return wr.writeValueAsString(this);
    }

}