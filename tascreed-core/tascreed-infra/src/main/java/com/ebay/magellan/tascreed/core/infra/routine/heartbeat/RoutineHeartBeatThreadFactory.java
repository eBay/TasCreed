package com.ebay.magellan.tascreed.core.infra.routine.heartbeat;

import com.ebay.magellan.tascreed.core.infra.routine.RoutineThread;
import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadFactory;
import org.springframework.stereotype.Component;

@Component
public class RoutineHeartBeatThreadFactory extends DefaultThreadFactory {
    RoutineHeartBeatThreadFactory() {
        super();
        namePrefix = "tascreed-routine-heartbeat-thread-";
    }

    public RoutineHeartBeatThread buildHeartBeatThread(RoutineThread routineThread) {
        RoutineHeartBeatThread heartBeatThread = context.getBean(RoutineHeartBeatThread.class);
        heartBeatThread.setOccupiedInfo(routineThread.getOccupiedRoutine().getOccupyInfo());
        return heartBeatThread;
    }
}
