package com.ebay.magellan.tascreed.core.infra.jobserver.notify;

import com.ebay.magellan.tascreed.core.domain.builder.JobBuilder;
import com.ebay.magellan.tascreed.core.domain.builder.TaskBuilder;
import com.ebay.magellan.tascreed.core.domain.ban.BanContext;
import com.ebay.magellan.tascreed.core.domain.ban.BanLevelEnum;
import com.ebay.magellan.tascreed.core.domain.duty.NodeDutyEnum;
import com.ebay.magellan.tascreed.core.domain.define.JobDefine;
import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.job.JobInstKey;
import com.ebay.magellan.tascreed.core.domain.state.StateChange;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.domain.trait.Trait;
import com.ebay.magellan.tascreed.core.domain.util.JsonUtil;
import com.ebay.magellan.tascreed.core.infra.constant.TumblerKeys;
import com.ebay.magellan.tascreed.core.infra.duty.DutyHelper;
import com.ebay.magellan.tascreed.core.infra.jobserver.msg.JobMsgState;
import com.ebay.magellan.tascreed.core.infra.jobserver.msg.JobMsgStatePool;
import com.ebay.magellan.tascreed.core.infra.storage.archive.ArchiveStorageFactory;
import com.ebay.magellan.tascreed.core.infra.ban.BanHelper;
import com.ebay.magellan.tascreed.core.infra.constant.TumblerConstants;
import com.ebay.magellan.tascreed.core.infra.context.JobsContext;
import com.ebay.magellan.tascreed.core.infra.jobserver.help.JobHelper;
import com.ebay.magellan.tascreed.core.infra.monitor.Metrics;
import com.ebay.magellan.tascreed.core.infra.opr.OprEnum;
import com.ebay.magellan.tascreed.core.infra.repo.JobDefineRepo;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.JobBulletin;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.TaskBulletin;
import com.ebay.magellan.tascreed.depend.common.collection.GeneralDataListMap;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tascreed.depend.common.msg.MsgState;
import com.ebay.magellan.tascreed.depend.common.retry.RetryBackoffStrategy;
import com.ebay.magellan.tascreed.depend.common.retry.RetryCounter;
import com.ebay.magellan.tascreed.depend.common.retry.RetryCounterFactory;
import com.ebay.magellan.tascreed.depend.common.retry.RetryStrategy;
import com.ebay.magellan.tascreed.depend.common.util.DefaultValueUtil;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@Component
@Scope("prototype")
public class JobNotifyExecThread implements Runnable {
    public static final double MILLISECONDS_PER_SECOND = 1E3;
    private static final String THIS_CLASS_NAME = JobNotifyExecThread.class.getSimpleName();

    private RetryStrategy retryStrategy = RetryBackoffStrategy.newDefaultInstance();

    private JobBuilder jobBuilder = new JobBuilder();
    private TaskBuilder taskBuilder = new TaskBuilder();

    @Autowired
    private TumblerConstants tumblerConstants;

    @Autowired
    private JobDefineRepo jobDefineRepo;

    @Autowired
    private TumblerKeys tumblerKeys;
    @Autowired
    private JobBulletin jobBulletin;
    @Autowired
    private TaskBulletin taskBulletin;

    @Autowired
    private ArchiveStorageFactory archiveStorageFactory;

    @Autowired
    private BanHelper banHelper;

    @Autowired
    private DutyHelper dutyHelper;

    @Autowired
    private JobHelper jobHelper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    @Override
    public void run() {
        while (true) {
            try {
                MsgState<JobInstKey> state = JobMsgStatePool.getInstance().readState();
                executeMsgState(JobMsgState.parseFrom(state));
            } catch (Exception e) {
                /**
                 * watcher thread can't stop,
                 * when exception happens, just catch and log it and continue looping
                 */
                logger.error(THIS_CLASS_NAME, e.getMessage());
            }
        }
    }

    // -----

    void executeMsgState(JobMsgState jms) throws Exception {
        if (jms == null) return;

        dutyHelper.dutyEnableCheck(NodeDutyEnum.JOB_SERVER);

        long st = System.currentTimeMillis();
        if (jobRefreshProcess(jms)) {
            long et = System.currentTimeMillis();
            Metrics.jobRefreshExecCounter.inc();
            Metrics.jobRefreshExecSummary.observe(et - st);
        }
    }

    // -----

    boolean jobRefreshProcess(JobMsgState jms) throws Exception {
        long st, et = System.currentTimeMillis();

        // 1. fetch all jobs
        logger.info(THIS_CLASS_NAME, "start to fetch all jobs");
        st = et;
        JobsContext jobsContext = buildJobsContext(jms);
        et = System.currentTimeMillis();
        logger.info(THIS_CLASS_NAME, String.format(
                "fetch all jobs end, using time: %s ms", et - st));

        // 2. update job states
        logger.info(THIS_CLASS_NAME, "start to update job states");
        st = et;
        updateJobStates(jobsContext);
        et = System.currentTimeMillis();
        logger.info(THIS_CLASS_NAME, String.format(
                "update job states end, using time: %s ms", et - st));

        // 3. create new tasks
        logger.info(THIS_CLASS_NAME, "start to create new tasks");
        st = et;
        createNewTasks(jobsContext);
        et = System.currentTimeMillis();
        logger.info(THIS_CLASS_NAME, String.format(
                "create new tasks end, using time: %s ms", et - st));

        // 4. clear job states
        logger.info(THIS_CLASS_NAME, "start to clear job states");
        st = et;
        clearJobStates(jobsContext);
        et = System.currentTimeMillis();
        logger.info(THIS_CLASS_NAME, String.format(
                "clear job states end, using time: %s ms", et - st));

        return true;
    }

    // -----

    JobsContext buildJobsContext(JobMsgState jms) throws TumblerException {
        Map<String, Job> jobMap = new HashMap<>();
        try {
            if (jms.isAll()) {
                Map<String, String> map = jobBulletin.readAllJobs();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    Job job = buildJob(entry.getValue());
                    if (job != null) {
                        jobMap.put(entry.getKey(), job);
                    }
                }
            } else {
                for (JobInstKey jobId : jms.getKeys()) {
                    String key = tumblerKeys.getJobKey(jobId.getName(), jobId.getTrigger());
                    String value = jobBulletin.readJob(jobId.getName(), jobId.getTrigger());
                    Job job = buildJob(value);
                    if (job != null) {
                        jobMap.put(key, job);
                    }
                }
            }
        } catch (Exception e) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_RETRY_EXCEPTION, e.getMessage());
        }
        return JobsContext.init(jobMap);
    }

    // build job, parse from json string, and assemble with job define
    Job buildJob(String str) {
        Job job = JsonUtil.parseJob(str);
        if (job != null) {
            JobDefine jd = jobDefineRepo.getDefine(job.getJobName());
            jobBuilder.assembleJob(job, jd);
        }
        return job;
    }

    // -----

    void updateJobStates(JobsContext jobsContext) {
        long curTime = System.currentTimeMillis();
        boolean success = false;
        RetryCounter retryCounter = RetryCounterFactory.buildRetryCounter(retryStrategy);
        while (true) {
            try {
                // 1. fetch all done tasks
                List<Task> doneTasks = fetchAllDoneTasks();

                // 2. aggregate tasks by job instance key
                GeneralDataListMap<JobInstKey, Task> aggTaskMap = aggregateTasks(doneTasks);

                // 3. for each job, try to update its state
                for (Job job : jobsContext.getAllJobs()) {
                    if (job == null) continue;

                    // 3.1 build job instance key
                    JobInstKey id = new JobInstKey(job.getJobName(), job.getTrigger());

                    // 3.2 get done tasks of this job
                    List<Task> doneTasksOfJob = aggTaskMap.get(id);

                    // 3.3 update job/step state by its done tasks
                    StateChange stateChange = jobHelper.updateJobStateByDoneTasks(job, doneTasksOfJob, curTime);

                    // 3.4 submit job, and remove done tasks
                    if (stateChange.anyStateChanged()) {   // if any state changed
                        try {
                            if (jobHelper.submitJobWithTasks(job, null, doneTasksOfJob, null)) {
                                logger.info(THIS_CLASS_NAME, String.format("update job %s success", id));
                            } else {
                                logger.warn(THIS_CLASS_NAME, String.format("update job %s failed", id));
                            }
                        } catch (TumblerException e) {
                            logger.warn(THIS_CLASS_NAME, String.format(
                                    "Exception encountered when update job %s: %s", id, e.getMessage()));
                        }
                    }
                }

                // 4. archive done tasks
                if (tumblerConstants.isArchiveTaskEnable()) {
                    List<Task> archiveTasks = doneTasks.stream()
                            .filter(t -> t.getTraits().containsTrait(Trait.ARCHIVE))
                            .collect(Collectors.toList());
                    boolean archived = archiveStorageFactory.getArchiveStorage().archiveTasks(archiveTasks);
                    if (archived) {
                        logger.info(THIS_CLASS_NAME, String.format("archived %d done tasks to %s",
                                archiveTasks.size(), archiveStorageFactory.getArchiveStorage().name()));
                    }
                }

                success = true;
            } catch (TumblerException e) {
                success = false;
                if (e.isRetry()) {
                    retryCounter.grow();
                    String head = String.format("Retryable exception when update job states: %s", e.getMessage());
                    logger.error(THIS_CLASS_NAME, String.format("%s, %s", head, retryCounter.status()));
                } else {
                    retryCounter.forceStop();
                    String head = String.format("Non-Retryable exception when update job states: %s", e.getMessage());
                    logger.error(THIS_CLASS_NAME, String.format("%s, %s", head, retryCounter.status()));
                }
            }

            if (success || !retryCounter.isAlive()) {
                break;
            }
            retryCounter.waitForNextRetry();
        }
    }

    List<Task> fetchAllDoneTasks() throws TumblerException {
        List<Task> tasks = new ArrayList<>();
        try {
            Map<String, String> map = taskBulletin.readAllDoneTasks();
            tasks = JsonUtil.parseTasks(map.values());
        } catch (Exception e) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_RETRY_EXCEPTION, e.getMessage());
        }
        return tasks;
    }

    GeneralDataListMap<JobInstKey, Task> aggregateTasks(List<Task> tasks) {
        GeneralDataListMap<JobInstKey, Task> map = new GeneralDataListMap<>();
        for (Task task : tasks) {
            JobInstKey pair = new JobInstKey(task.getJobName(), task.getTrigger());
            map.append(pair, task);
        }
        return map;
    }

    // -----

    void createNewTasks(JobsContext jobsContext) {
        boolean success = false;
        RetryCounter retryCounter = RetryCounterFactory.buildRetryCounter(retryStrategy);
        while (true) {
            try {
                // 1. build ban context
                BanContext banContext = banHelper.buildBanContext(BanLevelEnum.TASK_CREATE, false);

                // 2. try to create new tasks for each job
                for (Job job : jobsContext.getAllJobs()) {
                    // do not create new tasks if banned
                    if (banHelper.isTaskCreateBanned(
                            banContext, job.getJobName(), job.getTrigger())) continue;

                    JobInstKey id = new JobInstKey(job.getJobName(), job.getTrigger());

                    // 3. build new tasks
                    List<Task> newTasks = taskBuilder.buildNewTasks(job);
                    if (CollectionUtils.isNotEmpty(newTasks)) {
                        logger.info(THIS_CLASS_NAME, String.format(
                                "job %s build %d tasks", id, CollectionUtils.size(newTasks)));
                        // 4. submit new tasks
                        try {
                            if (jobHelper.submitJobWithTasks(job, newTasks, null, null)) {
                                logger.info(THIS_CLASS_NAME, String.format(
                                        "create tasks for job %s success", id));

                                for (Task task : newTasks) {
                                    Metrics.taskOprCounter.labels(task.getJobName(), task.getStepName(),
                                            task.getTaskState().name(), OprEnum.CREATE.name()).inc();
                                }
                            } else {
                                logger.warn(THIS_CLASS_NAME, String.format(
                                        "create tasks for job %s failed", id));
                            }
                        } catch (TumblerException e) {
                            logger.warn(THIS_CLASS_NAME, String.format(
                                    "Exception encountered when create tasks for job %s: %s", id, e.getMessage()));
                        }
                    } else {
                        logger.info(THIS_CLASS_NAME, String.format("no new task created for job %s", id));
                    }
                }

                success = true;
            } catch (TumblerException e) {
                success = false;
                if (e.isRetry()) {
                    retryCounter.grow();
                    String head = String.format("Retryable exception when create new tasks: %s", e.getMessage());
                    logger.error(THIS_CLASS_NAME, String.format("%s, %s", head, retryCounter.status()));
                } else {
                    retryCounter.forceStop();
                    String head = String.format("Non-Retryable exception when create new tasks: %s", e.getMessage());
                    logger.error(THIS_CLASS_NAME, String.format("%s, %s", head, retryCounter.status()));
                }
            } catch (Exception e) {
                success = false;
            }

            if (success || !retryCounter.isAlive()) {
                break;
            }
            retryCounter.waitForNextRetry();
        }
    }

    // -----

    void clearJobStates(JobsContext jobsContext) throws Exception {
        Date now = new Date();
        for (Map.Entry<String, Job> entry : jobsContext.getAllJobsMap().entrySet()) {
            Job job = entry.getValue();

            // archive job
            if (job.getState().canAutoArchive()) {
                archiveStorageFactory.getArchiveStorage().archiveJob(entry.getKey(), job.toJson(),
                        job.getJobName(), job.getTrigger(), job.getState().name());
                logger.info(THIS_CLASS_NAME, String.format("archive job %s to %s",
                        entry.getKey(), archiveStorageFactory.getArchiveStorage().name()));
                jobBulletin.deleteKeyAnyway(entry.getKey());
                logger.info(THIS_CLASS_NAME, String.format("delete job %s from ETCD",
                        entry.getKey()));
            }

            // job with determined state can expose the metrics
            if (job.getState().canAutoArchive()) {
                Metrics.jobOprCounter.labels(job.getJobName(),
                        job.getState().name(), OprEnum.FINISH.name()).inc();

                Date startTime = DefaultValueUtil.defValue(job.getMidState().getCreateTime(), now);
                Date endTime = DefaultValueUtil.defValue(job.getMidState().getModifyTime(), now);
                long timeInMs = endTime.getTime() - startTime.getTime();
                Metrics.jobExecSummary.labels(job.getJobName(), job.getState().name()).observe(timeInMs);
                logger.info(THIS_CLASS_NAME, String.format("job [%s] with state [%s] finishes, has cost %d ms",
                        job.getJobName(), job.getState().name(), timeInMs));
            }
        }
    }

}
