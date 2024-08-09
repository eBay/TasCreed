package com.ebay.magellan.tascreed.core.infra.routine.heartbeat;

import com.ebay.magellan.tascreed.core.infra.conf.TcGlobalConfig;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadPoolExecutor;
import org.springframework.stereotype.Component;

@Component
public class RoutineHeartBeatThreadPoolExecutor extends DefaultThreadPoolExecutor<RoutineHeartBeatThread> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RoutineHeartBeatThreadPoolExecutor(TcGlobalConfig tcGlobalConfig,
                                              RoutineHeartBeatThreadFactory threadFactory,
                                              TcLogger logger) throws TcException {
        super(tcGlobalConfig.getMaxRoutineCountPerHost() * 2, threadFactory, logger);
    }
}
