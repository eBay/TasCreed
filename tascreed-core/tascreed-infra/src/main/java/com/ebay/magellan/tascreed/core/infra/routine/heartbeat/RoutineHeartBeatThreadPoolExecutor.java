package com.ebay.magellan.tascreed.core.infra.routine.heartbeat;

import com.ebay.magellan.tascreed.core.infra.conf.TumblerGlobalConfig;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadPoolExecutor;
import org.springframework.stereotype.Component;

@Component
public class RoutineHeartBeatThreadPoolExecutor extends DefaultThreadPoolExecutor<RoutineHeartBeatThread> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RoutineHeartBeatThreadPoolExecutor(TumblerGlobalConfig tumblerGlobalConfig,
                                              RoutineHeartBeatThreadFactory threadFactory,
                                              TcLogger logger) throws TcException {
        super(tumblerGlobalConfig.getMaxRoutineCountPerHost() * 2, threadFactory, logger);
    }
}
