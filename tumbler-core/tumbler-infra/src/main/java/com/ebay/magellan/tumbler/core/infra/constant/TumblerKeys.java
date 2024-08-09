package com.ebay.magellan.tumbler.core.infra.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class TumblerKeys {

    @Autowired
    private TumblerConstants tumblerConstants;

    // -----

    private static final String JOB_DEFINE_NAME_FORMAT = "%s";
    private static final String JOB_NAME_FORMAT = "%s/%s";
    private static final String TASK_NAME_FORMAT = "%s/%s/%s";
    private static final String ROUTINE_NAME_FORMAT = "%s";
    private static final String SCHEDULE_NAME_FORMAT = "%s";
    private static final String JOB_KEY_PREFIX_FORMAT = "%s/";
    private static final String TASK_KEY_PREFIX_FORMAT = "%s/%s/";

    // -----

    public String withNamespace(String key) {
        return String.format(key, tumblerConstants.getTumblerNamespace());
    }

    // -----

    public String buildRoutineWatcherSwitchOnKey() {
        return withNamespace(tumblerConstants.getRoutineWatcherSwitchOnKey());
    }

    public String buildTaskWatcherSwitchOnKey() {
        return withNamespace(tumblerConstants.getTaskWatcherSwitchOnKey());
    }

    public String buildMaxWorkerCountOverallKey() {
        return withNamespace(tumblerConstants.getMaxWorkerCountOverallKey());
    }

    public String buildMaxWorkerCountPerHostKey() {
        return withNamespace(tumblerConstants.getMaxWorkerCountPerHostKey());
    }

    public String buildMaxRoutineCountOverallKey() {
        return withNamespace(tumblerConstants.getMaxRoutineCountOverallKey());
    }

    public String buildMaxRoutineCountPerHostKey() {
        return withNamespace(tumblerConstants.getMaxRoutineCountPerHostKey());
    }

    // -----

    public String buildJobDefineKey(String head, String... params) {
        return head + String.format(JOB_DEFINE_NAME_FORMAT, params);
    }
    public String buildJobKey(String head, String... params) {
        return head + String.format(JOB_NAME_FORMAT, params);
    }
    public String buildTaskKey(String head, String... params) {
        return head + String.format(TASK_NAME_FORMAT, params);
    }
    public String buildRoutineKey(String head, String... params) {
        return head + String.format(ROUTINE_NAME_FORMAT, params);
    }
    public String buildScheduleKey(String head, String... params) {
        return head + String.format(SCHEDULE_NAME_FORMAT, params);
    }

    public String buildJobKeyPrefix(String head, String... params) {
        return head + String.format(JOB_KEY_PREFIX_FORMAT, params);
    }
    public String buildTaskKeyPrefix(String head, String... params) {
        return head + String.format(TASK_KEY_PREFIX_FORMAT, params);
    }

    // -----

    public String buildJobInfoPrefix() {
        return withNamespace(tumblerConstants.getBulletinJobInfoPrefix());
    }
    public String buildTaskInfoTodoPrefix() {
        return withNamespace(tumblerConstants.getBulletinTaskInfoTodoPrefix());
    }
    public String buildTaskInfoDonePrefix() {
        return withNamespace(tumblerConstants.getBulletinTaskInfoDonePrefix());
    }
    public String buildTaskInfoErrorPrefix() {
        return withNamespace(tumblerConstants.getBulletinTaskInfoErrorPrefix());
    }
    public String buildTaskAdoptionPrefix() {
        return withNamespace(tumblerConstants.getBulletinTaskAdoptionPrefix());
    }
    public String buildRoutineAdoptionPrefix() {
        return withNamespace(tumblerConstants.getBulletinRoutineAdoptionPrefix());
    }
    public String buildRoutineCheckpointPrefix() {
        return withNamespace(tumblerConstants.getBulletinRoutineCheckpointPrefix());
    }
    public String buildScheduleInfoPrefix() {
        return withNamespace(tumblerConstants.getBulletinScheduleInfoPrefix());
    }

    public String buildTaskAdoptionLock() {
        return withNamespace(tumblerConstants.getBulletinTaskAdoptionLock());
    }
    public String buildJobUpdateLockPrefix() {
        return withNamespace(tumblerConstants.getBulletinJobUpdateLockPrefix());
    }
    public String buildTaskUpdateLockPrefix() {
        return withNamespace(tumblerConstants.getBulletinTaskUpdateLockPrefix());
    }
    public String buildRoutineAdoptionLock() {
        return withNamespace(tumblerConstants.getBulletinRoutineAdoptionLock());
    }
    public String buildRoutineUpdateLockPrefix() {
        return withNamespace(tumblerConstants.getBulletinRoutineUpdateLockPrefix());
    }
    public String buildScheduleUpdateLockPrefix() {
        return withNamespace(tumblerConstants.getBulletinScheduleUpdateLockPrefix());
    }

    // -----

    public String buildBanGlobalKey() {
        return withNamespace(tumblerConstants.getBanGlobalKey());
    }
    public String buildBanJobDefinePrefix() {
        return withNamespace(tumblerConstants.getBanJobDefinePrefix());
    }
    public String buildBanJobPrefix() {
        return withNamespace(tumblerConstants.getBanJobPrefix());
    }
    public String buildBanRoutineDefinePrefix() {
        return withNamespace(tumblerConstants.getBanRoutineDefinePrefix());
    }
    public String buildBanRoutinePrefix() {
        return withNamespace(tumblerConstants.getBanRoutinePrefix());
    }

    // -----

    public String buildDutyRulesGlobalKey() {
        return withNamespace(tumblerConstants.getDutyRulesGlobalKey());
    }

    // -----

    public String getJobKey(String jobName, String trigger) {
        return buildJobKey(buildJobInfoPrefix(), jobName, trigger);
    }
    public String getTodoTaskKey(String jobName, String trigger, String taskName) {
        return buildTaskKey(buildTaskInfoTodoPrefix(), jobName, trigger, taskName);
    }
    public String getDoneTaskKey(String jobName, String trigger, String taskName) {
        return buildTaskKey(buildTaskInfoDonePrefix(), jobName, trigger, taskName);
    }
    public String getErrorTaskKey(String jobName, String trigger, String taskName) {
        return buildTaskKey(buildTaskInfoErrorPrefix(), jobName, trigger, taskName);
    }
    public String getTaskAdoptionKey(String jobName, String trigger, String taskName) {
        return buildTaskKey(buildTaskAdoptionPrefix(), jobName, trigger, taskName);
    }
    public String getScheduleKey(String scheduleName) {
        return buildScheduleKey(buildScheduleInfoPrefix(), scheduleName);
    }

    // -----

    public String getJobUpdateLockKey(String jobName, String trigger) {
        return buildJobKey(buildJobUpdateLockPrefix(), jobName, trigger);
    }
    public String getTaskUpdateLockKey(String jobName, String trigger, String taskName) {
        return buildTaskKey(buildTaskUpdateLockPrefix(), jobName, trigger, taskName);
    }
    public String getRoutineUpdateLockKey(String routineFullName) {
        return buildRoutineKey(buildRoutineUpdateLockPrefix(), routineFullName);
    }
    public String getScheduleUpdateLockKey(String scheduleName) {
        return buildScheduleKey(buildScheduleUpdateLockPrefix(), scheduleName);
    }

    // -----

    public String getJobPrefixKey(String jobName) {
        return buildJobKeyPrefix(buildJobInfoPrefix(), jobName);
    }
    public String getTodoTaskOfJobPrefixKey(String jobName, String trigger) {
        return buildTaskKeyPrefix(buildTaskInfoTodoPrefix(), jobName, trigger);
    }
    public String getDoneTaskOfJobPrefixKey(String jobName, String trigger) {
        return buildTaskKeyPrefix(buildTaskInfoDonePrefix(), jobName, trigger);
    }
    public String getErrorTaskOfJobPrefixKey(String jobName, String trigger) {
        return buildTaskKeyPrefix(buildTaskInfoErrorPrefix(), jobName, trigger);
    }
    public String getTaskAdoptionOfJobPrefixKey(String jobName, String trigger) {
        return buildTaskKeyPrefix(buildTaskAdoptionPrefix(), jobName, trigger);
    }

    // -----

    public String getRoutineAdoptionKey(String routineFullName) {
        return buildRoutineKey(buildRoutineAdoptionPrefix(), routineFullName);
    }
    public String getRoutineCheckpointKey(String routineFullName) {
        return buildRoutineKey(buildRoutineCheckpointPrefix(), routineFullName);
    }

    // -----

    public String getBanJobDefineKey(String jobDefineName) {
        return buildJobDefineKey(buildBanJobDefinePrefix(), jobDefineName);
    }
    public String getBanJobKey(String jobName, String trigger) {
        return buildJobKey(buildBanJobPrefix(), jobName, trigger);
    }
    public String getBanRoutineDefineKey(String routineName) {
        return buildRoutineKey(buildBanRoutineDefinePrefix(), routineName);
    }
    public String getBanRoutineKey(String routineFullName) {
        return buildRoutineKey(buildBanRoutinePrefix(), routineFullName);
    }
}
