package com.ebay.magellan.tumbler.core.infra.routine;

import com.ebay.magellan.tumbler.depend.common.thread.DefaultThreadFactory;
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
