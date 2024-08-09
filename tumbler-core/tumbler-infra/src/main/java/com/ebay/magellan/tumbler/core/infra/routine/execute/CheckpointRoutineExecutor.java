package com.ebay.magellan.tumbler.core.infra.routine.execute;

import com.ebay.magellan.tumbler.core.infra.routine.execute.checkpoint.RoutineExecCheckpoint;

public abstract class CheckpointRoutineExecutor<C extends RoutineExecCheckpoint> extends RoutineExecutor {

    protected C checkpoint;

    // -----

    @Override
    public final boolean checkpointEnabled() {
        return true;
    }

    protected String getCheckpointValue() {
        if (checkpoint == null) return null;
        return checkpoint.toValue();
    }

}
