package com.ebay.magellan.tascreed.core.infra.executor.help;

import com.ebay.magellan.tascreed.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tascreed.core.domain.task.TaskResult;
import com.ebay.magellan.tascreed.core.infra.executor.NormalTaskExecutor;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;

public class TestNmExecutor extends NormalTaskExecutor {
    int count = 0;

    public int getCount() {
        return count;
    }

    @Override
    protected void initImpl() throws TumblerException {
        ;
    }

    @Override
    protected TaskResult executeImpl() throws TumblerException {
        count++;
        TaskResult result = new TaskResult(TaskStateEnum.SUCCESS, "");
        return result;
    }

    @Override
    protected void closeImpl() throws TumblerException {
        ;
    }
}
