package com.ebay.magellan.tascreed.core.domain.job;

import com.ebay.magellan.tascreed.core.domain.define.StepDefine;
import com.ebay.magellan.tascreed.core.domain.define.conf.StepAllConf;
import com.ebay.magellan.tascreed.core.domain.define.conf.StepPackConf;
import com.ebay.magellan.tascreed.core.domain.define.conf.StepShardConf;
import com.ebay.magellan.tascreed.core.domain.job.crt.TaskAllCreation;
import com.ebay.magellan.tascreed.core.domain.job.crt.TaskPackCreation;
import com.ebay.magellan.tascreed.core.domain.job.crt.TaskShardCreation;
import com.ebay.magellan.tascreed.core.domain.job.mid.StepMidState;
import com.ebay.magellan.tascreed.core.domain.state.StepStateEnum;
import com.ebay.magellan.tascreed.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tascreed.core.domain.state.partial.Progression;
import com.ebay.magellan.tascreed.core.domain.state.partial.step.StepAllDoneRange;
import com.ebay.magellan.tascreed.core.domain.task.conf.TaskAllConf;
import com.ebay.magellan.tascreed.core.domain.trait.Trait;
import com.ebay.magellan.tascreed.core.domain.trait.Traits;
import com.ebay.magellan.tascreed.depend.common.util.DefaultValueUtil;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class JobStep {
    @JsonProperty("stepName")
    @JsonAlias({"name"})
    private String stepName;

    @JsonProperty("exeClass")
    private String exeClass;

    @JsonProperty("affinityRule")
    private String affinityRule;

    @JsonProperty("ignore")
    private Boolean ignore;

//    @JsonProperty("archive")
//    private Boolean archive;
    @JsonProperty("traits")
    private List<String> traitStrList;
    @JsonIgnore
    private Traits traits = new Traits(Trait.TraitType.STEP);

    @JsonUnwrapped
    private StepAllConf stepAllConf = new StepAllConf();
    @JsonUnwrapped
    private TaskAllCreation taskAllCreation = new TaskAllCreation();

    @JsonProperty("params")
    private Map<String, String> params;

    @JsonProperty("state")
    private StepStateEnum state;
    @JsonUnwrapped
    private Progression progression;

    @JsonProperty("taskStates")
    private Map<Long, TaskStateEnum> taskStates;
    @JsonUnwrapped
    private StepAllDoneRange stepAllDoneRange = new StepAllDoneRange();

    // wrap all the mid state in an object
    @JsonUnwrapped
    private StepMidState midState = new StepMidState();

    @JsonIgnore
    private StepDefine stepDefine;

    public static final Long SIMPLE_TASK_INDEX = -1L;

    public JobStep(StepDefine stepDefine) {
        this.stepDefine = stepDefine;
    }

    public void addParam(String k, String v) {
        if (k == null) return;
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(k, v);
    }

    public StepStateEnum getState() {
        return DefaultValueUtil.defValue(state, StepStateEnum.DORMANT);
    }

    public TaskStateEnum getTaskState(Long index) {
        return DefaultValueUtil.defValue(MapUtils.getObject(taskStates, index), TaskStateEnum.SUCCESS);
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

    // -----

    public boolean needIgnore() {
        return ignore != null ? ignore : false;
    }

    public boolean canFail() {
        return getTraits().containsTrait(Trait.CAN_FAIL);
    }

    // -----

    public void addTaskState(Long k, TaskStateEnum v) {
        if (k == null) return;
        if (taskStates == null) {
            taskStates = new HashMap<>();
        }
        taskStates.put(k, v);
    }

    boolean updateStepState(StepStateEnum newState) {
        if (newState == null || newState.equals(getState())) return false;
        setState(newState);
        midState.setModifyTime(new Date());
        return true;
    }
    boolean updateTaskState(Long index, TaskStateEnum newState) {
        if (newState == null || newState.equals(getTaskState(index))) return false;
        addTaskState(index, newState);
        midState.setModifyTime(new Date());
        return true;
    }

    public boolean updateTaskState(TaskAllConf taskAllConf, TaskStateEnum taskState) {
        if (stepDefine == null) return false;
        if (stepDefine.isShard()) {
            TaskShardCreation crt = getTaskAllCreation().getShardCreation();
            if (crt == null) return false;
            int idx = taskAllConf.getShardConf().getIndex();
            if (idx > crt.getLastShardIndex()) return false;
            return updateTaskState(Long.valueOf(idx), taskState);
        } else if (stepDefine.isPack()) {
            TaskPackCreation crt = getTaskAllCreation().getPackCreation();
            if (crt == null) return false;
            long pid = taskAllConf.getPackConf().getId();
            if (pid > crt.getLastPackId()) return false;
            return updateTaskState(Long.valueOf(pid), taskState);
        } else {
            return updateTaskState(SIMPLE_TASK_INDEX, taskState);
        }
    }

    // update step to done state by task states
    public boolean updateStepStateByTaskStates() {
        if (stepDefine == null) return false;
        if (!getState().isReady()) return false;

        boolean allTasksCreated = true;
        if (stepDefine.isShard()) {
            StepShardConf conf = getStepAllConf().getShardConf();
            TaskShardCreation crt = getTaskAllCreation().getShardCreation();
            allTasksCreated = crt.isDone(conf);
        } else if (stepDefine.isPack()) {
            StepPackConf conf = getStepAllConf().getPackConf();
            TaskPackCreation crt = getTaskAllCreation().getPackCreation();
            allTasksCreated = crt.isDone(conf);
        }

        if (!allTasksCreated) return false;

        List<TaskStateEnum> taskStates = collectTaskStates();
        StepStateEnum newStepState = StepStateEnum.getStepDoneState(
                getState(), taskStates, canFail());

        boolean updated = updateStepState(newStepState);

        return updated;
    }

    public boolean resetForRetry() {
        boolean updated = false;
        if (getState().isError()) {
            updated = updateStepState(StepStateEnum.READY);
        } else if (getState().isSkipByError()) {
            updated = updateStepState(StepStateEnum.DORMANT);
        }

        if (MapUtils.isNotEmpty(taskStates)) {
            for (Map.Entry<Long, TaskStateEnum> entry : taskStates.entrySet()) {
                if (entry.getValue() != null && entry.getValue().canRetry()) {
                    entry.setValue(TaskStateEnum.UNDONE);
                    updated = true;
                }
            }
        }
        return updated;
    }

    // -----

    public void updateStepAllDoneRange(TaskAllConf taskAllConf) {
        if (stepDefine == null) return;
        if (stepDefine.isShard()) {
            int index = taskAllConf.getShardConf().getIndex();
            stepAllDoneRange.shard().addDoneIndex(index);
        } else if (stepDefine.isPack()) {
            long start = taskAllConf.getPackConf().getStart();
            long end = taskAllConf.getPackConf().getEnd();
            stepAllDoneRange.pack().addDoneOffsetRange(start, end);
        }
    }

    // -----

    public int countOnHoldTask() {
        int count = 0;
        if (taskStates != null) {
            for (TaskStateEnum state: taskStates.values()) {
                if (state != null && state.onHold()) {
                    count++;
                }
            }
        }
        return count;
    }

    public List<TaskStateEnum> collectTaskStates() {
        List<TaskStateEnum> list = new ArrayList<>();
        if (taskStates != null) {
            for (TaskStateEnum state: taskStates.values()) {
                if (state != null) {
                    list.add(state);
                }
            }
        }
        return list;
    }

    public void refreshTaskStates() {
        if (taskStates != null) {
            List<Long> successKeys = new ArrayList<>();
            for (Map.Entry<Long, TaskStateEnum> entry : taskStates.entrySet()) {
                TaskStateEnum state = entry.getValue();
                if (state != null && state.isSuccess()) {
                    successKeys.add(entry.getKey());
                }
            }

            for (Long k : successKeys) {
                taskStates.remove(k);
            }
        }
    }

    // -----

    public void updateProgression() {
        if (stepDefine == null) return;

        if (getState().notStarted()) {
            progression = Progression.buildNonProgression();
        } else if (getState().resultSuccess()) {
            progression = Progression.buildFullProgression();
        } else {
            if (stepDefine.isShard()) {
                StepShardConf conf = getStepAllConf().getShardConf();
                TaskShardCreation crt = getTaskAllCreation().getShardCreation();

                int totalTaskNum = conf.getShard();
                int unCreatedTaskNum = crt.isDone(conf) ? 0 : conf.getShard() - (crt.getLastShardIndex() + 1);
                int unDoneTaskNum = 0;
                if (taskStates != null) {
                    unDoneTaskNum = taskStates.values().stream().filter(t -> !t.resultSuccess()).collect(Collectors.toList()).size();
                }
                int doneTaskNum = totalTaskNum - unCreatedTaskNum - unDoneTaskNum;
                progression = Progression.buildProgression(doneTaskNum, totalTaskNum);
            } else if (stepDefine.isPack()) {
                StepPackConf conf = getStepAllConf().getPackConf();
                TaskPackCreation crt = getTaskAllCreation().getPackCreation();

                if (!conf.isInfinite()) {
                    long totalRange = (conf.getEnd() - conf.getStart() + 1);
                    long unCreatedRange = crt.isDone(conf) ? 0 : (conf.getEnd() - crt.getLastOffset() + 1);
                    long unDoneRange = 0L;
                    if (taskStates != null) {
                        unDoneRange = taskStates.values().stream().filter(t -> !t.resultSuccess()).collect(Collectors.toList()).size() * conf.getSize();
                    }
                    long doneRange = totalRange - unCreatedRange - unDoneRange;
                    progression = Progression.buildProgression(doneRange, totalRange);
                } else {
                    progression = Progression.buildNonProgression();
                }
            } else {
                int totalTaskNum = 1;
                int unDoneTaskNum = 0;
                if (taskStates != null) {
                    unDoneTaskNum = taskStates.values().stream().filter(t -> !t.resultSuccess()).collect(Collectors.toList()).size();
                }
                int doneTaskNum = totalTaskNum - unDoneTaskNum;
                progression = Progression.buildProgression(doneTaskNum, totalTaskNum);
            }
        }
    }

}