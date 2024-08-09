package com.ebay.magellan.tascreed.core.infra.taskworker;

import com.ebay.magellan.tascreed.core.domain.duty.NodeDutyEnum;
import com.ebay.magellan.tascreed.core.domain.job.JobInstKey;
import com.ebay.magellan.tascreed.core.domain.state.partial.TaskCheckpoint;
import com.ebay.magellan.tascreed.core.domain.task.TaskResult;
import com.ebay.magellan.tascreed.core.infra.conf.TcGlobalConfig;
import com.ebay.magellan.tascreed.core.infra.duty.DutyHelper;
import com.ebay.magellan.tascreed.core.infra.executor.alive.TaskOccupation;
import com.ebay.magellan.tascreed.core.infra.jobserver.msg.JobMsgItem;
import com.ebay.magellan.tascreed.core.infra.jobserver.msg.JobMsgStatePool;
import com.ebay.magellan.tascreed.core.infra.monitor.Metrics;
import com.ebay.magellan.tascreed.core.infra.opr.OprEnum;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.TaskBulletin;
import com.ebay.magellan.tascreed.core.infra.taskworker.help.TaskOccupyHelper;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.common.retry.*;
import com.ebay.magellan.tascreed.depend.common.util.DefaultValueUtil;
import com.ebay.magellan.tascreed.depend.common.util.ExceptionUtil;
import com.ebay.magellan.tascreed.depend.common.util.ThreadUtil;
import com.ebay.magellan.tascreed.core.infra.constant.TcConstants;
import com.ebay.magellan.tascreed.core.domain.occupy.OccupyInfo;
import com.ebay.magellan.tascreed.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.infra.executor.TaskExecutor;
import com.ebay.magellan.tascreed.core.infra.executor.TaskExecutorFactory;
import com.ebay.magellan.tascreed.core.infra.taskworker.heartbeat.TaskHeartBeatThread;
import com.ebay.magellan.tascreed.core.infra.taskworker.heartbeat.TaskHeartBeatThreadFactory;
import com.ebay.magellan.tascreed.core.infra.taskworker.heartbeat.TaskHeartBeatThreadPoolExecutor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Setter
@Getter
@Component
@Scope("prototype")
public class TaskWorkerThread implements Runnable {

    private static final String THIS_CLASS_NAME = TaskWorkerThread.class.getSimpleName();

    private RetryStrategy retryStrategyForOccupyPack = RetryBackoffStrategy.newDefaultInstance();
    private RetryStrategy retryStrategyForWorker = RetryBackoffStrategy.newDefaultInstance();

    @Autowired
    private TcConstants tcConstants;

    @Autowired
    private TcGlobalConfig tcGlobalConfig;

    @Autowired
    private TaskExecutorFactory taskExecutorFactory;

    @Autowired
    private TaskHeartBeatThreadFactory heartBeatThreadFactory;
    @Autowired
    private TaskHeartBeatThreadPoolExecutor heartBeatThreadPoolExecutor;

    @Autowired
    private TaskWorkerThreadPoolExecutor taskWorkerThreadPoolExecutor;

    @Autowired
    private TaskBulletin taskBulletin;

    @Autowired
    private TaskOccupyHelper taskOccupyHelper;

    @Autowired
    private DutyHelper dutyHelper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TcLogger logger;

    private TaskExecutor taskExecutor;
    private TaskOccupation taskOccupation;

    private volatile Task occupiedTask = null;

    private volatile TaskHeartBeatThread heartBeatThread;

    private volatile String threadName;

    public String getThreadName() {
        if (threadName == null) {
            threadName = ThreadUtil.getCurrentThreadName();
        }
        return threadName;
    }

    private boolean occupied() {
        return occupiedTask != null;
    }

    boolean isTaskAlive() {
        return occupiedTask != null &&
                occupiedTask.getOccupyInfo() != null &&
                occupiedTask.getOccupyInfo().isAlive();
    }

    @Override
    public void run() {
        try {
            /**
             * if one task is finished, need to pick another task to work,
             * so need loop here
             */
            while (true) {
                /**
                 * 1. check if current thread needs to be terminated
                 */
                if (needToEndThread()) {
                    logger.warn(THIS_CLASS_NAME,
                            String.format("%s needs to be terminated", getThreadName()));
                    break;
                }

                /**
                 * 2. try to occupy a task
                 */
                long startTimeMs = System.currentTimeMillis();
                tryOccupyTask();

                /**
                 * 3. if occupied a task, start working;
                 * if occupy failed, retry the loop with backoff
                 */
                if (occupied()) {
                    String msg = String.format("%s successfully occupied task [%s], occupy time cost is %d ms",
                            getThreadName(), occupiedTask.getTaskFullName(), System.currentTimeMillis() - startTimeMs);
                    logger.info(THIS_CLASS_NAME, msg);
                    // try work on task
                    tryWorkOnTask();
                    logger.info(THIS_CLASS_NAME,
                            String.format("task %s is finished or switch is off, total time cost is %d ms",
                                    occupiedTask.getTaskFullName(), System.currentTimeMillis() - startTimeMs));

                    // release occupied task
                    occupiedTask = null;
                } else {
                    // if can't occupy any pack, just return
                    break;
                }
            }
        } catch (TcException e) {
            logger.error(THIS_CLASS_NAME,
                    String.format("%s failed with TasCreed exception:\n%s", getThreadName(), ExceptionUtil.getStackTrace(e)));
        } catch (Exception e) {
            logger.error(THIS_CLASS_NAME,
                    String.format("%s failed with exception:\n%s", getThreadName(), ExceptionUtil.getStackTrace(e)));
        } catch (Throwable e) {
            logger.error(THIS_CLASS_NAME,
                    String.format("%s failed with unknown throwable:\n%s", getThreadName(), ExceptionUtil.getStackTrace(e)));
        } finally {
            exit();
        }
    }

    // need to end this worker thread if it is interrupted or thread pool size already exceeds max num
    boolean needToEndThread() {
        return Thread.interrupted() || taskWorkerThreadPoolExecutor.poolSizeExceeded();
    }

    void tryOccupyTask() throws TcException {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.TASK_EXECUTOR);

        occupiedTask = null;
        logger.info(THIS_CLASS_NAME,
                String.format("%s trying to occupy a task...", getThreadName()));
        RetryCounter retryCounter = RetryCounterFactory.buildRetryCounter(retryStrategyForOccupyPack);
        while (tcGlobalConfig.isTaskWatcherSwitchOn(true)) {
            try {
                occupiedTask = taskOccupyHelper.tryPickOneTask(getThreadName());
                if (occupiedTask == null) {
                    logger.info(THIS_CLASS_NAME,
                            String.format("%s can't occupy any task", getThreadName()));
                }
                return;
            } catch (TcException e) {
                if (e.isRetry()) {     // retry-able
                    retryCounter.grow();
                    String head = String.format("occupy task fails by retryable exception:\n%s", ExceptionUtil.getStackTrace(e));
                    logger.warn(THIS_CLASS_NAME, retryCounter.status(head));
                } else {        // nont-retry-able
                    retryCounter.forceStop();
                    String head = String.format("occupy task fails by non-retryable exception:\n%s", ExceptionUtil.getStackTrace(e));
                    logger.error(THIS_CLASS_NAME, retryCounter.status(head));
                }
            } catch (Throwable e) {        //NOSONAR
                // unknown exception, retry
                retryCounter.grow();
                String head = String.format("occupy task fails by unknown exception/throwable:\n%s", ExceptionUtil.getStackTrace(e));
                logger.error(THIS_CLASS_NAME, retryCounter.status(head));
            }

            if (!retryCounter.isAlive()) {
                break;
            }
            retryCounter.waitForNextRetry();
        }
    }

    // build task executor
    void buildTaskExecutor() throws TcException {
        taskExecutor = taskExecutorFactory.buildTaskExecutor(occupiedTask);
        if (taskExecutor == null) {
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_NON_RETRY_EXCEPTION,
                    String.format("build taskExecutor for step [%s] fails", occupiedTask.getStepName()));
        }
    }

    // build worker aliveness
    void buildTaskOccupation() {
        taskOccupation = taskExecutorFactory.buildTaskOccupation(occupiedTask, getThreadName(), taskBulletin);
    }

    void enableAsyncHeartBeatThread() throws TcException {
        OccupyInfo occupyInfo = occupiedTask.getOccupyInfo();
        if (heartBeatThread != null && heartBeatThread.isActive()) {
            heartBeatThread.setOccupiedInfo(occupyInfo);
        } else {
            heartBeatThread = heartBeatThreadFactory.buildHeartBeatThread(this);
            if (heartBeatThreadPoolExecutor.hasVacancy()) {
                heartBeatThreadPoolExecutor.submit(heartBeatThread);
            } else {
                TcExceptionBuilder.throwTcException(
                        TcErrorEnum.TC_NON_RETRY_EXCEPTION,
                        String.format("task [%s] fails to submit heart beat thread", occupyInfo.getOccupyKey()));
            }
        }
    }

    // build and init task executor, as well as heartbeat
    void taskWorkerStartUp() throws TcException {
        buildTaskExecutor();
        buildTaskOccupation();

        taskExecutor.init(occupiedTask, taskOccupation);

        enableAsyncHeartBeatThread();
    }

    // try work on task
    void tryWorkOnTask() throws TcException {
        try {
            workOnTask();
        } catch (TcException e) {
            // only identify the non-retry and fatal exceptions
            if (e.isNonRetry() || e.isFatal()) {
                // update task picked times and task pick after time for back-off retry
                boolean canBePickedAgain = updateTaskPickedTimes();
                if (!canBePickedAgain) {
                    // give up if no need to retry
                    logger.error(THIS_CLASS_NAME,
                            String.format("give up task [%s], no need to be picked any more", occupiedTask.getTaskFullName()));
                    return;
                }
            }
            logger.error(THIS_CLASS_NAME,
                    String.format("temporarily drop task [%s], it will be picked by other worker", occupiedTask.getTaskFullName()));
            throw e;
        } finally {
            finishTask();
        }
    }

    // while loop to work
    void workOnTask() throws TcException {
        // build and init task executor, as well as heartbeat
        taskWorkerStartUp();

        RetryCounter retryCounter = RetryCounterFactory.buildRetryCounter(retryStrategyForWorker);
        while (isTaskAlive() && tcGlobalConfig.isTaskWatcherSwitchOn(false)) {
            boolean success = false;
            try {
                // execute
                execute();
                // update task state to final state
                updateTaskStateToFinal();
                success = true;
            } catch (TcException e) {
                if (e.isRetry()) {     // retry-able
                    retryCounter.grow();
                    String head = String.format("execute task fails by retryable exception:\n%s", ExceptionUtil.getStackTrace(e));
                    logger.warn(THIS_CLASS_NAME, retryCounter.status(head));
                } else {        // non-retry-able
                    retryCounter.forceStop();
                    String head = String.format("execute task fails by non-retryable exception:\n%s", ExceptionUtil.getStackTrace(e));
                    logger.error(THIS_CLASS_NAME, retryCounter.status(head));
                }
            } catch (Throwable e) {        //NOSONAR
                // unknown exception, stop
                retryCounter.forceStop();
                String head = String.format("execute task fails by unknown exception/throwable:\n%s", ExceptionUtil.getStackTrace(e));
                logger.error(THIS_CLASS_NAME, retryCounter.status(head));
            }

            if (success) {
                // no need to finish task here, it will be invoked in the finally block outside
                break;      // task is finished
            } else {
                // update task tried times for mid state update
                boolean canRetry = updateTaskTriedTimes();

                if (canRetry && retryCounter.isAlive()) {
                    retryCounter.waitForNextRetry();
                } else {
                    TcExceptionBuilder.throwTcException(
                            TcErrorEnum.TC_NON_RETRY_EXCEPTION,
                            String.format("execute task [%s] fails", occupiedTask.getTaskFullName()));
                }
            }
        }
    }

    void execute() throws TcException {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.TASK_EXECUTOR);

        if (taskExecutor.checkpointEnabled()) {
            executeWithCheckpoint();
        } else {
            taskExecutor.execute();
        }
    }

    // check if the task still occupied by current thread
    boolean taskStillOccupied() {
        if (taskOccupation == null) return false;
        return taskOccupation.taskStillOccupied();
    }

    void updateTaskStateToFinal() throws TcException {
        TaskStateEnum state = occupiedTask.getTaskState();
        if (state.isUndone()) return;

        Date now = new Date();

        boolean success = false;

        occupiedTask.getMidState().setModifyTime(now);
        occupiedTask.getMidState().setModifyThread(getThreadName());

        if (taskStillOccupied()) {
            success = taskBulletin.moveTodoTask2DoneTask(
                    occupiedTask, getThreadName(), state.resultError());
        }

        if (success) {
            String msg = String.format("thread [%s] update task [%s] state success",
                    getThreadName(), occupiedTask.getTaskFullName());
            logger.info(THIS_CLASS_NAME, msg);

            // update notify
            JobInstKey id = new JobInstKey(occupiedTask.getJobName(), occupiedTask.getTrigger());
            JobMsgStatePool.getInstance().addItem(JobMsgItem.refresh(id));

            // record the metrics
            Metrics.taskOprCounter.labels(occupiedTask.getJobName(), occupiedTask.getStepName(),
                    occupiedTask.getTaskState().name(), OprEnum.FINISH.name()).inc();

            Date startTime = DefaultValueUtil.defValue(occupiedTask.getMidState().getCreateTime(), now);
            Date endTime = DefaultValueUtil.defValue(occupiedTask.getMidState().getModifyTime(), now);
            long timeInMs = endTime.getTime() - startTime.getTime();
            Metrics.taskExecSummary.labels(occupiedTask.getJobName(), occupiedTask.getStepName(),
                    occupiedTask.getTaskState().name()).observe(timeInMs);
            logger.info(THIS_CLASS_NAME, String.format("task [%s] with state [%s] has cost %d ms",
                    occupiedTask.getTaskFullName(), occupiedTask.getTaskState().name(), timeInMs));
        } else {
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_NON_RETRY_EXCEPTION,
                    String.format("thread [%s] update task [%s] state failed",
                            getThreadName(), occupiedTask.getTaskFullName()));
        }
    }

    // -----

    // update task tried times for mid state update
    boolean updateTaskTriedTimes() {
        if (occupiedTask == null) return false;
        occupiedTask.getMidState().increaseTriedTimes();

        if (!taskStillOccupied()) return false;

        occupiedTask.getMidState().setModifyTime(new Date());
        occupiedTask.getMidState().setModifyThread(getThreadName());

        try {
            // update task tried times
            boolean updated = taskBulletin.updateTodoTask(occupiedTask, getThreadName());
            return updated;     // if not updated, means task has been picked by another worker, no need to retry by this worker
        } catch (Exception e) {
            // ignore exception
        }
        return true;
    }

    // -----

    private static final RetryStrategy taskRetryStrategyAcrossThreads =
            new RetryLinearBackoffStrategy(0, 5 * 60, 30, 5, 15);

    // update task picked times and task pick after time for back-off retry
    // if task exceeds max pick times, return false;
    // else return true, means the task can be picked by any other worker again
    boolean updateTaskPickedTimes() throws TcException {
        if (occupiedTask == null) return false;

        occupiedTask.getMidState().increasePickedTimes();
        int pickedTimes = occupiedTask.getMidState().pickedTimesValue();

        if (!taskStillOccupied()) return false;

        // if exceeds max pick times, generate error result
        int maxPickTimes = DefaultValueUtil.intValue(
                occupiedTask.getTaskAllConf().getMaxPickTimes(),
                tcConstants.getTaskDefaultMaxPickTimes());
        if (maxPickTimes >= 0 && pickedTimes > maxPickTimes) {
            // end task with error result
            endTaskWithErrorResult(String.format("picked times %d exceeds max pick times %d", pickedTimes, maxPickTimes));
            return false;
        }

        long curTime = System.currentTimeMillis();
        long deltaTime = taskRetryStrategyAcrossThreads.getSleepMs(pickedTimes);
        if (deltaTime > 0) {
            long afterTime = curTime + deltaTime;
            occupiedTask.getMidState().setAfterTime(new Date(afterTime));
        }
        occupiedTask.getMidState().setModifyTime(new Date());
        occupiedTask.getMidState().setModifyThread(getThreadName());

        // update task picked times
        taskBulletin.updateTodoTask(occupiedTask, getThreadName());
        return true;
    }

    // end task with error result
    void endTaskWithErrorResult(String reason) throws TcException {
        if (occupiedTask == null) return;
        TaskResult tr = new TaskResult(TaskStateEnum.ERROR, reason);
        occupiedTask.setResult(tr);
        // update task state to final state
        updateTaskStateToFinal();

        String msg = String.format("end task [%s] with error result by %s", occupiedTask.getTaskFullName(), getThreadName());
        logger.error(THIS_CLASS_NAME, msg);
    }

    // -----

    // safe to end task adoption many times
    void endTaskAdoption() {
        if (occupiedTask == null || occupiedTask.getOccupyInfo() == null) return;
        OccupyInfo occupyInfo = occupiedTask.getOccupyInfo();
        occupyInfo.setFinished(true);     // set task finished state
        try {
            boolean deleted = taskBulletin.deleteAdoption(occupyInfo);
            String msg = String.format("delete task adoption for task [%s] %s",
                    occupiedTask.getTaskFullName(), deleted ? "success" : "failed");
            logger.info(THIS_CLASS_NAME, msg);
            occupiedTask.setOccupyInfo(null);       // clear occupy info in task
        } catch (Throwable e) {     //NOSONAR
            logger.error(THIS_CLASS_NAME, String.format(
                    "delete task adoption error: %s", e.getMessage()));
        }
    }

    // safe to finish task many times
    void finishTask() throws TcException {
        endTaskAdoption();      // end task adoption

        // inactive current occupied task heartbeat
        if (heartBeatThread != null) {
            heartBeatThread.inactiveCurrentHeartBeat();
        }

        if (taskExecutor != null) {
            taskExecutor.close();       // close executor
        }
        taskExecutor = null;

        String taskName = occupiedTask != null ? occupiedTask.getTaskFullName() : null;
        String msg = String.format("finish task [%s] by %s", taskName, getThreadName());
        logger.info(THIS_CLASS_NAME, msg);
    }

    // -----

    void stopHeartBeatThread() {
        if (heartBeatThread != null) {
            heartBeatThread.inactiveHeartBeat();
        }
    }

    void exit() {
        stopHeartBeatThread();
        try {
            finishTask();
        } catch (TcException e) {
            logger.error(THIS_CLASS_NAME,
                    String.format("%s failed to finish task: %s",
                            getThreadName(), e.getShortMessage()));
        }

        String msg = String.format("Thread [%s] ends.", getThreadName());
        logger.info(THIS_CLASS_NAME, msg);
    }

    // -----

    // execute with checkpoint
    void executeWithCheckpoint() throws TcException {
        // execute when task alive and task undone
        while (isTaskAlive() && occupiedTask.getTaskState().isUndone()) {
            dutyHelper.dutyEnableCheck(NodeDutyEnum.TASK_EXECUTOR);
            // execute once
            taskExecutor.executeRound();
            // update task checkpoint
            updateTaskCheckpoint();
        }
    }

    // update task checkpoint, with task fromValue updated
    void updateTaskCheckpoint() throws TcException {
        TaskCheckpoint cp = occupiedTask.getTaskCheckpoint();
        if (cp == null) return;

        boolean success = false;

        occupiedTask.getMidState().setModifyTime(new Date());
        occupiedTask.getMidState().setModifyThread(getThreadName());

        if (taskStillOccupied()) {
            success = taskBulletin.updateTodoTask(occupiedTask, getThreadName());
        }

        if (success) {
            String msg = String.format("thread [%s] update task [%s] checkpoint success",
                    getThreadName(), occupiedTask.getTaskFullName());
            logger.info(THIS_CLASS_NAME, msg);
        } else {
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_NON_RETRY_EXCEPTION,
                    String.format("thread [%s] update task [%s] checkpoint failed",
                            getThreadName(), occupiedTask.getTaskFullName()));
        }
    }

}
