package com.ebay.magellan.tascreed.core.infra.routine.watcher;

import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadFactory;
import org.springframework.stereotype.Component;

@Component
public class RoutineWatcherThreadFactory extends DefaultThreadFactory {
    RoutineWatcherThreadFactory() {
        super();
        namePrefix = "tumbler-routine-watcher-thread-";
    }

    public RoutineWatcherThread buildRoutineWatcherThread() {
        RoutineWatcherThread routineWatcherThread = context.getBean(RoutineWatcherThread.class);
        return routineWatcherThread;
    }
}
