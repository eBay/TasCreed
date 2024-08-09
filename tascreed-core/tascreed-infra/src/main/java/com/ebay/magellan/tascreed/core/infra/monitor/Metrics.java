package com.ebay.magellan.tascreed.core.infra.monitor;

import com.ebay.magellan.tascreed.core.infra.monitor.metric.RetryTimesGauge;
import com.ebay.magellan.tascreed.core.infra.monitor.metric.TimeExceedGauge;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerErrorEnum;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

public class Metrics {

    // === activity (per node) ===

    public static final Counter jobOprCounter = Counter.build().name("job_opr_counter")
            .help("counter of job operation")
            .labelNames("jobName", "state", "opr")
            .register();

    public static final Counter taskOprCounter = Counter.build().name("task_opr_counter")
            .help("counter of task operation")
            .labelNames("jobName", "stepName", "state", "opr")
            .register();

    public static final Summary jobExecSummary =
            Summary.build().name("job_execution_summary")
                    .help("summary of job execution time")
                    .labelNames("jobName", "state")
                    .quantile(0.5, 0.05)
                    .quantile(0.9, 0.01)
                    .quantile(0.99, 0.001)
                    .quantile(0.999, 0.0001)
                    .register();

    public static final Summary taskExecSummary =
            Summary.build().name("task_execution_summary")
                    .help("summary of task execution time")
                    .labelNames("jobName", "stepName", "state")
                    .quantile(0.5, 0.05)
                    .quantile(0.9, 0.01)
                    .quantile(0.99, 0.001)
                    .quantile(0.999, 0.0001)
                    .register();

    public static final Summary routineRoundExecSummary =
            Summary.build().name("routine_round_execution_summary")
                    .help("summary of routine round execution time")
                    .labelNames("routineName")
                    .quantile(0.5, 0.05)
                    .quantile(0.9, 0.01)
                    .quantile(0.99, 0.001)
                    .quantile(0.999, 0.0001)
                    .register();

    // === state (monitor node) ===

    public static final Gauge aliveJobsGauge = Gauge.build().name("alive_jobs_gauge")
            .help("gauge of alive jobs")
            .labelNames("state")
            .register();

    public static final Gauge aliveTasksGauge = Gauge.build().name("alive_tasks_gauge")
            .help("gauge of alive tasks")
            .labelNames("state")
            .register();

    // === notification (per node) ===

    public static final Counter jobRefreshExecCounter = Counter.build().name("job_refresh_exec_counter")
            .help("counter of job refresh execution")
            .register();

    public static final Summary jobRefreshExecSummary =
            Summary.build().name("job_refresh_exec_summary")
                    .help("summary of job refresh execution")
                    .quantile(0.5, 0.05)
                    .quantile(0.9, 0.01)
                    .quantile(0.99, 0.001)
                    .quantile(0.999, 0.0001)
                    .register();

    // === retry times (monitor node) ===

    // alive task retry times, in step level
    private static final Gauge aliveTaskRetryTimesGauge = Gauge.build().name("alive_task_retry_times_gauge")
            .help("gauge of retry times of alive tasks")
            .labelNames("type", "jobStepName")
            .register();

    // number of alive tasks which are retried or re-picked
    private static final Gauge retryAliveTaskNumGauge = Gauge.build().name("retry_alive_task_num_gauge")
            .help("gauge of alive tasks has retry times")
            .labelNames("type")
            .register();

    // max error times of alive tasks
    private static final Gauge aliveTaskMaxRetryTimesGauge = Gauge.build().name("alive_task_max_retry_times_gauge")
            .help("gauge of max retry times of alive tasks")
            .labelNames("type")
            .register();

    public static void reportTaskRetryTimesGauge(RetryTimesGauge retryTimesGauge) {
        if (retryTimesGauge == null) return;
        reportTaskRetryTimesGauge("picked", retryTimesGauge.getPickedTimesGaugeMap(),
                retryTimesGauge.getNumRepickedTasks(), retryTimesGauge.getMaxRepickedTimes());
        reportTaskRetryTimesGauge("tried", retryTimesGauge.getTriedTimesGaugeMap(),
                retryTimesGauge.getNumRetriedTasks(), retryTimesGauge.getMaxRetriedTimes());
    }

    private static void reportTaskRetryTimesGauge(String type, Map<String, RetryTimesGauge.GaugeValue> values, int num, int max) {
        if (MapUtils.isEmpty(values)) return;
        // step level task retry times
        for (Map.Entry<String, RetryTimesGauge.GaugeValue> entry : values.entrySet()) {
            String k = entry.getKey();
            int v = entry.getValue().getValue();
            aliveTaskRetryTimesGauge.labels(type, k).set(v);
        }
        // num and max times gauge
        retryAliveTaskNumGauge.labels(type).set(num);
        aliveTaskMaxRetryTimesGauge.labels(type).set(max);
    }

    // === slow jobs and tasks (monitor node) ===

    // number of slow jobs
    private static final Gauge slowJobNumGauge = Gauge.build().name("slow_job_num_gauge")
            .help("gauge of slow jobs number")
            .register();
    // number of slow tasks
    private static final Gauge slowTaskNumGauge = Gauge.build().name("slow_task_num_gauge")
            .help("gauge of slow tasks number")
            .register();

    public static void reportJobTimeExceedGauge(TimeExceedGauge timeExceedGauge) {
        if (timeExceedGauge == null) return;
        slowJobNumGauge.set(timeExceedGauge.getNumTimeExceedJobs());
    }
    public static void reportTaskTimeExceedGauge(TimeExceedGauge timeExceedGauge) {
        if (timeExceedGauge == null) return;
        slowJobNumGauge.set(timeExceedGauge.getNumTimeExceedTasks());
    }

    // === caught exceptions (per node) ===

    private static final Counter executionExceptionCounter = Counter.build().name("execution_exception_counter")
            .help("counter of execution exceptions")
            .labelNames("type")
            .register();

    public static void reportExecutionExceptionCounter(TumblerErrorEnum errorEnum) {
        if (errorEnum == null) return;
        executionExceptionCounter.labels(errorEnum.name()).inc();
    }

}
