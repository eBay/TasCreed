package com.ebay.magellan.tascreed.core.infra.routine.watcher;

import com.ebay.magellan.tascreed.core.domain.duty.NodeDutyEnum;
import com.ebay.magellan.tascreed.core.infra.conf.TcGlobalConfig;
import com.ebay.magellan.tascreed.core.infra.constant.TcConstants;
import com.ebay.magellan.tascreed.core.infra.duty.DutyHelper;
import com.ebay.magellan.tascreed.core.infra.routine.RoutineThreadFactory;
import com.ebay.magellan.tascreed.core.infra.routine.RoutineThreadPoolExecutor;
import com.ebay.magellan.tascreed.core.infra.routine.heartbeat.RoutineHeartBeatThreadPoolExecutor;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Setter
@Component
@Scope("prototype")
public class RoutineWatcherThread implements Runnable {

    private static final String THIS_CLASS_NAME = RoutineWatcherThread.class.getSimpleName();

    @Autowired
    private TcConstants tcConstants;

    @Autowired
    private TcGlobalConfig tcGlobalConfig;

    @Autowired
    private RoutineThreadFactory routineThreadFactory;
    @Autowired
    private RoutineThreadPoolExecutor routineThreadPoolExecutor;
    @Autowired
    private RoutineHeartBeatThreadPoolExecutor routineHeartBeatThreadPoolExecutor;

    @Autowired
    private DutyHelper dutyHelper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    TcLogger logger;

    @Override
    public void run() {
        long intervalMs = tcConstants.getRoutineWatcherIntervalInSeconds() * 1000L;
        while (true) {
            try {
                dutyHelper.dutyEnableCheck(NodeDutyEnum.ROUTINE_EXECUTOR);

                if (tcGlobalConfig.isRoutineWatcherSwitchOn(false)) {
                    // try to reset max worker count of task worker thread pool
                    tryResetMaxRoutineCount();

                    // submit routine thread if has vacancy
                    logger.info(THIS_CLASS_NAME,
                            String.format("RoutineThreadPool max thread count is %d, active thread count is %d",
                                    routineThreadPoolExecutor.getMaxWorkerCount(),
                                    routineThreadPoolExecutor.getActiveThreadCount()));
                    if (routineThreadPoolExecutor.hasVacancy()) {
                        logger.info(THIS_CLASS_NAME, "RoutineWatcherThread now creating a routine thread");
                        routineThreadPoolExecutor.submit(
                                routineThreadFactory.buildRoutineThread());
                    }
                } else {
                    logger.info(THIS_CLASS_NAME, "switch is off, won't create any routine thread");
                }
            } catch (Exception e) {
                /**
                 * watcher thread can't stop,
                 * when exception happens, just catch and log it and continue looping
                 */
                logger.error(THIS_CLASS_NAME, e.getMessage());
            }

            // wait for next schedule
            try {
                Thread.sleep(intervalMs);
            } catch (Exception e) {
                logger.error(THIS_CLASS_NAME, e.getMessage());
            }
        }
    }

    void tryResetMaxRoutineCount() {
        int newMaxRoutineCount = tcGlobalConfig.getMaxRoutineCountPerHost();
        routineThreadPoolExecutor.resetMaxWorkerCount(newMaxRoutineCount);
        int newMaxHeartBeatThreadCount = Math.min(newMaxRoutineCount * 2, Integer.MAX_VALUE);
        routineHeartBeatThreadPoolExecutor.resetMaxWorkerCount(newMaxHeartBeatThreadCount);
    }
}
