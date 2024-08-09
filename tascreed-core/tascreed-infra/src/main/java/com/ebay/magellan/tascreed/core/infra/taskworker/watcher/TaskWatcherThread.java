package com.ebay.magellan.tascreed.core.infra.taskworker.watcher;

import com.ebay.magellan.tascreed.core.domain.duty.NodeDutyEnum;
import com.ebay.magellan.tascreed.core.infra.conf.TcGlobalConfig;
import com.ebay.magellan.tascreed.core.infra.duty.DutyHelper;
import com.ebay.magellan.tascreed.core.infra.taskworker.heartbeat.TaskHeartBeatThreadPoolExecutor;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.core.infra.constant.TcConstants;
import com.ebay.magellan.tascreed.core.infra.taskworker.TaskWorkerThreadFactory;
import com.ebay.magellan.tascreed.core.infra.taskworker.TaskWorkerThreadPoolExecutor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Setter
@Component
@Scope("prototype")
public class TaskWatcherThread implements Runnable {

    private static final String THIS_CLASS_NAME = TaskWatcherThread.class.getSimpleName();

    @Autowired
    private TcConstants tcConstants;

    @Autowired
    private TcGlobalConfig tcGlobalConfig;

    @Autowired
    private TaskWorkerThreadFactory taskWorkerThreadFactory;
    @Autowired
    private TaskWorkerThreadPoolExecutor taskWorkerThreadPoolExecutor;
    @Autowired
    private TaskHeartBeatThreadPoolExecutor taskHeartBeatThreadPoolExecutor;

    @Autowired
    private DutyHelper dutyHelper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    TcLogger logger;

    @Override
    public void run() {
        long intervalMs = tcConstants.getTaskWatcherIntervalInSeconds() * 1000L;
        while (true) {
            try {
                dutyHelper.dutyEnableCheck(NodeDutyEnum.TASK_EXECUTOR);

                if (tcGlobalConfig.isTaskWatcherSwitchOn(false)) {
                    // try to reset max worker count of task worker thread pool
                    tryResetMaxWorkerCount();

                    // submit task worker thread if has vacancy
                    logger.info(THIS_CLASS_NAME,
                            String.format("WorkerThreadPool max thread count is %d, active thread count is %d",
                                    taskWorkerThreadPoolExecutor.getMaxWorkerCount(),
                                    taskWorkerThreadPoolExecutor.getActiveThreadCount()));
                    if (taskWorkerThreadPoolExecutor.hasVacancy()) {
                        logger.info(THIS_CLASS_NAME, "TaskWatcherThread now creating a task worker thread");
                        taskWorkerThreadPoolExecutor.submit(
                                taskWorkerThreadFactory.buildTaskWorkerThread());
                    }
                } else {
                    logger.info(THIS_CLASS_NAME, "switch is off, won't create any task worker thread");
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

    void tryResetMaxWorkerCount() {
        int newMaxWorkerCount = tcGlobalConfig.getMaxWorkerCountPerHost();
        taskWorkerThreadPoolExecutor.resetMaxWorkerCount(newMaxWorkerCount);
        int newMaxHeartBeatThreadCount = Math.min(newMaxWorkerCount * 2, Integer.MAX_VALUE);
        taskHeartBeatThreadPoolExecutor.resetMaxWorkerCount(newMaxHeartBeatThreadCount);
    }
}
