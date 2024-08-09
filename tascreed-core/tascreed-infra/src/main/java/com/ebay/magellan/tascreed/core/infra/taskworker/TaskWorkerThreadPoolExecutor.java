package com.ebay.magellan.tascreed.core.infra.taskworker;

import com.ebay.magellan.tascreed.core.infra.conf.TumblerGlobalConfig;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadPoolExecutor;
import org.springframework.stereotype.Component;

@Component
public class TaskWorkerThreadPoolExecutor extends DefaultThreadPoolExecutor<TaskWorkerThread> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public TaskWorkerThreadPoolExecutor(TumblerGlobalConfig tumblerGlobalConfig,
                                        TaskWorkerThreadFactory threadFactory,
                                        TumblerLogger logger) throws TumblerException {
        super(tumblerGlobalConfig.getMaxWorkerCountPerHost(), threadFactory, logger);
    }
}
