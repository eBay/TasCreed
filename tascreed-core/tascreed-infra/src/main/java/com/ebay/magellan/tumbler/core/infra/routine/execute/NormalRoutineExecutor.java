package com.ebay.magellan.tumbler.core.infra.routine.execute;

import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;

public abstract class NormalRoutineExecutor extends RoutineExecutor {

    @Override
    public final boolean checkpointEnabled() {
        return false;
    }

    protected abstract void executeRoundImpl() throws TumblerException;

    protected abstract void closeImpl() throws TumblerException;
}
