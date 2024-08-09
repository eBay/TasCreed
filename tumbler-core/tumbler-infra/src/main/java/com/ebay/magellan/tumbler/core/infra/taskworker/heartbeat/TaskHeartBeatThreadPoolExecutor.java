package com.ebay.magellan.tumbler.core.infra.taskworker.heartbeat;

import com.ebay.magellan.tumbler.core.infra.conf.TumblerGlobalConfig;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;
import com.ebay.magellan.tumbler.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tumbler.depend.common.thread.DefaultThreadPoolExecutor;
import org.springframework.stereotype.Component;

@Component
public class TaskHeartBeatThreadPoolExecutor extends DefaultThreadPoolExecutor<TaskHeartBeatThread> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public TaskHeartBeatThreadPoolExecutor(TumblerGlobalConfig tumblerGlobalConfig,
                                           TaskHeartBeatThreadFactory threadFactory,
                                           TumblerLogger logger) throws TumblerException {
        super(tumblerGlobalConfig.getMaxWorkerCountPerHost() * 2, threadFactory, logger);
    }
}