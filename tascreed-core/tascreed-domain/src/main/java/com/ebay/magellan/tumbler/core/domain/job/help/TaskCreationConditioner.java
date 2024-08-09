package com.ebay.magellan.tumbler.core.domain.job.help;

import com.ebay.magellan.tumbler.core.domain.define.StepDefine;
import com.ebay.magellan.tumbler.core.domain.graph.GraphNode;
import com.ebay.magellan.tumbler.core.domain.graph.Phase;
import com.ebay.magellan.tumbler.core.domain.job.Job;
import com.ebay.magellan.tumbler.core.domain.job.JobStep;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskCreationConditioner {

    // get step nodes can create tasks, only considering step state and topology, not considering the time and task count
    public static List<GraphNode<JobStep>> getStepNodesCanCreateTasks(Job job) {
        if (job == null || job.getStepGraph() == null) return new ArrayList<>();
        return job.getStepGraph().getNodesByPredicates(
                TaskCreationConditioner::needCreateTasks_step,
                TaskCreationConditioner::canCreateTasks_prevStep,
                true,
                null,
                true)
                .stream()
                .filter(gn -> stepCanCreateTask(gn.getData(), job))
                .collect(Collectors.toList());
    }

    // get step nodes need create tasks, only considering step state
    public static List<GraphNode<JobStep>> getStepNodesNeedCreateTasks(Job job) {
        if (job == null || job.getStepGraph() == null) return new ArrayList<>();
        return job.getStepGraph().getNodesByPredicates(
                TaskCreationConditioner::needCreateTasks_step,
                null,
                true,
                null,
                true);
    }

    // get step nodes in ready state, only considering step state
    public static List<GraphNode<JobStep>> getStepNodesInReadyState(Job job) {
        if (job == null || job.getStepGraph() == null) return new ArrayList<>();
        return job.getStepGraph().getNodesByPredicates(
                TaskCreationConditioner::stepInReadyState,
                null,
                true,
                null,
                true);
    }

    // -----

    public static boolean canCreateTasks_count(int taskCount) {
        return taskCount > 0;
    }

    public static boolean canCreateTasks_job(Job job) {
        // undone job can create task
        return job != null && job.getState().canCreateTask();
    }

    public static boolean needCreateTasks_step(JobStep step) {
        // dormant or start step need create task
        return step != null && step.getState().canCreateTask();
    }

    public static boolean canCreateTasks_prevStep(JobStep prevStep) {
        return prevStep != null && prevStep.getState().resultSuccess();
    }

    public static boolean stepInReadyState(JobStep step) {
        return step != null && step.getState().isReady();
    }

    // -----

    // a step can create task, its recent prev phase steps should be all done
    private static boolean stepCanCreateTask(JobStep step, Job job) {
        if (step == null || job == null) return false;
        if (step.getStepDefine() == null) return false;

        boolean allPrevPhaseStepsDone = true;
        Phase<JobStep> prevPhase = job.getStepPhaseList().findPrevPhase(
                step.getStepDefine().phaseValue());
        if (prevPhase != null) {
            for (JobStep prevStep : prevPhase.getNodes().values()) {
                if (prevStep != null) {
                    allPrevPhaseStepsDone = allPrevPhaseStepsDone && prevStep.getState().done();
                }
            }
        }
        return allPrevPhaseStepsDone;
    }

}
