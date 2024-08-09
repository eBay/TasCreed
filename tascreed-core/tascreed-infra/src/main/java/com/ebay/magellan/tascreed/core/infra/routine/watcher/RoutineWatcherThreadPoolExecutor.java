package com.ebay.magellan.tascreed.core.infra.routine.watcher;

import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadPoolExecutor;
import org.springframework.stereotype.Component;

@Component
public class RoutineWatcherThreadPoolExecutor extends DefaultThreadPoolExecutor<RoutineWatcherThread> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RoutineWatcherThreadPoolExecutor(RoutineWatcherThreadFactory threadFactory, TumblerLogger logger) {
        super(1, threadFactory, logger);
    }
}
