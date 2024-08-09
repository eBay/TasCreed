package com.ebay.magellan.tascreed.core.infra.routine;

import com.ebay.magellan.tascreed.core.domain.ban.BanContext;
import com.ebay.magellan.tascreed.core.domain.occupy.OccupyInfo;
import com.ebay.magellan.tascreed.core.domain.ban.BanLevelEnum;
import com.ebay.magellan.tascreed.core.domain.duty.NodeDutyEnum;
import com.ebay.magellan.tascreed.core.domain.routine.Routine;
import com.ebay.magellan.tascreed.core.infra.ban.BanHelper;
import com.ebay.magellan.tascreed.core.infra.conf.TcGlobalConfig;
import com.ebay.magellan.tascreed.core.infra.duty.DutyHelper;
import com.ebay.magellan.tascreed.core.infra.routine.execute.RoutineExecutor;
import com.ebay.magellan.tascreed.core.infra.routine.execute.RoutineExecutorFactory;
import com.ebay.magellan.tascreed.core.infra.routine.alive.RoutineOccupation;
import com.ebay.magellan.tascreed.core.infra.routine.heartbeat.RoutineHeartBeatThread;
import com.ebay.magellan.tascreed.core.infra.routine.heartbeat.RoutineHeartBeatThreadFactory;
import com.ebay.magellan.tascreed.core.infra.routine.heartbeat.RoutineHeartBeatThreadPoolExecutor;
import com.ebay.magellan.tascreed.core.infra.routine.help.RoutineOccupyHelper;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.RoutineBulletin;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.common.retry.RetryBackoffStrategy;
import com.ebay.magellan.tascreed.depend.common.retry.RetryCounter;
import com.ebay.magellan.tascreed.depend.common.retry.RetryCounterFactory;
import com.ebay.magellan.tascreed.depend.common.retry.RetryStrategy;
import com.ebay.magellan.tascreed.depend.common.util.ExceptionUtil;
import com.ebay.magellan.tascreed.depend.common.util.ThreadUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Scope("prototype")
public class RoutineThread implements Runnable {
    private static final String THIS_CLASS_NAME = RoutineThread.class.getSimpleName();

    private RetryStrategy retryStrategyForOccupy = RetryBackoffStrategy.newDefaultInstance();
    private RetryStrategy retryStrategyForProcess = RetryBackoffStrategy.newDefaultInstance();

    @Autowired
    private TcGlobalConfig tcGlobalConfig;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TcLogger logger;

    @Autowired
    private RoutineExecutorFactory routineExecutorFactory;

    @Autowired
    private RoutineHeartBeatThreadFactory heartBeatThreadFactory;
    @Autowired
    private RoutineHeartBeatThreadPoolExecutor heartBeatThreadPoolExecutor;

    @Autowired
    private RoutineThreadPoolExecutor routineThreadPoolExecutor;

    @Autowired
    private RoutineBulletin routineBulletin;

    @Autowired
    private RoutineOccupyHelper routineOccupyHelper;

    @Autowired
    private BanHelper banHelper;

    @Autowired
    private DutyHelper dutyHelper;

    private RoutineExecutor routineExecutor;
    private RoutineOccupation routineOccupation;

    private volatile Routine occupiedRoutine = null;

    private volatile RoutineHeartBeatThread heartBeatThread;

    private volatile String threadName;

    public String getThreadName() {
        if (threadName == null) {
            threadName = ThreadUtil.getCurrentThreadName();
        }
        return threadName;
    }

    private boolean occupied() {
        return occupiedRoutine != null;
    }

    boolean isRoutineAlive() {
        return occupiedRoutine != null &&
                occupiedRoutine.getOccupyInfo() != null &&
                occupiedRoutine.getOccupyInfo().isAlive();
    }

    // -----

    @Override
    public void run() {
        try {
            /**
             * if one routine is finished, need to pick another routine to work,
             * so need loop here
             */
            while (true) {
                /**
                 * 1. check if current thread needs to be terminated
                 */
                if (needToEndThread()) {
                    logger.warn(THIS_CLASS_NAME,
                            String.format("%s needs to be terminated", getThreadName()));
                    break;
                }

                /**
                 * 2. try to occupy a routine
                 */
                long startTimeMs = System.currentTimeMillis();
                tryOccupyRoutine();

                /**
                 * 3. if occupied a routine, start working;
                 * if occupy failed, retry the loop with backoff
                 */
                if (occupied()) {
                    String msg = String.format("%s successfully occupied routine [%s], occupy time cost is %d ms",
                            getThreadName(), occupiedRoutine.getFullName(), System.currentTimeMillis() - startTimeMs);
                    logger.info(THIS_CLASS_NAME, msg);
                    // try work on routine
                    tryWorkOnRoutine();
                    logger.info(THIS_CLASS_NAME,
                            String.format("routine %s is finished or switch is off, total time cost is %d ms",
                                    occupiedRoutine.getFullName(), System.currentTimeMillis() - startTimeMs));

                    // release occupied routine
                    occupiedRoutine = null;
                } else {
                    // if can't occupy any pack, just return
                    break;
                }
            }
        } catch (TcException e) {
            logger.error(THIS_CLASS_NAME,
                    String.format("%s failed with TasCreed exception:\n%s", getThreadName(), ExceptionUtil.getStackTrace(e)));
        } catch (Exception e) {
            logger.error(THIS_CLASS_NAME,
                    String.format("%s failed with exception:\n%s", getThreadName(), ExceptionUtil.getStackTrace(e)));
        } catch (Throwable e) {
            logger.error(THIS_CLASS_NAME,
                    String.format("%s failed with unknown throwable:\n%s", getThreadName(), ExceptionUtil.getStackTrace(e)));
        } finally {
            exit();
        }
    }

    // need to end this routine thread if it is interrupted or thread pool size already exceeds max num
    boolean needToEndThread() {
        return Thread.interrupted() || routineThreadPoolExecutor.poolSizeExceeded();
    }

    void tryOccupyRoutine() throws TcException {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.ROUTINE_EXECUTOR);

        occupiedRoutine = null;
        logger.info(THIS_CLASS_NAME,
                String.format("%s trying to occupy a routine...", getThreadName()));
        RetryCounter retryCounter = RetryCounterFactory.buildRetryCounter(retryStrategyForOccupy);
        while (tcGlobalConfig.isRoutineWatcherSwitchOn(true)) {
            try {
                occupiedRoutine = routineOccupyHelper.tryOccupyOneRoutine(getThreadName());
                if (occupiedRoutine == null) {
                    logger.info(THIS_CLASS_NAME,
                            String.format("%s can't occupy any routine", getThreadName()));
                }
                return;
            } catch (TcException e) {
                if (e.isRetry()) {     // retry-able
                    retryCounter.grow();
                    String head = String.format("occupy routine fails by retryable exception:\n%s", ExceptionUtil.getStackTrace(e));
                    logger.warn(THIS_CLASS_NAME, retryCounter.status(head));
                } else {        // nont-retry-able
                    retryCounter.forceStop();
                    String head = String.format("occupy routine fails by non-retryable exception:\n%s", ExceptionUtil.getStackTrace(e));
                    logger.error(THIS_CLASS_NAME, retryCounter.status(head));
                }
            } catch (Throwable e) {        //NOSONAR
                // unknown exception, retry
                retryCounter.grow();
                String head = String.format("occupy routine fails by unknown exception/throwable:\n%s", ExceptionUtil.getStackTrace(e));
                logger.error(THIS_CLASS_NAME, retryCounter.status(head));
            }

            if (!retryCounter.isAlive()) {
                break;
            }
            retryCounter.waitForNextRetry();
        }
    }

    // build routine executor
    void buildRoutineExecutor() throws TcException {
        routineExecutor = routineExecutorFactory.buildRoutineExecutor(occupiedRoutine);
        if (routineExecutor == null) {
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_NON_RETRY_EXCEPTION,
                    String.format("build routineExecutor for routine [%s] fails", occupiedRoutine.getRoutineName()));
        }
    }

    // build routine aliveness
    void buildRoutineOccupation() {
        routineOccupation = routineExecutorFactory.buildRoutineOccupation(occupiedRoutine, getThreadName(), routineBulletin);
    }

    void enableAsyncHeartBeatThread() throws TcException {
        OccupyInfo occupyInfo = occupiedRoutine.getOccupyInfo();
        if (heartBeatThread != null && heartBeatThread.isActive()) {
            heartBeatThread.setOccupiedInfo(occupyInfo);
        } else {
            heartBeatThread = heartBeatThreadFactory.buildHeartBeatThread(this);
            if (heartBeatThreadPoolExecutor.hasVacancy()) {
                heartBeatThreadPoolExecutor.submit(heartBeatThread);
            } else {
                TcExceptionBuilder.throwTcException(
                        TcErrorEnum.TC_NON_RETRY_EXCEPTION,
                        String.format("routine [%s] fails to submit heart beat thread", occupyInfo.getOccupyKey()));
            }
        }
    }

    // build and init routine executor, as well as heartbeat
    void routineStartUp() throws TcException {
        buildRoutineExecutor();
        buildRoutineOccupation();

        routineExecutor.init(occupiedRoutine, routineOccupation);

        enableAsyncHeartBeatThread();
    }

    // try work on routine
    void tryWorkOnRoutine() throws TcException {
        try {
            workOnRoutine();
        } catch (TcException e) {
            logger.error(THIS_CLASS_NAME,
                    String.format("routine [%s] error: %s", occupiedRoutine.getFullName(), e.getMessage()));
            throw e;
        } finally {
            finishRoutine();
        }
    }

    // while loop to work
    void workOnRoutine() throws TcException {
        // build and init routine executor, as well as heartbeat
        routineStartUp();

        RetryCounter retryCounter = RetryCounterFactory.buildRetryCounter(retryStrategyForProcess);
        while (isRoutineAlive() && tcGlobalConfig.isRoutineWatcherSwitchOn(false)) {
            boolean success = false;
            try {
                // execute
                execute();
                success = true;
            } catch (TcException e) {
                if (e.isRetry()) {     // retry-able
                    retryCounter.grow();
                    String head = String.format("execute routine fails by retryable exception:\n%s", ExceptionUtil.getStackTrace(e));
                    logger.warn(THIS_CLASS_NAME, retryCounter.status(head));
                } else {        // non-retry-able
                    retryCounter.forceStop();
                    String head = String.format("execute routine fails by non-retryable exception:\n%s", ExceptionUtil.getStackTrace(e));
                    logger.error(THIS_CLASS_NAME, retryCounter.status(head));
                }
            } catch (Throwable e) {        //NOSONAR
                // unknown exception, stop
                retryCounter.forceStop();
                String head = String.format("execute routine fails by unknown exception/throwable:\n%s", ExceptionUtil.getStackTrace(e));
                logger.error(THIS_CLASS_NAME, retryCounter.status(head));
            }

            if (success) {
                // no need to finish routine here, it will be invoked in the finally block outside
                break;      // routine is finished
            } else {
                if (retryCounter.isAlive()) {
                    retryCounter.waitForNextRetry();
                } else {
                    TcExceptionBuilder.throwTcException(
                            TcErrorEnum.TC_NON_RETRY_EXCEPTION,
                            String.format("execute routine [%s] fails", occupiedRoutine.getFullName()));
                }
            }
        }
    }

    // -----

    boolean routineOccupiable() throws TcException {
        if (occupiedRoutine == null) return false;
        BanContext banContext = banHelper.buildBanContext(BanLevelEnum.ROUTINE_OCCUPY, false);
        return !banHelper.isRoutineOccupyBanned(banContext,
                occupiedRoutine.getRoutineName(), occupiedRoutine.getFullName());
    }
    boolean routineExecutable() throws TcException {
        if (occupiedRoutine == null) return false;
        BanContext banContext = banHelper.buildBanContext(BanLevelEnum.ROUTINE_EXEC, false);
        return !banHelper.isRoutineExecBanned(banContext,
                occupiedRoutine.getRoutineName(), occupiedRoutine.getFullName());
    }

    void execute() throws TcException {
        long intervalMs = occupiedRoutine.getInterval();
        // if routine not occupiable any more, end the while loop
        while (isRoutineAlive() && routineOccupiable()) {

            dutyHelper.dutyEnableCheck(NodeDutyEnum.ROUTINE_EXECUTOR);

            // execute one round
            if (routineExecutable()) {
                executeRound();
            }

            // sleep interval
            if (intervalMs > 0) {
                try {
                    Thread.sleep(intervalMs);
                } catch (Exception e) {
                    logger.error(THIS_CLASS_NAME, e.getMessage());
                }
            }
        }
    }

    void executeRound() throws TcException {
        // execute round
        routineExecutor.executeRound();
        // update routine checkpoint
        if (routineExecutor.checkpointEnabled()) {
            updateRoutineCheckpoint();
        }
    }

    // update routine checkpoint, with routine fromValue updated
    void updateRoutineCheckpoint() throws TcException {
        String cpv = occupiedRoutine.getCheckpointValue();
        if (cpv == null) return;

        boolean success = false;

        if (routineStillOccupied()) {
            success = routineBulletin.updateRoutineCheckpoint(occupiedRoutine, getThreadName());
        }

        if (success) {
            String msg = String.format("thread [%s] update routine [%s] checkpoint success",
                    getThreadName(), occupiedRoutine.getFullName());
            logger.info(THIS_CLASS_NAME, msg);
        } else {
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_NON_RETRY_EXCEPTION,
                    String.format("thread [%s] update routine [%s] checkpoint failed",
                            getThreadName(), occupiedRoutine.getFullName()));
        }
    }

    // check if the routine still occupied by current thread
    boolean routineStillOccupied() {
        if (routineOccupation == null) return false;
        return routineOccupation.routineStillOccupied();
    }

    // -----

    // safe to end routine adoption many times
    void endRoutineAdoption() {
        if (occupiedRoutine == null || occupiedRoutine.getOccupyInfo() == null) return;
        OccupyInfo occupyInfo = occupiedRoutine.getOccupyInfo();
        occupyInfo.setFinished(true);     // set routine occupy finished state
        try {
            boolean deleted = routineBulletin.deleteAdoption(occupyInfo);
            String msg = String.format("delete routine adoption for routine [%s] %s",
                    occupiedRoutine.getFullName(), deleted ? "success" : "failed");
            logger.info(THIS_CLASS_NAME, msg);
            occupiedRoutine.revoke();       // revoke occupation and checkpoint of routine
        } catch (Throwable e) {     //NOSONAR
            logger.error(THIS_CLASS_NAME, String.format(
                    "delete routine adoption error: %s", e.getMessage()));
        }
    }

    // safe to finish routine many times
    void finishRoutine() throws TcException {
        endRoutineAdoption();      // end routine adoption

        // inactive current occupied routine heartbeat
        if (heartBeatThread != null) {
            heartBeatThread.inactiveCurrentHeartBeat();
        }

        if (routineExecutor != null) {
            routineExecutor.close();       // close executor
        }
        routineExecutor = null;

        String routineFullName = occupiedRoutine != null ? occupiedRoutine.getFullName() : null;
        String msg = String.format("finish routine [%s] by %s", routineFullName, getThreadName());
        logger.info(THIS_CLASS_NAME, msg);
    }

    // -----

    void stopHeartBeatThread() {
        if (heartBeatThread != null) {
            heartBeatThread.inactiveHeartBeat();
        }
    }

    void exit() {
        stopHeartBeatThread();
        try {
            finishRoutine();
        } catch (TcException e) {
            logger.error(THIS_CLASS_NAME,
                    String.format("%s failed to finish routine: %s",
                            getThreadName(), e.getShortMessage()));
        }

        String msg = String.format("Thread [%s] ends.", getThreadName());
        logger.info(THIS_CLASS_NAME, msg);
    }

    // -----
}
