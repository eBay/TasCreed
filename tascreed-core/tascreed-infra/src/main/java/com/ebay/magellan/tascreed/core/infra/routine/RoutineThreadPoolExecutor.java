package com.ebay.magellan.tascreed.core.infra.routine;

import com.ebay.magellan.tascreed.core.infra.conf.TcGlobalConfig;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadPoolExecutor;
import org.springframework.stereotype.Component;

@Component
public class RoutineThreadPoolExecutor extends DefaultThreadPoolExecutor<RoutineThread> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RoutineThreadPoolExecutor(TcGlobalConfig tcGlobalConfig,
                                     RoutineThreadFactory threadFactory,
                                     TcLogger logger) throws TcException {
        super(tcGlobalConfig.getMaxRoutineCountPerHost(), threadFactory, logger);
    }
}
