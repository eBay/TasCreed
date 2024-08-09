package com.ebay.magellan.tascreed.core.infra.monitor.routine;

import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.state.JobStateEnum;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.domain.util.JsonUtil;
import com.ebay.magellan.tascreed.core.infra.monitor.Metrics;
import com.ebay.magellan.tascreed.core.infra.monitor.metric.RetryTimesGauge;
import com.ebay.magellan.tascreed.core.infra.monitor.metric.TimeExceedGauge;
import com.ebay.magellan.tascreed.core.infra.routine.annotation.RoutineExec;
import com.ebay.magellan.tascreed.core.infra.routine.execute.NormalRoutineExecutor;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.JobBulletin;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.TaskBulletin;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RoutineExec(routine="monitor", priority = 50, interval = 30 * 1000L)
@Component
@Scope("prototype")
public class MonitorRoutineExecutor extends NormalRoutineExecutor {

    private static final String THIS_CLASS_NAME = MonitorRoutineExecutor.class.getSimpleName();

    @Autowired
    private JobBulletin jobBulletin;
    @Autowired
    private TaskBulletin taskBulletin;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TcLogger logger;

    // -----

    private RetryTimesGauge retryTimesGauge = new RetryTimesGauge();
    private TimeExceedGauge timeExceedGauge = new TimeExceedGauge();

    // -----

    @Override
    protected void initImpl() throws TcException {
        clearMetrics();
        logger.info(THIS_CLASS_NAME, String.format(
                "monitor routine [%s] init done", routine.getFullName()));
    }

    @Override
    protected void executeRoundImpl() throws TcException {
        reportMetrics();
    }

    @Override
    protected void closeImpl() throws TcException {
        clearMetrics();
        logger.info(THIS_CLASS_NAME, String.format(
                "monitor routine [%s] close done", routine.getFullName()));
    }

    // -----

    void clearMetrics() {
        Metrics.aliveJobsGauge.clear();
        Metrics.aliveTasksGauge.clear();
    }

    void reportMetrics() throws TcException {
        long st = System.currentTimeMillis();
        logger.info(THIS_CLASS_NAME, "report metrics starts...");

        try {
            reportJobs();
            reportTasks();
        } catch (Exception e) {
            TcExceptionBuilder.throwTumblerException(
                    TcErrorEnum.TUMBLER_RETRY_EXCEPTION, e.getMessage());
        } finally {
            long et = System.currentTimeMillis();
            logger.info(THIS_CLASS_NAME, String.format("report metrics ends, using time %d ms", et - st));
        }
    }

    // -----

    private class Cnt {
        long n = 0L;
    }

    Map<String, Cnt> initJobStateCountMap() {
        Map<String, Cnt> map = new HashMap<>();
        for (JobStateEnum state : JobStateEnum.values()) {
            map.put(state.name(), new Cnt());
        }
        return map;
    }

    void reportJobs() throws Exception {
        // read jobs
        Map<String, String> strMap = jobBulletin.readAllJobs();
        List<Job> jobs = JsonUtil.parseJobs(strMap.values());

        // count jobs by state
        Map<String, Cnt> map = initJobStateCountMap();
        for (Job job : jobs) {
            Cnt cnt = map.get(job.getState().name());
            if (cnt != null) {
                cnt.n++;
            }
        }

        // report
        map.forEach((state, cnt) -> {
            Metrics.aliveJobsGauge.labels(state).set(cnt.n);
        });

        // time exceed gauge
        timeExceedGauge.updateByAliveJobs(jobs);
        Metrics.reportJobTimeExceedGauge(timeExceedGauge);
    }

    // -----

    private static final String TASK_TODO = "PLANNED";
    private static final String TASK_RUNNING = "RUNNING";
    private static final String TASK_DONE = "DONE";
    private static final String TASK_ERROR = "ERROR";

    void reportTasks() throws Exception {
        // read tasks
        Map<String, String> todoTasks = taskBulletin.readAllTodoTasks();
        Map<String, String> adoptions = taskBulletin.readAllTaskAdoptions();
        Map<String, String> doneTasks = taskBulletin.readAllDoneTasks();
        Map<String, String> errorTasks = taskBulletin.readAllErrorTasks();

        // report
        Metrics.aliveTasksGauge.labels(TASK_TODO).set(todoTasks.size());
        Metrics.aliveTasksGauge.labels(TASK_RUNNING).set(adoptions.size());
        Metrics.aliveTasksGauge.labels(TASK_DONE).set(doneTasks.size());
        Metrics.aliveTasksGauge.labels(TASK_ERROR).set(errorTasks.size());

        // retry times gauge
        List<Task> aliveTasks = new ArrayList<>();
        for (String s : todoTasks.values()) {
            Task task = JsonUtil.parseTask(s);
            if (task != null) {
                aliveTasks.add(task);
            }
        }
        retryTimesGauge.updateByAliveTasks(aliveTasks);
        Metrics.reportTaskRetryTimesGauge(retryTimesGauge);

        // time exceed gauge
        timeExceedGauge.updateByAliveTasks(aliveTasks);
        Metrics.reportTaskTimeExceedGauge(timeExceedGauge);
    }

    // -----

}
