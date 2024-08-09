package com.ebay.magellan.tascreed.core.infra.taskworker.help;

import com.ebay.magellan.tascreed.core.domain.affinity.AffinityRuleRegistry;
import com.ebay.magellan.tascreed.core.domain.ban.BanContext;
import com.ebay.magellan.tascreed.core.domain.occupy.OccupyInfo;
import com.ebay.magellan.tascreed.core.domain.ban.BanLevelEnum;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.domain.task.TaskCandidate;
import com.ebay.magellan.tascreed.core.domain.task.WeightLabel;
import com.ebay.magellan.tascreed.core.domain.util.JsonUtil;
import com.ebay.magellan.tascreed.core.domain.util.SortUtil;
import com.ebay.magellan.tascreed.core.infra.ban.BanHelper;
import com.ebay.magellan.tascreed.core.infra.conf.TumblerGlobalConfig;
import com.ebay.magellan.tascreed.core.infra.constant.TumblerKeys;
import com.ebay.magellan.tascreed.core.infra.executor.TaskExecutorFactory;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.TaskBulletin;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tascreed.depend.common.util.DateUtil;
import com.ebay.magellan.tascreed.depend.ext.etcd.lock.EtcdLock;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TaskOccupyHelper {

    private static final String THIS_CLASS_NAME = TaskOccupyHelper.class.getSimpleName();

    @Autowired
    private TumblerKeys tumblerKeys;

    @Autowired
    private TumblerGlobalConfig tumblerGlobalConfig;

    @Autowired
    private TaskBulletin taskBulletin;

    @Autowired
    private TaskExecutorFactory taskExecutorFactory;

    @Autowired
    private BanHelper banHelper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    // -----

    private String getTaskAdoptionLockKey() {
        return tumblerKeys.buildTaskAdoptionLock();
    }

    /**
     * Try to pick one task from todoTask prefix
     * @param adoptionValue the value of task adoption, it is the thread name by default
     * @return task if picked success, null if no task to pick
     * @throws TumblerException if any exception
     */
    public Task tryPickOneTask(String adoptionValue) throws TumblerException {
        Task task = null;

        String taskAdoptionLock = getTaskAdoptionLockKey();
        EtcdLock lock = null;
        try {
            lock = taskBulletin.lock(taskAdoptionLock);

            task = tryPickOneTaskImpl(adoptionValue);
        } catch (Exception e) {
            TumblerExceptionBuilder.throwEtcdRetryableException(e);
        } finally {
            try {
                taskBulletin.unlock(lock);
            } catch (Exception e) {
                TumblerExceptionBuilder.throwEtcdRetryableException(e);
            }
        }
        return task;
    }

    // -----

    Task tryPickOneTaskImpl(String adoptionValue) throws TumblerException {
        Task pickedTask = null;
        try {
            String adoptionKey = null;

            // 1. check if there's available worker to pick task
            Map<String, String> adoptions = taskBulletin.readAllTaskAdoptions();
            if (!workerAvailable(MapUtils.size(adoptions))) {
                return null;
            }
            Map<String, String> todoTasks = taskBulletin.readAllTodoTasks();

            // 2. filter task candidates
            List<TaskCandidate> taskCandidates = filterTaskCandidates(adoptions, todoTasks);

            // 3. pick a task
            if (CollectionUtils.isNotEmpty(taskCandidates)) {
                // pick the task with max weight
                TaskCandidate winner = SortUtil.getFirstCandidate(taskCandidates);
                adoptionKey = winner.getAdoptionKey();
                pickedTask = winner.getTask();
                logger.info(THIS_CLASS_NAME, String.format("will pick task %s, with weight %s",
                        winner.getAdoptionKey(), winner.getWeight()));
            } else {
                logger.warn(THIS_CLASS_NAME, String.format("no task can be picked by this thread"));
            }

            // 4. adopt task
            if (StringUtils.isNotBlank(adoptionKey) && pickedTask != null) {
                OccupyInfo occupyInfo = taskBulletin.occupy(adoptionKey, adoptionValue);
                pickedTask.setOccupyInfo(occupyInfo);
            }
        } catch (Exception e) {
            TumblerExceptionBuilder.throwEtcdRetryableException(e);
        }
        return pickedTask;
    }

    // -----

    // there's available worker to occupy any task
    boolean workerAvailable(int adoptedWorkerCount) {
        int maxWorkerCountOverall = tumblerGlobalConfig.getMaxWorkerCountOverall();
        return adoptedWorkerCount < maxWorkerCountOverall;
    }

    List<TaskCandidate> filterTaskCandidates(Map<String, String> adoptions,
                                             Map<String, String> todoTasks) throws TumblerException {
        // 1. build ban context
        BanContext banContext = banHelper.buildBanContext(BanLevelEnum.TASK_PICK, false);

        Map<String, AdoptionPercentage> adoptionPercentageMap = new HashMap<>();

        long curTime = System.currentTimeMillis();

        // 2. filter task candidates
        List<TaskCandidate> taskCandidates = new ArrayList<>();
        for (String str : todoTasks.values()) {
            Task task = JsonUtil.parseTask(str);
            if (task != null) {
                String key = taskBulletin.getTaskAdoptionKey(task);
                boolean adopted = adoptions.containsKey(key);
                updateAdoptionPercentageMap(adoptionPercentageMap, task, adopted);

                if (!adopted) {
                    // ignore banned task
                    if (banHelper.isTaskPickBanned(
                            banContext, task.getJobName(), task.getTrigger())) continue;

                    // ignore if task can not be picked
                    if (!canPickTask(task, curTime)) continue;

                    if (tumblerKeys.getTumblerConstants().isWorkerAffinityEnable()) {
                        // filter by affinity
                        int affinityWeight = AffinityRuleRegistry.affinityWeight(task);
                        if (affinityWeight > 0) {    // check affinity
                            taskCandidates.add(new TaskCandidate(task, key, new WeightLabel(task.getPriorityWeight(), affinityWeight)));
                        }
                    } else {
                        taskCandidates.add(new TaskCandidate(task, key, new WeightLabel(task.getPriorityWeight(), 1)));
                    }
                }
            }
        }

        // 3. update candidates weight by adoption percentage
        for (TaskCandidate taskCandidate : taskCandidates) {
            updateTaskCandidateEmptyRate(taskCandidate, adoptionPercentageMap);
        }

        return taskCandidates;
    }

    // check if the task can be picked
    boolean canPickTask(Task task, long curTime) {
        if (task == null) return false;

        // task pick after time should be reached, task executor should exist
        return DateUtil.reachDate(curTime, task.getMidState().getAfterTime())
                && (taskExecutorFactory.taskExecutorExists(task));
    }

    void updateAdoptionPercentageMap(Map<String, AdoptionPercentage> adoptionPercentageMap, Task task, boolean adopted) {
        if (adoptionPercentageMap == null || task == null) return;
        String key = task.getStepFullName();
        AdoptionPercentage ap = adoptionPercentageMap.get(key);
        if (ap == null) {
            ap = new AdoptionPercentage();
            adoptionPercentageMap.put(key, ap);
        }
        if (ap != null) {
            ap.increase(adopted);
        }
    }

    void updateTaskCandidateEmptyRate(TaskCandidate taskCandidate, Map<String, AdoptionPercentage> adoptionPercentageMap) {
        if (taskCandidate == null || adoptionPercentageMap == null) return;
        Task task = taskCandidate.getTask();
        if (task == null) return;
        String key = task.getStepFullName();
        AdoptionPercentage ap = adoptionPercentageMap.get(key);
        if (ap != null) {
            taskCandidate.setEmptyRate(ap.planned, ap.adopted);
        }
    }

    // -----

    class AdoptionPercentage {
        int planned;
        int adopted;

        void increase(boolean adp) {
            planned++;
            if (adp) {
                adopted++;
            }
        }
    }

    // -----

}
