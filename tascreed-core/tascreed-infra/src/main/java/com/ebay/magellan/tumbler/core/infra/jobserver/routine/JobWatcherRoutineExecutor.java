package com.ebay.magellan.tumbler.core.infra.jobserver.routine;

import com.ebay.magellan.tumbler.core.infra.jobserver.msg.JobMsgItem;
import com.ebay.magellan.tumbler.core.infra.jobserver.msg.JobMsgStatePool;
import com.ebay.magellan.tumbler.core.infra.monitor.routine.MonitorRoutineExecutor;
import com.ebay.magellan.tumbler.core.infra.routine.annotation.RoutineExec;
import com.ebay.magellan.tumbler.core.infra.routine.execute.NormalRoutineExecutor;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerErrorEnum;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerExceptionBuilder;
import com.ebay.magellan.tumbler.depend.common.logger.TumblerLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@RoutineExec(routine="job-watcher", scale = 3, priority = 100, interval = 30 * 1000L)
@Component
@Scope("prototype")
public class JobWatcherRoutineExecutor extends NormalRoutineExecutor {

    private static final String THIS_CLASS_NAME = JobWatcherRoutineExecutor.class.getSimpleName();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    // -----

    @Override
    protected void initImpl() throws TumblerException {
        logger.info(THIS_CLASS_NAME, String.format(
                "job watcher routine [%s] init done", routine.getFullName()));
    }

    @Override
    protected void executeRoundImpl() throws TumblerException {
        try {
            // update notify
            JobMsgStatePool.getInstance().addItem(JobMsgItem.refreshAll());
        } catch (Exception e) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_NON_RETRY_EXCEPTION, e.getMessage());
        }
    }

    @Override
    protected void closeImpl() throws TumblerException {
        logger.info(THIS_CLASS_NAME, String.format(
                "job watcher routine [%s] close done", routine.getFullName()));
    }

    // -----
}
