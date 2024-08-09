package com.ebay.magellan.tumbler.core.infra.constant;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
public class TumblerConstants {

    // version
    @Value("${tumbler.version}")
    private String tumblerVersion;

    // namespace
    @Value("${tumbler.namespace}")
    private String tumblerNamespace;

    // job define
    @Value("${tumbler.define.dirs}")
    private String tumblerDefineDirsStr;
    @Value("${tumbler.define.graph.validate.enable}")
    private boolean defineGraphValidateEnable;

    // watcher
    @Value("${tumbler.watcher.routine.interval.seconds:30}")
    private int routineWatcherIntervalInSeconds;
    @Value("${tumbler.watcher.task.interval.seconds:30}")
    private int taskWatcherIntervalInSeconds;

    @Value("${tumbler.watcher.routine.switch.on.key}")
    private String routineWatcherSwitchOnKey;
    @Value("${tumbler.watcher.routine.switch.on.default}")
    private String routineWatcherSwitchOnDefault;
    private volatile boolean routineWatcherSwitchOn;

    @Value("${tumbler.watcher.task.switch.on.key}")
    private String taskWatcherSwitchOnKey;
    @Value("${tumbler.watcher.task.switch.on.default}")
    private String taskWatcherSwitchOnDefault;
    private volatile boolean taskWatcherSwitchOn;

    // worker count
    @Value("${tumbler.worker.max.count.overall.key}")
    private String maxWorkerCountOverallKey;
    @Value("${tumbler.worker.max.count.overall.default}")
    private String maxWorkerCountOverallDefault;
    private int maxWorkerCountOverall = -1;

    @Value("${tumbler.worker.max.count.per.host.key}")
    private String maxWorkerCountPerHostKey;
    @Value("${tumbler.worker.max.count.per.host.default}")
    private String maxWorkerCountPerHostDefault;
    private int maxWorkerCountPerHost = -1;

    @Value("${tumbler.worker.affinity.enable}")
    private boolean workerAffinityEnable;

    // routine count
    @Value("${tumbler.routine.max.count.overall.key}")
    private String maxRoutineCountOverallKey;
    @Value("${tumbler.routine.max.count.overall.default}")
    private String maxRoutineCountOverallDefault;
    private int maxRoutineCountOverall = -1;

    @Value("${tumbler.routine.max.count.per.host.key}")
    private String maxRoutineCountPerHostKey;
    @Value("${tumbler.routine.max.count.per.host.default}")
    private String maxRoutineCountPerHostDefault;
    private int maxRoutineCountPerHost = -1;

    // bulletin
    @Value("${tumbler.bulletin.routine.adoption.lock}")
    private String bulletinRoutineAdoptionLock;
    @Value("${tumbler.bulletin.routine.adoption.prefix}")
    private String bulletinRoutineAdoptionPrefix;

    @Value("${tumbler.bulletin.routine.update.lock.prefix}")
    private String bulletinRoutineUpdateLockPrefix;
    @Value("${tumbler.bulletin.routine.checkpoint.prefix}")
    private String bulletinRoutineCheckpointPrefix;

    @Value("${tumbler.bulletin.job.update.lock.prefix}")
    private String bulletinJobUpdateLockPrefix;
    @Value("${tumbler.bulletin.job.info.prefix}")
    private String bulletinJobInfoPrefix;

    @Value("${tumbler.bulletin.task.update.lock.prefix}")
    private String bulletinTaskUpdateLockPrefix;
    @Value("${tumbler.bulletin.task.info.todo.prefix}")
    private String bulletinTaskInfoTodoPrefix;
    @Value("${tumbler.bulletin.task.info.done.prefix}")
    private String bulletinTaskInfoDonePrefix;
    @Value("${tumbler.bulletin.task.info.error.prefix}")
    private String bulletinTaskInfoErrorPrefix;

    @Value("${tumbler.bulletin.task.adoption.lock}")
    private String bulletinTaskAdoptionLock;
    @Value("${tumbler.bulletin.task.adoption.prefix}")
    private String bulletinTaskAdoptionPrefix;

    @Value("${tumbler.bulletin.schedule.update.lock.prefix}")
    private String bulletinScheduleUpdateLockPrefix;
    @Value("${tumbler.bulletin.schedule.info.prefix}")
    private String bulletinScheduleInfoPrefix;

    // ban
    @Value("${tumbler.ban.enable}")
    private boolean banEnable;
    @Value("${tumbler.ban.global.key}")
    private String banGlobalKey;
    @Value("${tumbler.ban.job.define.prefix}")
    private String banJobDefinePrefix;
    @Value("${tumbler.ban.job.prefix}")
    private String banJobPrefix;
    @Value("${tumbler.ban.routine.define.prefix}")
    private String banRoutineDefinePrefix;
    @Value("${tumbler.ban.routine.prefix}")
    private String banRoutinePrefix;

    // duty
    @Value("${tumbler.duty.enable}")
    private boolean dutyEnable;
    @Value("${tumbler.duty.rules.global.key}")
    private String dutyRulesGlobalKey;

    // archive
    @Value("${tumbler.archive.task.enable}")
    private boolean archiveTaskEnable;

    // storage
    @Value("${tumbler.storage.archive}")
    private String storageArchiveStr;
    @Value("${tumbler.storage.bulletin}")
    private String storageBulletin;

    // be able to overwrite etcd constants
    // occupy lease seconds and heartbeat period seconds can overwrite the default etcd constants, not set by default
    @Value("${tumbler.occupy.worker.lease.seconds:0}")
    private int occupyWorkerLeaseInSeconds;
    @Value("${tumbler.occupy.worker.heartbeat.period.seconds:0}")
    private int occupyWorkerHeartbeatPeriodInSeconds;
    @Value("${tumbler.occupy.routine.lease.seconds:0}")
    private int occupyRoutineLeaseInSeconds;
    @Value("${tumbler.occupy.routine.heartbeat.period.seconds:0}")
    private int occupyRoutineHeartbeatPeriodInSeconds;

    // task default max pick times, negative denotes infinity
    @Value("${tumbler.task.default.max.pick.times:-1}")
    private int taskDefaultMaxPickTimes;

    // -----

    public List<String> getTumblerDefineDirs() {
        List<String> list = new ArrayList<>();
        String[] strs = StringUtils.split(tumblerDefineDirsStr, ",");
        for (String str : strs) {
            String s = StringUtils.trim(str);
            if (StringUtils.isNotBlank(s)) {
                list.add(s);
            }
        }
        return list;
    }

    public List<String> getStorageArchives() {
        List<String> list = new ArrayList<>();
        String[] strs = StringUtils.split(storageArchiveStr, ",");
        for (String str : strs) {
            String s = StringUtils.trim(str);
            if (StringUtils.isNotBlank(s)) {
                list.add(s);
            }
        }
        return list;
    }

    // -----

}
