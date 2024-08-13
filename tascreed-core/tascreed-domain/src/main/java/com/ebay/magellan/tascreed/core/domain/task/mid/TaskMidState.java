package com.ebay.magellan.tascreed.core.domain.task.mid;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TaskMidState {
    // optional, task pick after a time, by default no limit
    // when error and need to retry, it will be set a future time for back-off retry
    @JsonProperty("after")
    private Date afterTime;

    // expect execute duration of task, in milliseconds
    @JsonProperty("duration")
    private Long duration;

    // picked times, indicates the task has been picked (but not done) for how many times, by default 0;
    // when we have to drop this task (due to fatal exception) and let other workers can pick it, increase this counter.
    @JsonProperty("pickedTimes")
    @JsonAlias({"errorTimes"})
    private Integer pickedTimes;
    // try times, indicates the task has been tried (but not done) for how many times, by default 0;
    // when we will retry the task execution (due to retryable exception or fatal exception), increase this counter.
    @JsonProperty("triedTimes")
    private Integer triedTimes;

    @JsonProperty("createTime")
    private Date createTime;
    @JsonProperty("modifyTime")
    private Date modifyTime;
    @JsonProperty("modifyThread")
    private String modifyThread;

    // -----

    // picked times

    public void increasePickedTimes() {
        if (pickedTimes == null) {
            pickedTimes = Integer.valueOf(0);
        }
        if (pickedTimes < Integer.MAX_VALUE) {
            pickedTimes += 1;
        }
    }

    public int pickedTimesValue() {
        return pickedTimes != null ? pickedTimes.intValue() : 0;
    }

    void resetPickedTimes() {
        pickedTimes = null;
    }

    // tried times

    public void increaseTriedTimes() {
        if (triedTimes == null) {
            triedTimes = Integer.valueOf(0);
        }
        if (triedTimes < Integer.MAX_VALUE) {
            triedTimes += 1;
        }
    }

    public int triedTimesValue() {
        return triedTimes != null ? triedTimes.intValue() : 0;
    }

    void resetTriedTimes() {
        triedTimes = null;
    }

    // all retry times

    public void resetAllRetryTimes() {
        resetPickedTimes();
        resetTriedTimes();
    }

    // -----
}
