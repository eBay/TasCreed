package com.ebay.magellan.tascreed.core.domain.state;

import com.ebay.magellan.tascreed.core.domain.state.basic.DoneEnum;
import com.ebay.magellan.tascreed.core.domain.state.basic.ResultEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * step state
 */
public enum StepStateEnum {
    /**
     * undone state with unknown result
     */
    DORMANT(DoneEnum.UNDONE, ResultEnum.UNKNOWN),     // dormant step
    START(DoneEnum.UNDONE, ResultEnum.UNKNOWN),       // start to create task
    READY(DoneEnum.UNDONE, ResultEnum.UNKNOWN),     // task created
    /**
     * done state
     */
    SUCCESS(DoneEnum.DONE, ResultEnum.SUCCESS), // all tasks success
    IGNORED(DoneEnum.DONE, ResultEnum.SUCCESS),       // ignored step, ignore by request
    ACCEPTABLE_FAILED(DoneEnum.DONE, ResultEnum.SUCCESS), // any task failed, but the step failure is acceptable
    FAILED(DoneEnum.DONE, ResultEnum.FAILED),   // any task failed, and the step failure is not acceptable
    ERROR(DoneEnum.DONE, ResultEnum.ERROR),     // any task error

    SKIP_BY_FAILED(DoneEnum.DONE, ResultEnum.FAILED),   // skip step due to dependent step failed
    SKIP_BY_ERROR(DoneEnum.DONE, ResultEnum.ERROR),   // skip step due to dependent step failed
    ;

    /**
     * DORMANT -> START: step is independent, start to creat tasks
     * START -> READY: all tasks of this step are created
     *
     * READY -> SUCCESS: all tasks of this step are success
     * READY -> ACCEPTABLE_FAILED: any task of this step is failed, and the step can fail
     * READY -> FAILED: any task of this step is failed, and the step can not fail
     * READY -> ERROR: any task of this step is error
     *
     * DORMANT -> IGNORED: step is ignored by step request, no task will be created, treat as SUCCESS
     * DORMANT -> ACCEPTABLE_FAILED: skip step mark result as success, if any dependent step has failed result, and this step can fail
     * DORMANT -> SKIP_BY_FAILED: skip step mark result as failed, if any dependent step has failed result, and this step can not fail
     * DORMANT -> SKIP_BY_ERROR: skip step mark result as error, if any dependent step has error result
     */

    private DoneEnum done;
    private ResultEnum result;

    StepStateEnum(DoneEnum doneState, ResultEnum result) {
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
        return isDormant() || isStart();
    }

    public boolean notStarted() {
        return isDormant() || isSkipByFailed() || isSkipByError();
    }

    public boolean canRetry() {
        return isError() || isSkipByError();
    }

    // -----

    public boolean isDormant() {
        return this.equals(DORMANT);
    }
    public boolean isStart() {
        return this.equals(START);
    }
    public boolean isReady() {
        return this.equals(READY);
    }

    public boolean isSuccess() {
        return this.equals(SUCCESS);
    }
    public boolean isIgnored() {
        return this.equals(IGNORED);
    }
    public boolean isAcceptableFailed() {
        return this.equals(ACCEPTABLE_FAILED);
    }
    public boolean isFailed() {
        return this.equals(FAILED);
    }
    public boolean isError() {
        return this.equals(ERROR);
    }

    public boolean isSkipByFailed() {
        return this.equals(SKIP_BY_FAILED);
    }
    public boolean isSkipByError() {
        return this.equals(SKIP_BY_ERROR);
    }

    // -----

    /**
     * undone: any task undone
     * error: any task error
     * fail: any task fail, can not fail
     * acceptable fail: any task fail, can fail
     * success: all tasks success
     * @param oldStepState old step state
     * @param taskStates list of task states
     * @param canFail can fail or not
     * @return new step state
     */
    public static StepStateEnum getStepDoneState(StepStateEnum oldStepState,
                                                 List<TaskStateEnum> taskStates,
                                                 boolean canFail) {
        if (CollectionUtils.isEmpty(taskStates)) return null;
        boolean undone = false;
        boolean success = true;
        boolean failed = false;
        boolean error = false;
        for (TaskStateEnum taskState : taskStates) {
            undone = undone || taskState.isUndone();
            success = success && taskState.isSuccess();
            failed = failed || taskState.isFailed();
            error = error || taskState.isError();
        }

        if (undone) return oldStepState;
        if (error) return ERROR;
        if (failed) return canFail ? ACCEPTABLE_FAILED : FAILED;
        if (success) return SUCCESS;
        return oldStepState;
    }
}
