package com.ebay.magellan.tumbler.core.infra.jobserver;

import com.ebay.magellan.tumbler.core.domain.ban.BanLevelEnum;
import com.ebay.magellan.tumbler.core.domain.ban.BanTargetEnum;
import com.ebay.magellan.tumbler.core.domain.duty.NodeDutyEnum;
import com.ebay.magellan.tumbler.core.domain.define.JobDefine;
import com.ebay.magellan.tumbler.core.domain.duty.NodeDutyRules;
import com.ebay.magellan.tumbler.core.domain.job.Job;
import com.ebay.magellan.tumbler.core.domain.routine.Routine;
import com.ebay.magellan.tumbler.core.domain.routine.RoutineDefine;
import com.ebay.magellan.tumbler.core.domain.task.Task;
import com.ebay.magellan.tumbler.core.domain.task.TaskInstKey;
import com.ebay.magellan.tumbler.core.domain.trait.Trait;
import com.ebay.magellan.tumbler.core.domain.util.FilterUtil;
import com.ebay.magellan.tumbler.core.domain.util.JsonUtil;
import com.ebay.magellan.tumbler.core.infra.constant.TumblerKeys;
import com.ebay.magellan.tumbler.core.infra.storage.bulletin.*;
import com.ebay.magellan.tumbler.core.infra.duty.DutyHelper;
import com.ebay.magellan.tumbler.core.infra.routine.repo.RoutineDefineRepo;
import com.ebay.magellan.tumbler.core.infra.routine.repo.RoutineRepo;
import com.ebay.magellan.tumbler.core.infra.storage.archive.ArchiveStorageFactory;
import com.ebay.magellan.tumbler.core.infra.ban.BanHelper;
import com.ebay.magellan.tumbler.core.infra.monitor.Metrics;
import com.ebay.magellan.tumbler.core.infra.opr.OprEnum;
import com.ebay.magellan.tumbler.core.infra.repo.JobDefineRepo;
import com.ebay.magellan.tumbler.depend.common.collection.KeyValuePair;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerErrorEnum;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerExceptionBuilder;
import com.ebay.magellan.tumbler.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tumbler.depend.common.util.DefaultValueUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StateServer {
    private static final String THIS_CLASS_NAME = StateServer.class.getSimpleName();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    @Autowired
    private TumblerKeys tumblerKeys;
    @Autowired
    private JobBulletin jobBulletin;
    @Autowired
    private TaskBulletin taskBulletin;
    @Autowired
    private RoutineBulletin routineBulletin;

    @Autowired
    private ArchiveStorageFactory archiveStorageFactory;

    @Autowired
    private JobDefineRepo jobDefineRepo;

    @Autowired
    private RoutineDefineRepo routineDefineRepo;
    @Autowired
    private RoutineRepo routineRepo;

    @Autowired
    private BanHelper banHelper;

    @Autowired
    private DutyHelper dutyHelper;

    // -----

    public Task findArchivedTask(String jobName, String trigger, String taskName) {
        TaskInstKey id = new TaskInstKey(jobName, trigger, taskName);
        return archiveStorageFactory.getArchiveStorage().findTask(id);
    }

    // -----

    // find alive jobs, only parse from json string
    public List<Job> findAliveJobs(Optional<String> jobNameOpt, Optional<String> triggerOpt) throws Exception {
        Map<String, String> kvs = jobBulletin.readAllJobs();
        return FilterUtil.filterJobs(
                JsonUtil.parseJobs(kvs.values()),
                jobNameOpt, triggerOpt);
    }

    // find to-do tasks, only parse from json string
    public List<Task> findTodoTasks(Optional<String> jobNameOpt,
                                    Optional<String> triggerOpt,
                                    Optional<String> stepNameOpt) throws Exception {
        Map<String, String> kvs = taskBulletin.readAllTodoTasks();
        return FilterUtil.filterTasks(
                JsonUtil.parseTasks(kvs.values()),
                jobNameOpt, triggerOpt, stepNameOpt);
    }

    // find done tasks, only parse from json string
    public List<Task> findDoneTasks(Optional<String> jobNameOpt,
                                    Optional<String> triggerOpt,
                                    Optional<String> stepNameOpt) throws Exception {
        Map<String, String> kvs = taskBulletin.readAllDoneTasks();
        return FilterUtil.filterTasks(
                JsonUtil.parseTasks(kvs.values()),
                jobNameOpt, triggerOpt, stepNameOpt);
    }

    // find error tasks, only parse from json string
    public List<Task> findErrorTasks(Optional<String> jobNameOpt,
                                     Optional<String> triggerOpt,
                                     Optional<String> stepNameOpt) throws Exception {
        Map<String, String> kvs = taskBulletin.readAllErrorTasks();
        return FilterUtil.filterTasks(
                JsonUtil.parseTasks(kvs.values()),
                jobNameOpt, triggerOpt, stepNameOpt);
    }

    // -----

    public List<KeyValuePair> findTaskAdoptions(Optional<String> filterOpt) throws Exception {
        Map<String, String> kvs = taskBulletin.readAllTaskAdoptions();
        List<KeyValuePair> list = new ArrayList<>();
        for (Map.Entry<String, String> kv : kvs.entrySet()) {
            if (filterOpt.isPresent() && !StringUtils.contains(kv.getKey(), filterOpt.get())) continue;
            list.add(new KeyValuePair(kv.getKey(), kv.getValue()));
        }
        return list;
    }

    public KeyValuePair<String, String> findTaskAdoption(String jobName, String trigger, String taskName) {
        String k = tumblerKeys.getTaskAdoptionKey(jobName, trigger, taskName);
        String v = taskBulletin.getSingleValue(k);
        if (StringUtils.isBlank(v)) return null;
        return new KeyValuePair(k, v);
    }

    public KeyValuePair<String, String> deleteTaskAdoption(String jobName, String trigger, String taskName) throws Exception {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.STATE_SERVER);

        KeyValuePair<String, String> pair = findTaskAdoption(jobName, trigger, taskName);
        if (pair == null) return null;
        boolean deleted = taskBulletin.deleteIfEquals(pair.getKey(), pair.getValue());
        return deleted ? pair : null;
    }

    // -----

    public List<KeyValuePair> findRoutineAdoptions(Optional<String> filterOpt) throws Exception {
        Map<String, String> kvs = routineBulletin.readAllRoutineAdoptions();
        List<KeyValuePair> list = new ArrayList<>();
        for (Map.Entry<String, String> kv : kvs.entrySet()) {
            if (filterOpt.isPresent() && !StringUtils.contains(kv.getKey(), filterOpt.get())) continue;
            list.add(new KeyValuePair(kv.getKey(), kv.getValue()));
        }
        return list;
    }

    public KeyValuePair<String, String> findRoutineAdoption(String routineFullName) {
        String k = tumblerKeys.getRoutineAdoptionKey(routineFullName);
        String v = routineBulletin.getSingleValue(k);
        if (StringUtils.isBlank(v)) return null;
        return new KeyValuePair(k, v);
    }

    public KeyValuePair<String, String> deleteRoutineAdoption(String routineFullName) throws Exception {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.STATE_SERVER);

        KeyValuePair<String, String> pair = findRoutineAdoption(routineFullName);
        if (pair == null) return null;
        boolean deleted = routineBulletin.deleteIfEquals(pair.getKey(), pair.getValue());
        return deleted ? pair : null;
    }

    // -----

    public Task deleteTodoTask(String jobName, String trigger, String taskName) throws Exception {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.STATE_SERVER);

        String k = tumblerKeys.getTodoTaskKey(jobName, trigger, taskName);
        String v = jobBulletin.getSingleValue(k);
        if (StringUtils.isBlank(v)) return null;
        boolean deleted = jobBulletin.deleteIfEquals(k, v);

        // delete adoption
        if (deleted) {
            String adoptionKey = tumblerKeys.getTaskAdoptionKey(jobName, trigger, taskName);
            jobBulletin.deleteKeyAnyway(adoptionKey);
        }

        Task task = null;
        if (deleted) {
            task = JsonUtil.parseTask(v);
            if (task != null) {
                Metrics.taskOprCounter.labels(task.getJobName(), task.getStepName(),
                        task.getTaskState().name(), OprEnum.DELETE.name()).inc();
            }
        }

        return task;
    }

    public Job deleteAliveJob(String jobName, String trigger, Boolean archive) throws Exception {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.STATE_SERVER);

        String k = tumblerKeys.getJobKey(jobName, trigger);
        String v = jobBulletin.getSingleValue(k);
        if (StringUtils.isBlank(v)) return null;

        boolean deleted = jobBulletin.deleteIfEquals(k, v);
        if (deleted) {
            String todoTaskOfJobPrefixKey = tumblerKeys.getTodoTaskOfJobPrefixKey(jobName, trigger);
            String doneTaskOfJobPrefixKey = tumblerKeys.getDoneTaskOfJobPrefixKey(jobName, trigger);
            String errorTaskOfJobPrefixKey = tumblerKeys.getErrorTaskOfJobPrefixKey(jobName, trigger);
            String taskAdoptionOfJobPrefixKey = tumblerKeys.getTaskAdoptionOfJobPrefixKey(jobName, trigger);

            jobBulletin.deletePrefixAnyway(todoTaskOfJobPrefixKey);
            jobBulletin.deletePrefixAnyway(doneTaskOfJobPrefixKey);
            jobBulletin.deletePrefixAnyway(errorTaskOfJobPrefixKey);
            jobBulletin.deletePrefixAnyway(taskAdoptionOfJobPrefixKey);
        }

        // archive job to es
        if (deleted) {
            boolean forceArchive = DefaultValueUtil.booleanValue(archive);
            if (forceArchive) {
                Job job = JsonUtil.parseJob(v);
                if (job != null) {
                    job.getTraits().trySetTrait(Trait.DELETED, true);
                    v = job.toJson();
                    archiveStorageFactory.getArchiveStorage().archiveJob(k, v,
                            job.getJobName(), job.getTrigger(), job.getState().name());
                    logger.info(THIS_CLASS_NAME, String.format(
                            "upload to es for job %s with value %s", k, v));
                }
            }
        }

        Job job = null;
        if (deleted) {
            job = JsonUtil.parseJob(v);
            if (job != null) {
                Metrics.jobOprCounter.labels(job.getJobName(),
                        job.getState().name(), OprEnum.DELETE.name()).inc();
            }
        }

        return job;
    }

    // -----

    BanLevelEnum parseBanLevel(String banLevelStr) {
        return BanLevelEnum.buildByName(banLevelStr);
    }

    void validateBanTarget(BanLevelEnum banLevel, BanTargetEnum banTarget) throws TumblerException {
        if (!banLevel.getTarget().compatible(banTarget)) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_VALIDATION_EXCEPTION,
                    String.format("ban level %s is not compatible with ban target %s",
                            banLevel.getName(), banTarget.name()));
        }
    }

    public boolean banGlobal(String banLevelStr) throws Exception {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.STATE_SERVER);

        BanLevelEnum banLevel = parseBanLevel(banLevelStr);
        validateBanTarget(banLevel, BanTargetEnum.JOB);

        return banHelper.submitBanGlobal(banLevel);
    }

    public boolean resumeGlobal() throws Exception {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.STATE_SERVER);

        return banHelper.resumeBanGlobal();
    }

    public boolean banJobDefine(String jobDefineName, String banLevelStr) throws Exception {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.STATE_SERVER);

        BanLevelEnum banLevel = parseBanLevel(banLevelStr);
        validateBanTarget(banLevel, BanTargetEnum.JOB);

        JobDefine jd = jobDefineRepo.getDefine(jobDefineName);
        if (jd == null) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_VALIDATION_EXCEPTION,
                    String.format("job define %s doesn't exist", jobDefineName));
        }
        return banHelper.submitBanJobDefine(jobDefineName, banLevel);
    }

    public boolean resumeJobDefine(String jobDefineName) throws Exception {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.STATE_SERVER);

        return banHelper.deleteBanJobDefine(jobDefineName);
    }

    public boolean banJob(String jobName, String trigger, String banLevelStr) throws Exception {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.STATE_SERVER);

        BanLevelEnum banLevel = parseBanLevel(banLevelStr);
        validateBanTarget(banLevel, BanTargetEnum.JOB);

        String aliveJobStr = jobBulletin.readJob(jobName, trigger);
        if (aliveJobStr == null) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_VALIDATION_EXCEPTION,
                    String.format("alive job (%s, %s) doesn't exist", jobName, trigger));
        }
        return banHelper.submitBanJob(jobName, trigger, banLevel);
    }

    public boolean resumeJob(String jobName, String trigger) throws Exception {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.STATE_SERVER);

        return banHelper.deleteBanJob(jobName, trigger);
    }

    // -----

    public boolean banRoutineDefine(String routineName, String banLevelStr) throws Exception {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.STATE_SERVER);

        BanLevelEnum banLevel = parseBanLevel(banLevelStr);
        validateBanTarget(banLevel, BanTargetEnum.ROUTINE);

        RoutineDefine rd = routineDefineRepo.getRoutineDefine(routineName);
        if (rd == null) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_VALIDATION_EXCEPTION,
                    String.format("routine define %s doesn't exist", routineName));
        }
        return banHelper.submitBanRoutineDefine(routineName, banLevel);
    }

    public boolean resumeRoutineDefine(String routineName) throws Exception {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.STATE_SERVER);

        return banHelper.deleteBanRoutineDefine(routineName);
    }

    public boolean banRoutine(String routineFullName, String banLevelStr) throws Exception {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.STATE_SERVER);

        BanLevelEnum banLevel = parseBanLevel(banLevelStr);
        validateBanTarget(banLevel, BanTargetEnum.ROUTINE);

        Routine routine = routineRepo.getRoutine(routineFullName);
        if (routine == null) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_VALIDATION_EXCEPTION,
                    String.format("routine %s doesn't exist", routineFullName));
        }
        return banHelper.submitBanRoutine(routineFullName, banLevel);
    }

    public boolean resumeRoutine(String routineFullName) throws Exception {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.STATE_SERVER);

        return banHelper.deleteBanRoutine(routineFullName);
    }

    // -----

    public NodeDutyRules readDutyRules(Boolean forceRefresh) throws Exception {
        boolean fr = DefaultValueUtil.booleanValue(forceRefresh);
        return dutyHelper.readDutyRules(fr);
    }

    public NodeDutyRules submitDutyRules(NodeDutyRules nodeDutyRules) throws Exception {
        if (nodeDutyRules == null) return null;
        return dutyHelper.submitDutyRules(nodeDutyRules);
    }

    public boolean deleteDutyRules() throws Exception {
        return dutyHelper.deleteDutyRules();
    }

}
