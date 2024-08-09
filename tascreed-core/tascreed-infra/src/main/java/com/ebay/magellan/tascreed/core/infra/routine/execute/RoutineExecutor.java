package com.ebay.magellan.tascreed.core.infra.routine.execute;

import com.ebay.magellan.tascreed.core.domain.routine.Routine;
import com.ebay.magellan.tascreed.core.infra.monitor.Metrics;
import com.ebay.magellan.tascreed.core.infra.routine.alive.RoutineOccupation;
import com.ebay.magellan.tascreed.core.infra.routine.context.RoutineTriggerContext;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import lombok.Getter;

@Getter
public abstract class RoutineExecutor {

    protected Routine routine;

    // for routine occupation check
    protected RoutineOccupation routineOccupation;

    // users can get routine trigger information like trigger round, etc.
    protected RoutineTriggerContext routineTriggerContext = new RoutineTriggerContext();

    // -----

    public abstract boolean checkpointEnabled();

    // -----

    protected abstract void initImpl() throws TcException;

    protected abstract void executeRoundImpl() throws TcException;

    protected abstract void closeImpl() throws TcException;

    // -----

    protected void tryExecuteRoundImpl() throws TcException {
        try {
            executeRoundImpl();
        } catch (TcException e) {
            Metrics.reportExecutionExceptionCounter(e.getError());
            throw e;
        }
    }

    // -----

    public String printInfo() {
        String routineFullName = routine != null ? routine.getFullName() : "";
        String routineContextStr = routineTriggerContext != null ? routineTriggerContext.getStr() : "";
        return String.format("%s (%s)", routineFullName, routineContextStr);
    }

    // -----

    public final void init(Routine routine, RoutineOccupation routineOccupation) throws TcException {
        this.routine = routine;
        this.routineOccupation = routineOccupation;
        this.routineTriggerContext.init();
        initImpl();
    }

    public final void executeRound() throws TcException {
        long st = System.currentTimeMillis();

        routineTriggerContext.trigger();
        tryExecuteRoundImpl();

        long et = System.currentTimeMillis();
        Metrics.routineRoundExecSummary.labels(routine.getFullName()).observe(et - st);
    }

    public final void close() throws TcException {
        closeImpl();
    }

    // -----

    /**
     * routine executor can check if the thread still occupies this routine;
     * users might do this check before data persistence to storage
     * @return true if the routine is still occupied; otherwise false
     */
    protected boolean routineStillOccupied() {
        if (routineOccupation == null) return false;
        return routineOccupation.routineStillOccupied();
    }

    /**
     * users can do mandatory check of routine occupation at any critical point,
     * and throw non-retryable exception if check fails
     * @throws TcException if occupation check fails
     */
    protected void routineOccupationCheck() throws TcException {
        boolean occupied = routineStillOccupied();
        if (!occupied) {
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_NON_RETRY_HEARTBEAT_EXCEPTION,
                    "routine occupation check fails, will end task execution");
        }
    }
}
