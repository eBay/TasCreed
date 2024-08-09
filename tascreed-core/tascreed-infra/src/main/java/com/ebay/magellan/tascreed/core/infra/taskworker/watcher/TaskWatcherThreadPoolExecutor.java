package com.ebay.magellan.tascreed.core.infra.taskworker.watcher;

import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadPoolExecutor;
import org.springframework.stereotype.Component;

@Component
public class TaskWatcherThreadPoolExecutor extends DefaultThreadPoolExecutor<TaskWatcherThread> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public TaskWatcherThreadPoolExecutor(TaskWatcherThreadFactory threadFactory, TumblerLogger logger) {
        super(1, threadFactory, logger);
    }
}
