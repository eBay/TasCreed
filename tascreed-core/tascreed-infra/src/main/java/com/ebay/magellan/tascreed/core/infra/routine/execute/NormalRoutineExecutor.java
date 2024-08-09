package com.ebay.magellan.tascreed.core.infra.routine.execute;

import com.ebay.magellan.tascreed.depend.common.exception.TcException;

public abstract class NormalRoutineExecutor extends RoutineExecutor {

    @Override
    public final boolean checkpointEnabled() {
        return false;
    }

    protected abstract void executeRoundImpl() throws TcException;

    protected abstract void closeImpl() throws TcException;
}
