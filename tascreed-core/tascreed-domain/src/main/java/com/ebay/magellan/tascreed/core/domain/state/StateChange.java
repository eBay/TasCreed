package com.ebay.magellan.tascreed.core.domain.state;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StateChange {
    private boolean jobStateChanged;
    private boolean stepStateChanged;
    private boolean taskStateChanged;

    public static StateChange init() {
        return new StateChange(false, false, false);
    }

    public void changeJobState(boolean b) {
        jobStateChanged = b || jobStateChanged;
    }
    public void changeStepState(boolean b) {
        stepStateChanged = b || stepStateChanged;
    }
    public void changeTaskState(boolean b) {
        taskStateChanged = b || taskStateChanged;
    }

    public boolean anyStateChanged() {
        return taskStateChanged || stepStateChanged || jobStateChanged;
    }
}
