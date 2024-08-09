package com.ebay.magellan.tascreed.core.infra.executor;

import com.ebay.magellan.tascreed.core.domain.task.TaskResult;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;

public abstract class NormalTaskExecutor extends TaskExecutor {

    @Override
    public final boolean checkpointEnabled() {
        return false;
    }

    @Override
    protected final TaskResult executeRoundImpl() throws TcException {
        TcExceptionBuilder.throwTumblerException(TcErrorEnum.TUMBLER_FATAL_TASK_EXCEPTION,
                String.format("NormalTaskExecutor should not call executeRoundImpl method!"));
        return null;
    }

}
