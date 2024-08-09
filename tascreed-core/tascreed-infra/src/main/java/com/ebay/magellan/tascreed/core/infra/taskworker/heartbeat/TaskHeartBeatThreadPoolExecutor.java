package com.ebay.magellan.tascreed.core.infra.taskworker.heartbeat;

import com.ebay.magellan.tascreed.core.infra.conf.TcGlobalConfig;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadPoolExecutor;
import org.springframework.stereotype.Component;

@Component
public class TaskHeartBeatThreadPoolExecutor extends DefaultThreadPoolExecutor<TaskHeartBeatThread> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public TaskHeartBeatThreadPoolExecutor(TcGlobalConfig tcGlobalConfig,
                                           TaskHeartBeatThreadFactory threadFactory,
                                           TcLogger logger) throws TcException {
        super(tcGlobalConfig.getMaxWorkerCountPerHost() * 2, threadFactory, logger);
    }
}