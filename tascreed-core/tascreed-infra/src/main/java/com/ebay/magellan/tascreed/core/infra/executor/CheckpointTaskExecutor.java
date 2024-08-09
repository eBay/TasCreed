package com.ebay.magellan.tascreed.core.infra.executor;

import com.ebay.magellan.tascreed.core.domain.state.partial.TaskCheckpoint;
import com.ebay.magellan.tascreed.core.domain.task.TaskResult;
import com.ebay.magellan.tascreed.core.infra.executor.checkpoint.TaskExecCheckpoint;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerExceptionBuilder;

public abstract class CheckpointTaskExecutor<C extends TaskExecCheckpoint> extends TaskExecutor {

    protected C checkpoint;

    // -----

    @Override
    public final boolean checkpointEnabled() {
        return true;
    }

    @Override
    protected final TaskResult executeImpl() throws TumblerException {
        TumblerExceptionBuilder.throwTumblerException(TumblerErrorEnum.TUMBLER_FATAL_TASK_EXCEPTION,
                String.format("CheckpointTaskExecutor should not call executeImpl method!"));
        return null;
    }

    protected TaskCheckpoint buildCheckpoint() {
        if (checkpoint == null) return null;
        return checkpoint.toValue();
    }

}
