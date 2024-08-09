package com.ebay.magellan.tascreed.core.infra.routine;

import com.ebay.magellan.tascreed.core.infra.conf.TumblerGlobalConfig;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadPoolExecutor;
import org.springframework.stereotype.Component;

@Component
public class RoutineThreadPoolExecutor extends DefaultThreadPoolExecutor<RoutineThread> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RoutineThreadPoolExecutor(TumblerGlobalConfig tumblerGlobalConfig,
                                     RoutineThreadFactory threadFactory,
                                     TumblerLogger logger) throws TumblerException {
        super(tumblerGlobalConfig.getMaxRoutineCountPerHost(), threadFactory, logger);
    }
}
