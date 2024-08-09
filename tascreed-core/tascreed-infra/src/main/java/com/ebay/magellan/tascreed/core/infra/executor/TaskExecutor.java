package com.ebay.magellan.tascreed.core.infra.executor;

import com.ebay.magellan.tascreed.core.domain.state.partial.Progression;
import com.ebay.magellan.tascreed.core.infra.executor.alive.TaskOccupation;
import com.ebay.magellan.tascreed.core.infra.executor.progression.TaskExecProgression;
import com.ebay.magellan.tascreed.core.infra.monitor.Metrics;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.domain.task.TaskResult;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import lombok.Getter;

@Getter
public abstract class TaskExecutor {

    protected Task task;

    protected TaskOccupation taskOccupation;        // for task occupation check

    protected TaskExecProgression progression;      // for checkpoint

    // -----

    public abstract boolean checkpointEnabled();

    // -----

    protected abstract void initImpl() throws TcException;

    protected abstract TaskResult executeImpl() throws TcException;
    protected abstract TaskResult executeRoundImpl() throws TcException;

    protected abstract void closeImpl() throws TcException;

    // -----

    protected TaskResult tryExecuteImpl() throws TcException {
        try {
            return executeImpl();
        } catch (TcException e) {
            Metrics.reportExecutionExceptionCounter(e.getError());
            throw e;
        }
    }
    protected TaskResult tryExecuteRoundImpl() throws TcException {
        try {
            return executeRoundImpl();
        } catch (TcException e) {
            Metrics.reportExecutionExceptionCounter(e.getError());
            throw e;
        }
    }

    // -----

    public final void init(Task task, TaskOccupation taskOccupation) throws TcException {
        this.task = task;
        this.taskOccupation = taskOccupation;
        this.progression = new TaskExecProgression();
        initImpl();
    }

    public final void execute() throws TcException {
        TaskStateEnum curState = task.getTaskState();
        if (curState.isUndone()) {
            TaskResult result = tryExecuteImpl();
            task.setResult(result);
        }
    }

    public final void executeRound() throws TcException {
        TaskStateEnum curState = task.getTaskState();
        if (curState.isUndone()) {
            TaskResult result = tryExecuteRoundImpl();
            task.setResult(result);
        }
    }

    public final void close() throws TcException {
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
     * @throws TcException if occupation check fails
     */
    protected void taskOccupationCheck() throws TcException {
        boolean occupied = taskStillOccupied();
        if (!occupied) {
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_NON_RETRY_HEARTBEAT_EXCEPTION,
                    "task occupation check fails, will end task execution");
        }
    }

}
