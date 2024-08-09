package com.ebay.magellan.tascreed.core.infra.taskworker.heartbeat;

import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadFactory;
import com.ebay.magellan.tascreed.core.infra.taskworker.TaskWorkerThread;
import org.springframework.stereotype.Component;

@Component
public class TaskHeartBeatThreadFactory extends DefaultThreadFactory {
    TaskHeartBeatThreadFactory() {
        super();
        namePrefix = "tumbler-heartbeat-thread-";
    }

    public TaskHeartBeatThread buildHeartBeatThread(TaskWorkerThread workerThread) {
        TaskHeartBeatThread heartBeatThread = context.getBean(TaskHeartBeatThread.class);
        heartBeatThread.setOccupiedInfo(workerThread.getOccupiedTask().getOccupyInfo());
        return heartBeatThread;
    }
}