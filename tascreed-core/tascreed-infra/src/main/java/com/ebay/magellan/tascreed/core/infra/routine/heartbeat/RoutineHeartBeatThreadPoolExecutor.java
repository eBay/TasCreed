package com.ebay.magellan.tascreed.core.infra.routine.heartbeat;

import com.ebay.magellan.tascreed.core.infra.conf.TumblerGlobalConfig;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadPoolExecutor;
import org.springframework.stereotype.Component;

@Component
public class RoutineHeartBeatThreadPoolExecutor extends DefaultThreadPoolExecutor<RoutineHeartBeatThread> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RoutineHeartBeatThreadPoolExecutor(TumblerGlobalConfig tumblerGlobalConfig,
                                              RoutineHeartBeatThreadFactory threadFactory,
                                              TumblerLogger logger) throws TumblerException {
        super(tumblerGlobalConfig.getMaxRoutineCountPerHost() * 2, threadFactory, logger);
    }
}
