package com.ebay.magellan.tascreed.core.infra.routine.execute;

import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;

public abstract class NormalRoutineExecutor extends RoutineExecutor {

    @Override
    public final boolean checkpointEnabled() {
        return false;
    }

    protected abstract void executeRoundImpl() throws TumblerException;

    protected abstract void closeImpl() throws TumblerException;
}
