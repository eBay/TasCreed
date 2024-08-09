package com.ebay.magellan.tumbler.core.domain.task;

import com.ebay.magellan.tumbler.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tumbler.core.domain.state.partial.TaskCheckpoint;
import com.ebay.magellan.tumbler.core.domain.state.partial.Progression;
import com.ebay.magellan.tumbler.depend.common.util.DefaultValueUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TaskResult {
    @JsonView(TaskViews.TASK_DONE.class)
    @JsonProperty("state")
    private TaskStateEnum state;
    @JsonView(TaskViews.TASK_DONE.class)
    @JsonProperty("reason")
    private String reason;

    @JsonUnwrapped
    private TaskCheckpoint checkpoint;
    @JsonUnwrapped
    private Progression progression;

    public TaskStateEnum getState() {
        return DefaultValueUtil.defValue(state, TaskStateEnum.UNDONE);
    }

    // -----

    public TaskResult(TaskStateEnum state, String reason) {
        this.state = state;
        this.reason = reason;
    }

    public TaskResult(TaskStateEnum state, String reason, TaskCheckpoint checkpoint) {
        this.state = state;
        this.reason = reason;
        this.checkpoint = checkpoint;
    }
}