package com.ebay.magellan.tumbler.core.infra.executor;

import com.ebay.magellan.tumbler.core.domain.state.partial.Progression;
import com.ebay.magellan.tumbler.core.infra.executor.alive.TaskOccupation;
import com.ebay.magellan.tumbler.core.infra.executor.progression.TaskExecProgression;
import com.ebay.magellan.tumbler.core.infra.monitor.Metrics;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerErrorEnum;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;
import com.ebay.magellan.tumbler.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tumbler.core.domain.task.Task;
import com.ebay.magellan.tumbler.core.domain.task.TaskResult;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerExceptionBuilder;
import lombok.Getter;

@Getter
public abstract class TaskExecutor {

    protected Task task;

    protected TaskOccupation taskOccupation;        // for task occupation check

    protected TaskExecProgression progression;      // for checkpoint

    // -----

    public abstract boolean checkpointEnabled();

    // -----

    protected abstract void initImpl() throws TumblerException;

    protected abstract TaskResult executeImpl() throws TumblerException;
    protected abstract TaskResult executeRoundImpl() throws TumblerException;

    protected abstract void closeImpl() throws TumblerException;

    // -----

    protected TaskResult tryExecuteImpl() throws TumblerException {
        try {
            return executeImpl();
        } catch (TumblerException e) {
            Metrics.reportExecutionExceptionCounter(e.getError());
            throw e;
        }
    }
    protected TaskResult tryExecuteRoundImpl() throws TumblerException {
        try {
            return executeRoundImpl();
        } catch (TumblerException e) {
            Metrics.reportExecutionExceptionCounter(e.getError());
            throw e;
        }
    }

    // -----

    public final void init(Task task, TaskOccupation taskOccupation) throws TumblerException {
        this.task = task;
        this.taskOccupation = taskOccupation;
        this.progression = new TaskExecProgression();
        initImpl();
    }

    public final void execute() throws TumblerException {
        TaskStateEnum curState = task.getTaskState();
        if (curState.isUndone()) {
            TaskResult result = tryExecuteImpl();
            task.setResult(result);
        }
    }

    public final void executeRound() throws TumblerException {
        TaskStateEnum curState = task.getTaskState();
        if (curState.isUndone()) {
            TaskResult result = tryExecuteRoundImpl();
            task.setResult(result);
        }
    }

    public final void close() throws TumblerException {
        closeImpl();
    }

    // -----

    protected Progression buildProgression() {
        if (progression == null) return null;
        return progression.toValue();
    }

    // -----

    /**
     * task executor can check if the worker still occupies this task;
     * users might do this check before data persistence to storage
     * @return true if the task is still occupied; otherwise false
     */
    protected boolean taskStillOccupied() {
        if (taskOccupation == null) return false;
        return taskOccupation.taskStillOccupied();
    }

    /**
     * users can do mandatory check of task occupation at any critical point,
     * and throw non-retryable exception if check fails
     * @throws TumblerException if occupation check fails
     */
    protected void taskOccupationCheck() throws TumblerException {
        boolean occupied = taskStillOccupied();
        if (!occupied) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_NON_RETRY_HEARTBEAT_EXCEPTION,
                    "task occupation check fails, will end task execution");
        }
    }

}
