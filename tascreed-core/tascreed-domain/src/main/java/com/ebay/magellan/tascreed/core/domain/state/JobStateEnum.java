package com.ebay.magellan.tascreed.core.domain.state;

import com.ebay.magellan.tascreed.core.domain.state.basic.DoneEnum;
import com.ebay.magellan.tascreed.core.domain.state.basic.ResultEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * job state
 */
public enum JobStateEnum {
    /**
     * undone state
     */
    UNDONE(DoneEnum.UNDONE, ResultEnum.UNKNOWN),    // undone
    STUCK(DoneEnum.UNDONE, ResultEnum.ERROR),       // stuck, can not be done
    /**
     * done state
     */
    SUCCESS(DoneEnum.DONE, ResultEnum.SUCCESS), // all steps with success result
    FAILED(DoneEnum.DONE, ResultEnum.FAILED),   // any step with failed result
    ERROR(DoneEnum.DONE, ResultEnum.ERROR),     // any step with error result
    ;

    private DoneEnum done;
    private ResultEnum result;

    JobStateEnum(DoneEnum doneState, ResultEnum result) {
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

    public boolean canCreateTask() {
        return isUndone();
    }

    public boolean canAutoArchive() {
        return isSuccess() || isFailed();
    }

    public boolean canRetry() {
        return isError();
    }

    // -----

    public boolean isUndone() {
        return this.equals(UNDONE);
    }
    public boolean isStuck() {
        return this.equals(STUCK);
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

    // -----

    /**
     * undone: any step undone
     * error: any step result error
     * fail: any step fail
     * success: all steps success
     * @param oldJobState old job state
     * @param stepStates list of step states
     * @return new job state
     */
    public static JobStateEnum getJobState(JobStateEnum oldJobState,
                                           List<StepStateEnum> stepStates) {
        if (CollectionUtils.isEmpty(stepStates)) return null;
        boolean undone = false;
        boolean success = true;
        boolean failed = false;
        boolean error = false;
        for (StepStateEnum stepState : stepStates) {
            undone = undone || stepState.resultUnknown();
            success = success && stepState.resultSuccess();
            failed = failed || stepState.resultFailed();
            error = error || stepState.resultError();
        }

        if (undone) return oldJobState;
        if (error) return ERROR;
        if (failed) return FAILED;
        if (success) return SUCCESS;
        return oldJobState;
    }

}
