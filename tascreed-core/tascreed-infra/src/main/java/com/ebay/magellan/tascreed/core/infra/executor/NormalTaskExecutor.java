package com.ebay.magellan.tascreed.core.infra.executor;

import com.ebay.magellan.tascreed.core.domain.task.TaskResult;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerExceptionBuilder;

public abstract class NormalTaskExecutor extends TaskExecutor {

    @Override
    public final boolean checkpointEnabled() {
        return false;
    }

    @Override
    protected final TaskResult executeRoundImpl() throws TumblerException {
        TumblerExceptionBuilder.throwTumblerException(TumblerErrorEnum.TUMBLER_FATAL_TASK_EXCEPTION,
                String.format("NormalTaskExecutor should not call executeRoundImpl method!"));
        return null;
    }

}
