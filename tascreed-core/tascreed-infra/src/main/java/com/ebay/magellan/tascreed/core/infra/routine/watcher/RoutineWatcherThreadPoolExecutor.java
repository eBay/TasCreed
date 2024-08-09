package com.ebay.magellan.tascreed.core.infra.routine.watcher;

import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadPoolExecutor;
import org.springframework.stereotype.Component;

@Component
public class RoutineWatcherThreadPoolExecutor extends DefaultThreadPoolExecutor<RoutineWatcherThread> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RoutineWatcherThreadPoolExecutor(RoutineWatcherThreadFactory threadFactory, TcLogger logger) {
        super(1, threadFactory, logger);
    }
}
