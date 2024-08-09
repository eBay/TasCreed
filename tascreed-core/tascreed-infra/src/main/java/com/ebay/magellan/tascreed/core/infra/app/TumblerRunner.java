package com.ebay.magellan.tascreed.core.infra.app;

import com.ebay.magellan.tascreed.core.infra.executor.TaskExecutorRegistry;
import com.ebay.magellan.tascreed.core.infra.jobserver.notify.JobNotifyExecThreadFactory;
import com.ebay.magellan.tascreed.core.infra.jobserver.notify.JobNotifyExecThreadPoolExecutor;
import com.ebay.magellan.tascreed.core.infra.routine.execute.RoutineExecutorRegistry;
import com.ebay.magellan.tascreed.core.infra.routine.watcher.RoutineWatcherThreadFactory;
import com.ebay.magellan.tascreed.core.infra.routine.watcher.RoutineWatcherThreadPoolExecutor;
import com.ebay.magellan.tascreed.core.infra.storage.archive.ArchiveStorageFactory;
import com.ebay.magellan.tascreed.core.infra.taskworker.watcher.TaskWatcherThreadFactory;
import com.ebay.magellan.tascreed.core.infra.taskworker.watcher.TaskWatcherThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TumblerRunner {

    @Autowired
    private ArchiveStorageFactory archiveStorageFactory;
    @Autowired
    private TaskExecutorRegistry taskExecutorRegistry;
    @Autowired
    private RoutineExecutorRegistry routineExecutorRegistry;

    @Autowired
    private TaskWatcherThreadFactory taskWatcherThreadFactory;
    @Autowired
    private TaskWatcherThreadPoolExecutor taskWatcherThreadPoolExecutor;

    @Autowired
    private RoutineWatcherThreadFactory routineWatcherThreadFactory;
    @Autowired
    private RoutineWatcherThreadPoolExecutor routineWatcherThreadPoolExecutor;

    @Autowired
    private JobNotifyExecThreadFactory jobNotifyExecThreadFactory;
    @Autowired
    private JobNotifyExecThreadPoolExecutor jobNotifyExecThreadPoolExecutor;

    public void init() {
        archiveStorageFactory.init();
        taskExecutorRegistry.registerTaskExecutors();
        routineExecutorRegistry.registerRoutineExecutors();
    }

    public void start() {
        taskWatcherThreadPoolExecutor.submit(
                taskWatcherThreadFactory.buildTaskWatcherThread());
        routineWatcherThreadPoolExecutor.submit(
                routineWatcherThreadFactory.buildRoutineWatcherThread());
        jobNotifyExecThreadPoolExecutor.submit(
                jobNotifyExecThreadFactory.buildJobUpdateThread());
    }

}