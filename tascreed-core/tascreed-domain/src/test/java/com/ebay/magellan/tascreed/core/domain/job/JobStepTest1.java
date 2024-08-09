package com.ebay.magellan.tascreed.core.domain.job;

import com.ebay.magellan.tascreed.core.domain.builder.JobBuilder;
import com.ebay.magellan.tascreed.core.domain.builder.TaskBuilder;
import com.ebay.magellan.tascreed.core.domain.help.TestRepo;
import com.ebay.magellan.tascreed.core.domain.state.StepStateEnum;
import com.ebay.magellan.tascreed.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JobStepTest1 {

    JobBuilder jobBuilder = new JobBuilder();
    TaskBuilder taskBuilder = new TaskBuilder();

    @Test
    public void updateJobStepStateByTaskStates1() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest1);
        JobStep step = job.getSteps().get(0);
        List<Task> tasks = taskBuilder.buildNewTasks(job);

        for (Task task : tasks) {
            step.updateTaskState(task.getTaskAllConf(), TaskStateEnum.SUCCESS);
        }

        step.updateStepStateByTaskStates();
        assertEquals(StepStateEnum.START, step.getState());

        tasks = taskBuilder.buildNewTasks(job);
        for (Task task : tasks) {
            step.updateTaskState(task.getTaskAllConf(), TaskStateEnum.SUCCESS);
        }
        tasks = taskBuilder.buildNewTasks(job);
        for (Task task : tasks) {
            step.updateTaskState(task.getTaskAllConf(), TaskStateEnum.SUCCESS);
        }

        step.updateStepStateByTaskStates();
        assertEquals(StepStateEnum.SUCCESS, step.getState());
    }

    @Test
    public void updateJobStepStateByTaskStates2() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest1);
        JobStep step = job.getSteps().get(0);
        List<Task> tasks = taskBuilder.buildNewTasks(job);

        for (Task task : tasks) {
            step.updateTaskState(task.getTaskAllConf(), TaskStateEnum.SUCCESS);
        }
        step.updateTaskState(tasks.get(0).getTaskAllConf(), TaskStateEnum.ERROR);

        step.updateStepStateByTaskStates();
        assertEquals(StepStateEnum.START, step.getState());
    }

    @Test
    public void updateJobStepStateByTaskStates3() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest1);
        JobStep step = job.getSteps().get(0);
        List<Task> tasks = taskBuilder.buildNewTasks(job);

        for (Task task : tasks) {
            step.updateTaskState(task.getTaskAllConf(), TaskStateEnum.SUCCESS);
        }
        step.updateTaskState(tasks.get(0).getTaskAllConf(), TaskStateEnum.UNDONE);

        step.updateStepStateByTaskStates();
        assertEquals(StepStateEnum.START, step.getState());
    }

    @Test
    public void updateJobStepStateByTaskStates4() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest1);
        JobStep step = job.getSteps().get(0);
        List<Task> tasks = taskBuilder.buildNewTasks(job);

        for (Task task : tasks) {
            step.updateTaskState(task.getTaskAllConf(), TaskStateEnum.SUCCESS);
        }
        step.updateTaskState(tasks.get(0).getTaskAllConf(), TaskStateEnum.FAILED);

        step.updateStepStateByTaskStates();
        assertEquals(StepStateEnum.START, step.getState());

        tasks = taskBuilder.buildNewTasks(job);
        for (Task task : tasks) {
            step.updateTaskState(task.getTaskAllConf(), TaskStateEnum.SUCCESS);
        }
        tasks = taskBuilder.buildNewTasks(job);
        for (Task task : tasks) {
            step.updateTaskState(task.getTaskAllConf(), TaskStateEnum.SUCCESS);
        }

        step.updateStepStateByTaskStates();
        assertEquals(StepStateEnum.ACCEPTABLE_FAILED, step.getState());
    }

    // -----

    @Test
    public void updateProgression() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest1);
        JobStep step = job.getSteps().get(0);
        List<Task> tasks = taskBuilder.buildNewTasks(job);

        step.updateProgression();
        assertEquals("0.00%", step.getProgression().getValue());

        for (Task task : tasks) {
            step.updateTaskState(task.getTaskAllConf(), TaskStateEnum.SUCCESS);
        }

        step.updateProgression();
        assertEquals("33.33%", step.getProgression().getValue());
    }

}
