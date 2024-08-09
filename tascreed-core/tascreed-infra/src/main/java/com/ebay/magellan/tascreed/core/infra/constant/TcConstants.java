package com.ebay.magellan.tascreed.core.infra.constant;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
public class TcConstants {

    // version
    @Value("${tascreed.version}")
    private String tcVersion;

    // namespace
    @Value("${tascreed.namespace}")
    private String tcNamespace;

    // job define
    @Value("${tascreed.define.dirs}")
    private String tcDefineDirsStr;
    @Value("${tascreed.define.graph.validate.enable}")
    private boolean defineGraphValidateEnable;

    // watcher
    @Value("${tascreed.watcher.routine.interval.seconds:30}")
    private int routineWatcherIntervalInSeconds;
    @Value("${tascreed.watcher.task.interval.seconds:30}")
    private int taskWatcherIntervalInSeconds;

    @Value("${tascreed.watcher.routine.switch.on.key}")
    private String routineWatcherSwitchOnKey;
    @Value("${tascreed.watcher.routine.switch.on.default}")
    private String routineWatcherSwitchOnDefault;
    private volatile boolean routineWatcherSwitchOn;

    @Value("${tascreed.watcher.task.switch.on.key}")
    private String taskWatcherSwitchOnKey;
    @Value("${tascreed.watcher.task.switch.on.default}")
    private String taskWatcherSwitchOnDefault;
    private volatile boolean taskWatcherSwitchOn;

    // worker count
    @Value("${tascreed.worker.max.count.overall.key}")
    private String maxWorkerCountOverallKey;
    @Value("${tascreed.worker.max.count.overall.default}")
    private String maxWorkerCountOverallDefault;
    private int maxWorkerCountOverall = -1;

    @Value("${tascreed.worker.max.count.per.host.key}")
    private String maxWorkerCountPerHostKey;
    @Value("${tascreed.worker.max.count.per.host.default}")
    private String maxWorkerCountPerHostDefault;
    private int maxWorkerCountPerHost = -1;

    @Value("${tascreed.worker.affinity.enable}")
    private boolean workerAffinityEnable;

    // routine count
    @Value("${tascreed.routine.max.count.overall.key}")
    private String maxRoutineCountOverallKey;
    @Value("${tascreed.routine.max.count.overall.default}")
    private String maxRoutineCountOverallDefault;
    private int maxRoutineCountOverall = -1;

    @Value("${tascreed.routine.max.count.per.host.key}")
    private String maxRoutineCountPerHostKey;
    @Value("${tascreed.routine.max.count.per.host.default}")
    private String maxRoutineCountPerHostDefault;
    private int maxRoutineCountPerHost = -1;

    // bulletin
    @Value("${tascreed.bulletin.routine.adoption.lock}")
    private String bulletinRoutineAdoptionLock;
    @Value("${tascreed.bulletin.routine.adoption.prefix}")
    private String bulletinRoutineAdoptionPrefix;

    @Value("${tascreed.bulletin.routine.update.lock.prefix}")
    private String bulletinRoutineUpdateLockPrefix;
    @Value("${tascreed.bulletin.routine.checkpoint.prefix}")
    private String bulletinRoutineCheckpointPrefix;

    @Value("${tascreed.bulletin.job.update.lock.prefix}")
    private String bulletinJobUpdateLockPrefix;
    @Value("${tascreed.bulletin.job.info.prefix}")
    private String bulletinJobInfoPrefix;

    @Value("${tascreed.bulletin.task.update.lock.prefix}")
    private String bulletinTaskUpdateLockPrefix;
    @Value("${tascreed.bulletin.task.info.todo.prefix}")
    private String bulletinTaskInfoTodoPrefix;
    @Value("${tascreed.bulletin.task.info.done.prefix}")
    private String bulletinTaskInfoDonePrefix;
    @Value("${tascreed.bulletin.task.info.error.prefix}")
    private String bulletinTaskInfoErrorPrefix;

    @Value("${tascreed.bulletin.task.adoption.lock}")
    private String bulletinTaskAdoptionLock;
    @Value("${tascreed.bulletin.task.adoption.prefix}")
    private String bulletinTaskAdoptionPrefix;

    @Value("${tascreed.bulletin.schedule.update.lock.prefix}")
    private String bulletinScheduleUpdateLockPrefix;
    @Value("${tascreed.bulletin.schedule.info.prefix}")
    private String bulletinScheduleInfoPrefix;

    // ban
    @Value("${tascreed.ban.enable}")
    private boolean banEnable;
    @Value("${tascreed.ban.global.key}")
    private String banGlobalKey;
    @Value("${tascreed.ban.job.define.prefix}")
    private String banJobDefinePrefix;
    @Value("${tascreed.ban.job.prefix}")
    private String banJobPrefix;
    @Value("${tascreed.ban.routine.define.prefix}")
    private String banRoutineDefinePrefix;
    @Value("${tascreed.ban.routine.prefix}")
    private String banRoutinePrefix;

    // duty
    @Value("${tascreed.duty.enable}")
    private boolean dutyEnable;
    @Value("${tascreed.duty.rules.global.key}")
    private String dutyRulesGlobalKey;

    // archive
    @Value("${tascreed.archive.task.enable}")
    private boolean archiveTaskEnable;

    // storage
    @Value("${tascreed.storage.archive}")
    private String storageArchiveStr;
    @Value("${tascreed.storage.bulletin}")
    private String storageBulletin;

    // be able to overwrite etcd constants
    // occupy lease seconds and heartbeat period seconds can overwrite the default etcd constants, not set by default
    @Value("${tascreed.occupy.worker.lease.seconds:0}")
    private int occupyWorkerLeaseInSeconds;
    @Value("${tascreed.occupy.worker.heartbeat.period.seconds:0}")
    private int occupyWorkerHeartbeatPeriodInSeconds;
    @Value("${tascreed.occupy.routine.lease.seconds:0}")
    private int occupyRoutineLeaseInSeconds;
    @Value("${tascreed.occupy.routine.heartbeat.period.seconds:0}")
    private int occupyRoutineHeartbeatPeriodInSeconds;

    // task default max pick times, negative denotes infinity
    @Value("${tascreed.task.default.max.pick.times:-1}")
    private int taskDefaultMaxPickTimes;

    // -----

    public List<String> getTcDefineDirs() {
        List<String> list = new ArrayList<>();
        String[] strs = StringUtils.split(tcDefineDirsStr, ",");
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
