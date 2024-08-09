package com.ebay.magellan.tumbler.core.infra.jobserver;

import com.ebay.magellan.tumbler.core.domain.builder.JobUpdateBuilder;
import com.ebay.magellan.tumbler.core.domain.ban.BanContext;
import com.ebay.magellan.tumbler.core.domain.ban.BanLevelEnum;
import com.ebay.magellan.tumbler.core.domain.duty.NodeDutyEnum;
import com.ebay.magellan.tumbler.core.domain.task.Task;
import com.ebay.magellan.tumbler.core.domain.validate.JobValidator;
import com.ebay.magellan.tumbler.core.domain.validate.ValidateResult;
import com.ebay.magellan.tumbler.core.infra.ban.BanHelper;
import com.ebay.magellan.tumbler.core.infra.duty.DutyHelper;
import com.ebay.magellan.tumbler.core.infra.jobserver.help.JobHelper;
import com.ebay.magellan.tumbler.core.infra.jobserver.msg.JobMsgItem;
import com.ebay.magellan.tumbler.core.infra.jobserver.msg.JobMsgStatePool;
import com.ebay.magellan.tumbler.core.infra.monitor.Metrics;
import com.ebay.magellan.tumbler.core.infra.opr.OprEnum;
import com.ebay.magellan.tumbler.core.infra.storage.archive.ArchiveStorageFactory;
import com.ebay.magellan.tumbler.core.infra.storage.bulletin.JobBulletin;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerErrorEnum;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerExceptionBuilder;
import com.ebay.magellan.tumbler.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tumbler.core.domain.builder.JobBuilder;
import com.ebay.magellan.tumbler.core.infra.repo.JobDefineRepo;
import com.ebay.magellan.tumbler.core.domain.define.JobDefine;
import com.ebay.magellan.tumbler.core.domain.job.Job;
import com.ebay.magellan.tumbler.core.domain.job.JobInstKey;
import com.ebay.magellan.tumbler.core.domain.request.JobRequest;
import com.ebay.magellan.tumbler.core.domain.util.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class JobServer {
    private static final String THIS_CLASS_NAME = JobServer.class.getSimpleName();

    private JobBuilder jobBuilder = new JobBuilder();
    private JobUpdateBuilder jobUpdateBuilder = new JobUpdateBuilder();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    @Autowired
    private JobDefineRepo jobDefineRepo;

    @Autowired
    private JobBulletin jobBulletin;

    @Autowired
    private ArchiveStorageFactory archiveStorageFactory;

    @Autowired
    private BanHelper banHelper;

    @Autowired
    private DutyHelper dutyHelper;

    @Autowired
    private JobHelper jobHelper;

    private static final JobValidator jobValidator = new JobValidator();

    // -----

    public void assembleJobWithDefine(Job job) {
        if (job != null) {
            JobDefine jd = jobDefineRepo.getDefine(job.getJobName());
            jobBuilder.assembleJob(job, jd);
        }
    }

    // -----

    public Job findAliveJobByJobNameAndTrigger(String jobName, String trigger) {
        if (StringUtils.isBlank(jobName) || StringUtils.isBlank(trigger)) return null;
        JobInstKey id = new JobInstKey(jobName, trigger);
        return findAliveJobByJobIdPair(id);
    }

    Job findAliveJobByJobIdPair(JobInstKey pair) {
        if (pair == null) return null;
        return JsonUtil.parseJob(jobBulletin.readJob(pair.getName(), pair.getTrigger()));
    }

    // -----

    public Job findJobByJobNameAndTrigger(String jobName, String trigger) {
        if (StringUtils.isBlank(jobName) || StringUtils.isBlank(trigger)) return null;
        JobInstKey id = new JobInstKey(jobName, trigger);
        return findJobByJobIdPair(id);
    }

    // find job, only parse from json string
    public Job findJobByJobIdPair(JobInstKey pair) {
        Job job = findAliveJobByJobIdPair(pair);
        if (job == null) {
            // try to find in archive
            job = archiveStorageFactory.getArchiveStorage().findJob(pair);
        }
        return job;
    }

    boolean existAliveJobInstanceWithSameJobName(String jobName) {
        if (StringUtils.isBlank(jobName)) return false;
        try {
            Map<String, String> kvs = jobBulletin.readJobsByName(jobName);
            return MapUtils.isNotEmpty(kvs);
        } catch (Exception e) {
            // throw runtime exception to stop this job creation
            throw new RuntimeException(e);
        }
    }

    // -----

    public Job createNewJob(JobRequest jr) throws TumblerException {
        if (jr == null) return null;
        JobInstKey id = new JobInstKey(jr.getJobName(), jr.getTrigger());

        logger.info(THIS_CLASS_NAME, String.format("the job instance key %s is fresh, will create new job", id));
        JobDefine jd = jobDefineRepo.getDefine(jr.getJobName());
        if (jd == null) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_JOB_EXCEPTION,
                    String.format("create new job fails: job define %s not found", jr.getJobName()));
        }

        // do not submit new job if banned
        BanContext banContext = banHelper.buildBanContext(BanLevelEnum.JOB_SUBMIT, true);
        if (banHelper.isJobSubmitBanned(banContext, jr.getJobName())) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_JOB_EXCEPTION,
                    String.format("create new job fails: job define %s is banned", jr.getJobName()));
        }

        if (jd.isUniqueAliveInstance()) {
            if (existAliveJobInstanceWithSameJobName(jr.getJobName())) {
                TumblerExceptionBuilder.throwTumblerException(
                        TumblerErrorEnum.TUMBLER_FATAL_JOB_EXCEPTION,
                        String.format("create new job fails: alive job %s exists, only one alive job is allowed", id));
            }
        }

        Job job = jobBuilder.buildJob(jd, jr);

        ValidateResult vr = jobValidator.validate(job);
        if (!vr.isValid()) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_VALIDATION_EXCEPTION, vr.showMsg());
        }

        return job;
    }

    // -----

    /**
     * submit job request to create new job
     * @param jr job request
     * @return new created job if success; or existed job if already created before; or null if create fails
     * @throws TumblerException if any exception
     */
    public Job submitJobRequest(JobRequest jr) throws TumblerException {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.JOB_SERVER);

        if (jr == null) return null;
        boolean success = false;
        JobInstKey id = new JobInstKey(jr.getJobName(), jr.getTrigger());
        Job job = findJobByJobIdPair(id);
        if (job == null) {
            job = createNewJob(jr);

            logger.info(THIS_CLASS_NAME, String.format("submit job %s without build tasks", id));
            try {
                success = jobHelper.submitJobWithTasks(job, null, null, null);
            } catch (TumblerException e) {
                logger.error(THIS_CLASS_NAME, String.format("submit job fails: %s", e.getMessage()));
                throw e;
            }

            if (!success) {
                job = null;
                logger.warn(THIS_CLASS_NAME, String.format("create job %s fails", id));
            } else {
                logger.info(THIS_CLASS_NAME, String.format("create job %s success", id));

                // update notify
                JobMsgStatePool.getInstance().addItem(JobMsgItem.refresh(id));

                Metrics.jobOprCounter.labels(job.getJobName(),
                        job.getState().name(), OprEnum.CREATE.name()).inc();
            }
        } else {
            logger.info(THIS_CLASS_NAME, String.format("the job instance key %s is duplicated", id));
        }
        return job;
    }

    // -----

    /**
     * update alive job by request, support to update:
     * - the step pack size if the step can still create new tasks
     * not locked before query job, so submit might fail if the job changes after queried
     * @param jr job request
     * @return updated job if success; or null if submit fails
     * @throws TumblerException if any exception
     */
    public Job updateAliveJob(JobRequest jr) throws TumblerException {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.JOB_SERVER);

        if (jr == null) return null;
        boolean success = false;
        JobInstKey id = new JobInstKey(jr.getJobName(), jr.getTrigger());

        // 1. find alive job
        Job job = findAliveJobByJobIdPair(id);
        if (job == null) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_JOB_EXCEPTION,
                    String.format("alive job %s does not exist", id));
        }

        // 2. assemble with job define
        JobDefine jd = jobDefineRepo.getDefine(jr.getJobName());
        if (jd == null) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_JOB_EXCEPTION,
                    String.format("job define %s not found", jr.getJobName()));
        }
        jobBuilder.assembleJob(job, jd);

        // 3. update job by request
        boolean updated = jobUpdateBuilder.updateJob(job, jr);
        if (!updated) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_JOB_EXCEPTION,
                    String.format("alive job %s no need to update", id));
        }

        // 4. validate job
        ValidateResult vr = jobValidator.validate(job);
        if (!vr.isValid()) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_VALIDATION_EXCEPTION, vr.showMsg());
        }

        // 5. submit job
        logger.info(THIS_CLASS_NAME, String.format("update alive job %s", id));
        try {
            success = jobHelper.submitJobWithTasks(job, null, null, null);
        } catch (TumblerException e) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_JOB_EXCEPTION,
                    String.format("update alive job fails: %s", e.getMessage()), e);
        }

        if (!success) {
            job = null;
            logger.warn(THIS_CLASS_NAME, String.format("update alive job %s fails", id));
        } else {
            logger.info(THIS_CLASS_NAME, String.format("update alive job %s success", id));

            Metrics.jobOprCounter.labels(job.getJobName(),
                    job.getState().name(), OprEnum.UPDATE.name()).inc();
        }

        return job;
    }

    // -----

    /**
     * retry alive error job, reset the error job/step state, and retry error tasks
     * @param jr job request
     * @return retry error job if success; or null if submit fails
     * @throws TumblerException if any exception
     */
    public Job retryAliveErrorJob(JobRequest jr) throws TumblerException {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.JOB_SERVER);

        if (jr == null) return null;
        boolean success = false;
        JobInstKey id = new JobInstKey(jr.getJobName(), jr.getTrigger());

        // 1. find alive job
        Job job = findAliveJobByJobIdPair(id);
        if (job == null) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_JOB_EXCEPTION,
                    String.format("alive job %s does not exist", id));
        }

        // 2. assemble with job define
        JobDefine jd = jobDefineRepo.getDefine(jr.getJobName());
        if (jd == null) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_JOB_EXCEPTION,
                    String.format("job define %s not found", jr.getJobName()));
        }
        jobBuilder.assembleJob(job, jd);

        // 3. find error tasks
        List<Task> errorTasks = fetchErrorTasksOfJob(job);
        if (CollectionUtils.isEmpty(errorTasks)) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_JOB_EXCEPTION,
                    String.format("alive job %s has no error task, no need to retry", id));
        }

        // 4. update job state by done tasks
        List<Task> doneTasks = fetchDoneTasksOfJob(job);
        jobHelper.updateJobStateByDoneTasks(job, doneTasks, System.currentTimeMillis());

        // 5. reset error job/step state, reset error task state
        boolean updated = job.resetForRetry();
        for (Task task : errorTasks) {
            updated = task.resetForRetry() || updated;
        }
        if (!updated) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_JOB_EXCEPTION,
                    String.format("alive job %s no state updated, no need to retry", id));
        }

        // 6. submit job, retry tasks, remove done tasks and error tasks
        logger.info(THIS_CLASS_NAME, String.format("retry alive error job %s", id));
        try {
            success = jobHelper.submitJobWithTasks(job, errorTasks, doneTasks, errorTasks);
        } catch (TumblerException e) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_JOB_EXCEPTION,
                    String.format("retry alive error job fails: %s", e.getMessage()), e);
        }

        if (!success) {
            job = null;
            logger.warn(THIS_CLASS_NAME, String.format("retry alive error job %s fails", id));
        } else {
            logger.info(THIS_CLASS_NAME, String.format("retry alive error job %s success", id));

            Metrics.jobOprCounter.labels(job.getJobName(),
                    job.getState().name(), OprEnum.RETRY.name()).inc();
        }

        return job;
    }

    List<Task> fetchDoneTasksOfJob(Job job) throws TumblerException {
        List<Task> tasks = new ArrayList<>();
        if (job != null) {
            try {
                Map<String, String> map = jobBulletin.readDoneTasksOfJob(
                        job.getJobName(), job.getTrigger());
                tasks = JsonUtil.parseTasks(map.values());
            } catch (Exception e) {
                TumblerExceptionBuilder.throwTumblerException(
                        TumblerErrorEnum.TUMBLER_RETRY_EXCEPTION, e.getMessage());
            }
        }
        return tasks;
    }
    List<Task> fetchErrorTasksOfJob(Job job) throws TumblerException {
        List<Task> tasks = new ArrayList<>();
        if (job != null) {
            try {
                Map<String, String> map = jobBulletin.readErrorTasksOfJob(
                        job.getJobName(), job.getTrigger());
                tasks = JsonUtil.parseTasks(map.values());
            } catch (Exception e) {
                TumblerExceptionBuilder.throwTumblerException(
                        TumblerErrorEnum.TUMBLER_RETRY_EXCEPTION, e.getMessage());
            }
        }
        return tasks;
    }

    // -----

}
