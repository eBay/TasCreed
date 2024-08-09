package com.ebay.magellan.tumbler.core.infra.taskworker.watcher;

import com.ebay.magellan.tumbler.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tumbler.depend.common.thread.DefaultThreadPoolExecutor;
import org.springframework.stereotype.Component;

@Component
public class TaskWatcherThreadPoolExecutor extends DefaultThreadPoolExecutor<TaskWatcherThread> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public TaskWatcherThreadPoolExecutor(TaskWatcherThreadFactory threadFactory, TumblerLogger logger) {
        super(1, threadFactory, logger);
    }
}
