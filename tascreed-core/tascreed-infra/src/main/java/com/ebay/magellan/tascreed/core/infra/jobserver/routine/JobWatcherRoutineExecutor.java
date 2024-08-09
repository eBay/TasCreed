package com.ebay.magellan.tascreed.core.infra.jobserver.routine;

import com.ebay.magellan.tascreed.core.infra.jobserver.msg.JobMsgItem;
import com.ebay.magellan.tascreed.core.infra.jobserver.msg.JobMsgStatePool;
import com.ebay.magellan.tascreed.core.infra.routine.annotation.RoutineExec;
import com.ebay.magellan.tascreed.core.infra.routine.execute.NormalRoutineExecutor;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
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
    private TcLogger logger;

    // -----

    @Override
    protected void initImpl() throws TcException {
        logger.info(THIS_CLASS_NAME, String.format(
                "job watcher routine [%s] init done", routine.getFullName()));
    }

    @Override
    protected void executeRoundImpl() throws TcException {
        try {
            // update notify
            JobMsgStatePool.getInstance().addItem(JobMsgItem.refreshAll());
        } catch (Exception e) {
            TcExceptionBuilder.throwTumblerException(
                    TcErrorEnum.TUMBLER_NON_RETRY_EXCEPTION, e.getMessage());
        }
    }

    @Override
    protected void closeImpl() throws TcException {
        logger.info(THIS_CLASS_NAME, String.format(
                "job watcher routine [%s] close done", routine.getFullName()));
    }

    // -----
}
