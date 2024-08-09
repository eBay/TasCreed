package com.ebay.magellan.tumbler.core.infra.routine.watcher;

import com.ebay.magellan.tumbler.core.domain.duty.NodeDutyEnum;
import com.ebay.magellan.tumbler.core.infra.conf.TumblerGlobalConfig;
import com.ebay.magellan.tumbler.core.infra.constant.TumblerConstants;
import com.ebay.magellan.tumbler.core.infra.duty.DutyHelper;
import com.ebay.magellan.tumbler.core.infra.routine.RoutineThreadFactory;
import com.ebay.magellan.tumbler.core.infra.routine.RoutineThreadPoolExecutor;
import com.ebay.magellan.tumbler.core.infra.routine.heartbeat.RoutineHeartBeatThreadPoolExecutor;
import com.ebay.magellan.tumbler.depend.common.logger.TumblerLogger;
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
    private TumblerConstants tumblerConstants;

    @Autowired
    private TumblerGlobalConfig tumblerGlobalConfig;

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
    TumblerLogger logger;

    @Override
    public void run() {
        long intervalMs = tumblerConstants.getRoutineWatcherIntervalInSeconds() * 1000L;
        while (true) {
            try {
                dutyHelper.dutyEnableCheck(NodeDutyEnum.ROUTINE_EXECUTOR);

                if (tumblerGlobalConfig.isRoutineWatcherSwitchOn(false)) {
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
        int newMaxRoutineCount = tumblerGlobalConfig.getMaxRoutineCountPerHost();
        routineThreadPoolExecutor.resetMaxWorkerCount(newMaxRoutineCount);
        int newMaxHeartBeatThreadCount = Math.min(newMaxRoutineCount * 2, Integer.MAX_VALUE);
        routineHeartBeatThreadPoolExecutor.resetMaxWorkerCount(newMaxHeartBeatThreadCount);
    }
}
