package com.ebay.magellan.tascreed.core.infra.jobserver.notify;

import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadPoolExecutor;
import org.springframework.stereotype.Component;

@Component
public class JobNotifyExecThreadPoolExecutor extends DefaultThreadPoolExecutor<JobNotifyExecThread> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public JobNotifyExecThreadPoolExecutor(JobNotifyExecThreadFactory threadFactory, TumblerLogger logger) {
        super(1, threadFactory, logger);
    }
}
