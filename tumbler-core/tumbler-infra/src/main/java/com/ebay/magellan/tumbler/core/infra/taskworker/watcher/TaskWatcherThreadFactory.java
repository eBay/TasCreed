package com.ebay.magellan.tumbler.core.infra.taskworker.watcher;

import com.ebay.magellan.tumbler.depend.common.thread.DefaultThreadFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskWatcherThreadFactory extends DefaultThreadFactory {
    TaskWatcherThreadFactory() {
        super();
        namePrefix = "tumbler-task-watcher-thread-";
    }

    public TaskWatcherThread buildTaskWatcherThread() {
        TaskWatcherThread taskWatcherThread = context.getBean(TaskWatcherThread.class);
        return taskWatcherThread;
    }
}
