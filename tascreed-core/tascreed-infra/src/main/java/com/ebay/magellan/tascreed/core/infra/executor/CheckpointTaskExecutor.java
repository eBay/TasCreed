package com.ebay.magellan.tascreed.core.infra.executor;

import com.ebay.magellan.tascreed.core.domain.state.partial.TaskCheckpoint;
import com.ebay.magellan.tascreed.core.domain.task.TaskResult;
import com.ebay.magellan.tascreed.core.infra.executor.checkpoint.TaskExecCheckpoint;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;

public abstract class CheckpointTaskExecutor<C extends TaskExecCheckpoint> extends TaskExecutor {

    protected C checkpoint;

    // -----

    @Override
    public final boolean checkpointEnabled() {
        return true;
    }

    @Override
    protected final TaskResult executeImpl() throws TcException {
        TcExceptionBuilder.throwTcException(TcErrorEnum.TC_FATAL_TASK_EXCEPTION,
                String.format("CheckpointTaskExecutor should not call executeImpl method!"));
        return null;
    }

    protected TaskCheckpoint buildCheckpoint() {
        if (checkpoint == null) return null;
        return checkpoint.toValue();
    }

}
