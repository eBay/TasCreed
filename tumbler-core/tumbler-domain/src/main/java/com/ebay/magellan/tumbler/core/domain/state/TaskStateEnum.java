package com.ebay.magellan.tumbler.core.domain.state;

import com.ebay.magellan.tumbler.core.domain.state.basic.DoneEnum;
import com.ebay.magellan.tumbler.core.domain.state.basic.ResultEnum;

/**
 * task state
 */
public enum TaskStateEnum {
    /**
     * undone state
     */
    UNDONE(DoneEnum.UNDONE, ResultEnum.UNKNOWN),
    /**
     * done state
     */
    SUCCESS(DoneEnum.DONE, ResultEnum.SUCCESS),
    FAILED(DoneEnum.DONE, ResultEnum.FAILED),
    ERROR(DoneEnum.DONE, ResultEnum.ERROR),
    ;

    private DoneEnum done;
    private ResultEnum result;

    TaskStateEnum(DoneEnum doneState, ResultEnum result) {
        this.done = doneState;
        this.result = result;
    }

    // -----

    public boolean undone() {
        return DoneEnum.UNDONE.equals(done);
    }
    public boolean done() {
        return DoneEnum.DONE.equals(done);
    }

    public boolean resultUnknown() {
        return ResultEnum.UNKNOWN.equals(result);
    }
    public boolean resultSuccess() {
        return ResultEnum.SUCCESS.equals(result);
    }
    public boolean resultFailed() {
        return ResultEnum.FAILED.equals(result);
    }
    public boolean resultError() {
        return ResultEnum.ERROR.equals(result);
    }

    // -----

    public boolean onHold() {
        return isUndone();
    }

    public boolean canRetry() {
        return isError();
    }

    // -----

    public boolean isUndone() {
        return this.equals(UNDONE);
    }
    public boolean isSuccess() {
        return this.equals(SUCCESS);
    }
    public boolean isFailed() {
        return this.equals(FAILED);
    }
    public boolean isError() {
        return this.equals(ERROR);
    }
}
