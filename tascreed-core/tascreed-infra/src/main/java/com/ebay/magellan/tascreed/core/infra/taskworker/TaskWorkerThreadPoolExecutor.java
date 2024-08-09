package com.ebay.magellan.tascreed.core.infra.taskworker;

import com.ebay.magellan.tascreed.core.infra.conf.TcGlobalConfig;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadPoolExecutor;
import org.springframework.stereotype.Component;

@Component
public class TaskWorkerThreadPoolExecutor extends DefaultThreadPoolExecutor<TaskWorkerThread> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public TaskWorkerThreadPoolExecutor(TcGlobalConfig tcGlobalConfig,
                                        TaskWorkerThreadFactory threadFactory,
                                        TcLogger logger) throws TcException {
        super(tcGlobalConfig.getMaxWorkerCountPerHost(), threadFactory, logger);
    }
}
