package com.ebay.magellan.tumbler.core.infra.routine.watcher;

import com.ebay.magellan.tumbler.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tumbler.depend.common.thread.DefaultThreadPoolExecutor;
import org.springframework.stereotype.Component;

@Component
public class RoutineWatcherThreadPoolExecutor extends DefaultThreadPoolExecutor<RoutineWatcherThread> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RoutineWatcherThreadPoolExecutor(RoutineWatcherThreadFactory threadFactory, TumblerLogger logger) {
        super(1, threadFactory, logger);
    }
}
