package com.ebay.magellan.tumbler.core.domain.builder;

import com.ebay.magellan.tumbler.core.domain.define.conf.StepPackConf;
import com.ebay.magellan.tumbler.core.domain.define.conf.StepShardConf;
import com.ebay.magellan.tumbler.core.domain.graph.GraphNode;
import com.ebay.magellan.tumbler.core.domain.graph.Phase;
import com.ebay.magellan.tumbler.core.domain.job.Job;
import com.ebay.magellan.tumbler.core.domain.job.JobStep;
import com.ebay.magellan.tumbler.core.domain.job.crt.TaskPackCreation;
import com.ebay.magellan.tumbler.core.domain.job.crt.TaskShardCreation;
import com.ebay.magellan.tumbler.core.domain.job.help.TaskCreationConditioner;
import com.ebay.magellan.tumbler.core.domain.state.StepStateEnum;
import com.ebay.magellan.tumbler.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tumbler.core.domain.task.Task;
import com.ebay.magellan.tumbler.core.domain.task.conf.TaskPackConf;
import com.ebay.magellan.tumbler.core.domain.task.conf.TaskShardConf;
import com.ebay.magellan.tumbler.core.domain.task.mid.TaskMidState;
import com.ebay.magellan.tumbler.depend.common.util.DateUtil;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TaskBuilder {

    private static final int MAX_BUILD_TASK_COUNT = 100;

    public List<Task> buildNewTasks(Job job) {
        return buildNewTasks(job, MAX_BUILD_TASK_COUNT);
    }

    // when the step is independent, or dependent steps all done, new tasks can be built
    private List<Task> buildNewTasks(Job job, int taskCount) {
        List<Task> tasks = new ArrayList<>();
        if (!TaskCreationConditioner.canCreateTasks_count(taskCount)) return tasks;
        if (!TaskCreationConditioner.canCreateTasks_job(job)) return tasks;

        long curTime = System.currentTimeMillis();
        if (!DateUtil.reachDate(curTime, job.getMidState().getAfterTime())) return tasks;

        int c = taskCount;
        List<GraphNode<JobStep>> stepNodesCanCreateTasks = TaskCreationConditioner.getStepNodesCanCreateTasks(job);
        for (GraphNode<JobStep> stepNode : stepNodesCanCreateTasks) {
            if (!TaskCreationConditioner.canCreateTasks_count(c)) break;
            List<Task> newTasks = buildTasks(stepNode.getData(), job, c, curTime);
            tasks.addAll(newTasks);
            c -= newTasks.size();
        }

        return tasks;
    }

    // -----

    private List<Task> buildTasks(JobStep step, Job job, int taskCount, long curTime) {
        List<Task> tasks = new ArrayList<>();
        if (!DateUtil.reachDate(curTime, step.getMidState().getAfterTime())) return tasks;

        if (step.getStepDefine() != null && job != null) {
            step.setState(StepStateEnum.START);

            boolean isDone = false;
            if (step.getStepDefine().isShard()) {
                StepShardConf conf = step.getStepAllConf().getShardConf();
                TaskShardCreation creation = step.getTaskAllCreation().fetchShardCreation(conf);

                tasks.addAll(buildShardTasks(step, job, taskCount));
                isDone = creation.isDone(conf);
            } else if (step.getStepDefine().isPack()) {
                StepPackConf conf = step.getStepAllConf().getPackConf();
                TaskPackCreation creation = step.getTaskAllCreation().fetchTaskPackCreation(conf);

                tasks.addAll(buildPackTasks(step, job, taskCount));
                isDone = creation.isDone(conf);
            } else {
                tasks.add(buildTask(step, job, JobStep.SIMPLE_TASK_INDEX));
                isDone = true;
            }

            if (isDone) {
                step.setState(StepStateEnum.READY);
            }
        }

        return tasks;
    }

    private Task buildTask(JobStep step, Job job, Long index) {
        Task task = new Task();
        task.setJobName(job.getJobName());
        task.setTrigger(job.getTrigger());
        task.setPriority(job.getPriority());
        task.setStepName(step.getStepName());
        task.setExeClass(step.getExeClass());
        task.setAffinityRule(step.getAffinityRule());

        // assemble task all conf
        assembleTaskAllConf(task, step);

        // assemble dependent step states to task
        assembleDependentStepStates(task, job);
        // assemble recent prev phase step states to task
        assemblePrevPhaseStepStates(task, job, step);

        // set traits
        task.getTraits().copyFromTraits(step.getTraits());

        // assemble task params by job params, step params and job updated params
        assembleMultiTaskParams(task, job.getParams(), step.getParams(), job.getUpdatedParams());

        if (step.getStepDefine() != null) {
            task.setStepType(step.getStepDefine().getStepType());
        }

        // mid state
        assembleTaskMidState(task.getMidState(), step);

        step.addTaskState(index, TaskStateEnum.UNDONE);

        return task;
    }

    List<Task> buildShardTasks(JobStep step, Job job, int taskCount) {
        List<Task> tasks = new ArrayList<>();
        if (taskCount <= 0) return tasks;

        StepShardConf conf = step.getStepAllConf().getShardConf();
        TaskShardCreation creation = step.getTaskAllCreation().fetchShardCreation(conf);

        int onHoldTaskCount = step.countOnHoldTask();
        int newShardCount = conf.taskCount() - onHoldTaskCount;
        newShardCount = newShardCount > 0 ? newShardCount : 0;
        newShardCount = newShardCount < taskCount ? newShardCount : taskCount;

        int count = 0;
        while (!creation.isDone(conf) && count < newShardCount) {
            int index = creation.getLastShardIndex() + 1;

            Task task = buildTask(step, job, Long.valueOf(index));
            task.getTaskAllConf().setShardConf(new TaskShardConf(conf.getShard(), index));
            tasks.add(task);

            creation.setLastShardIndex(index);
            count++;
        }

        return tasks;
    }

    List<Task> buildPackTasks(JobStep step, Job job, int taskCount) {
        List<Task> tasks = new ArrayList<>();
        if (taskCount <= 0) return tasks;

        StepPackConf conf = step.getStepAllConf().getPackConf();
        TaskPackCreation creation = step.getTaskAllCreation().fetchTaskPackCreation(conf);

        int onHoldTaskCount = step.countOnHoldTask();
        int newPackCount = conf.taskCount() - onHoldTaskCount;
        newPackCount = newPackCount > 0 ? newPackCount : 0;
        newPackCount = newPackCount < taskCount ? newPackCount : taskCount;

        int count = 0;
        while (!creation.isDone(conf) && count < newPackCount) {
            long pid = creation.getLastPackId() + 1;
            long start = creation.getLastOffset() + 1;
            long end = start + (conf.getSize() - 1);
            if (!conf.isInfinite()) {   // if not infinite, need stop at end
                end = end < conf.getEnd() ? end : conf.getEnd();
            }
            if (end < start) break;

            Task task = buildTask(step, job, Long.valueOf(pid));
            task.getTaskAllConf().setPackConf(new TaskPackConf(pid, start, end));
            tasks.add(task);
            step.addTaskState(pid, TaskStateEnum.UNDONE);

            creation.setLastPackId(pid);
            creation.setLastOffset(end);
            count++;
        }

        return tasks;
    }

    // -----

    // assemble task all conf
    private void assembleTaskAllConf(Task task, JobStep step) {
        if (task == null || step == null) return;
        task.getTaskAllConf().setMaxPickTimes(step.getStepAllConf().getMaxPickTimes());
    }

    // assemble task params, the later will cover the former
    private void assembleMultiTaskParams(Task task, Map<String, String>... paramsArr) {
        for (Map<String, String> params : paramsArr) {
            assembleTaskParams(task, params);
        }
    }
    private void assembleTaskParams(Task task, Map<String, String> params) {
        if (task == null || MapUtils.isEmpty(params)) return;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            task.addParam(entry.getKey(), entry.getValue());
        }
    }

    // assemble dependent step states to task
    private void assembleDependentStepStates(Task task, Job job) {
        if (task == null || job == null || job.getStepGraph() == null) return;
        GraphNode<JobStep> stepNode = job.getStepGraph().findNodeByName(task.getStepName());
        if (stepNode != null) {
            for (GraphNode<JobStep> prevNode : stepNode.getPrevNodes()) {
                if (prevNode != null && prevNode.getData() != null) {
                    JobStep prevStep = prevNode.getData();
                    task.addDependentStepState(prevStep.getStepName(), prevStep.getState());
                }
            }
        }
    }

    // assemble recent prev phase step states to task
    private void assemblePrevPhaseStepStates(Task task, Job job, JobStep step) {
        if (task == null || job == null || step == null) return;
        if (step.getStepDefine() == null) return;

        Phase<JobStep> prevPhase = job.getStepPhaseList().findPrevPhase(
                step.getStepDefine().phaseValue());
        if (prevPhase != null) {
            for (JobStep prevStep : prevPhase.getNodes().values()) {
                if (prevStep != null) {
                    task.addPrevPhaseStepState(prevStep.getStepName(), prevStep.getState());
                }
            }
        }
    }

    // assemble task mid state
    private void assembleTaskMidState(TaskMidState midState, JobStep step) {
        if (midState == null) return;
        if (step != null) {
            midState.setDuration(step.getMidState().getDuration());
        }
        midState.setCreateTime(new Date());
    }

}
