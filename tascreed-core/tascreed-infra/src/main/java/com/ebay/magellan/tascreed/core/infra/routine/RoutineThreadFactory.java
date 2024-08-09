package com.ebay.magellan.tascreed.core.infra.routine;

import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadFactory;
import org.springframework.stereotype.Component;

@Component
public class RoutineThreadFactory extends DefaultThreadFactory {

    RoutineThreadFactory() {
        super();
        namePrefix = "tumbler-routine-thread-";
    }

    public RoutineThread buildRoutineThread() {
        RoutineThread routineThread = context.getBean(RoutineThread.class);
        return routineThread;
    }
}
