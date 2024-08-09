package com.ebay.magellan.tumbler.core.domain.builder;

import com.ebay.magellan.tumbler.core.domain.define.conf.StepPackConf;
import com.ebay.magellan.tumbler.core.domain.define.conf.StepShardConf;
import com.ebay.magellan.tumbler.core.domain.help.TestRepo;
import com.ebay.magellan.tumbler.core.domain.job.Job;
import com.ebay.magellan.tumbler.core.domain.job.JobStep;
import com.ebay.magellan.tumbler.core.domain.job.crt.TaskPackCreation;
import com.ebay.magellan.tumbler.core.domain.job.crt.TaskShardCreation;
import com.ebay.magellan.tumbler.core.domain.state.StepStateEnum;
import com.ebay.magellan.tumbler.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tumbler.core.domain.task.Task;
import com.ebay.magellan.tumbler.core.domain.trait.Trait;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TaskBuilderTest {

    private JobBuilder jobBuilder = new JobBuilder();
    private TaskBuilder taskBuilder = new TaskBuilder();

    @Test
    public void buildIndependentTasks1() throws Exception {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest1);

        List<Task> tasks = taskBuilder.buildNewTasks(job);
        assertEquals(2, tasks.size());
        assertEquals("prep.shard-1", tasks.get(1).getTaskName());
        assertEquals(1, tasks.get(1).getTaskAllConf().getShardConf().getIndex());
        assertEquals(StepStateEnum.START, job.getSteps().get(0).getState());
        assertEquals(TaskStateEnum.UNDONE, job.getSteps().get(0).getTaskState(1L));

        Task task1 = tasks.get(1);
        assertFalse(task1.getTraits().containsTrait(Trait.CAN_IGNORE));
        assertFalse(task1.getTraits().containsTrait(Trait.DELETED));
        assertFalse(task1.getTraits().containsTrait(Trait.CAN_FAIL));
        assertTrue(task1.getTraits().containsTrait(Trait.ARCHIVE));

        tasks = taskBuilder.buildNewTasks(job);
        assertEquals(0, tasks.size());

        job.getSteps().get(0).addTaskState(0L, TaskStateEnum.SUCCESS);
        job.getSteps().get(0).addTaskState(1L, TaskStateEnum.SUCCESS);
        tasks = taskBuilder.buildNewTasks(job);
        assertEquals(2, tasks.size());
        assertEquals("prep.shard-3", tasks.get(1).getTaskName());
        assertEquals(3, tasks.get(1).getTaskAllConf().getShardConf().getIndex());

        job.getSteps().get(0).addTaskState(2L, TaskStateEnum.SUCCESS);
        job.getSteps().get(0).addTaskState(3L, TaskStateEnum.SUCCESS);
        tasks = taskBuilder.buildNewTasks(job);
        assertEquals(2, tasks.size());
        assertEquals("prep.shard-4", tasks.get(0).getTaskName());
        assertEquals(4, tasks.get(0).getTaskAllConf().getShardConf().getIndex());
        assertEquals(StepStateEnum.READY, job.getSteps().get(0).getState());
        assertEquals(TaskStateEnum.SUCCESS, job.getSteps().get(0).getTaskState(2L));
        assertEquals(TaskStateEnum.UNDONE, job.getSteps().get(0).getTaskState(4L));
    }

    @Test
    public void buildIndependentTasks2() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest1);
        job.getSteps().get(0).setState(StepStateEnum.SUCCESS);

        List<Task> tasks = taskBuilder.buildNewTasks(job);
        assertEquals(6, tasks.size());
        assertEquals("calc.pack-1", tasks.get(1).getTaskName());
        assertEquals(1L, tasks.get(1).getTaskAllConf().getPackConf().getId());
        assertEquals(100L, tasks.get(1).getTaskAllConf().getPackConf().getStart());
        assertEquals(199L, tasks.get(1).getTaskAllConf().getPackConf().getEnd());
        assertEquals(StepStateEnum.START, job.getSteps().get(1).getState());
        assertEquals(TaskStateEnum.UNDONE, job.getSteps().get(1).getTaskState(1L));

        tasks = taskBuilder.buildNewTasks(job);
        assertEquals(0, tasks.size());

        for (long i = 0; i < 6L; i++) {
            job.getSteps().get(1).addTaskState(i, TaskStateEnum.SUCCESS);
        }
        job.getSteps().get(1).refreshTaskStates();
        tasks = taskBuilder.buildNewTasks(job);
        assertEquals(5, tasks.size());
        assertEquals("calc.pack-6", tasks.get(0).getTaskName());
        assertEquals(6L, tasks.get(0).getTaskAllConf().getPackConf().getId());
        assertEquals(600L, tasks.get(0).getTaskAllConf().getPackConf().getStart());
        assertEquals(699L, tasks.get(0).getTaskAllConf().getPackConf().getEnd());
        assertEquals("calc.pack-10", tasks.get(4).getTaskName());
        assertEquals(10L, tasks.get(4).getTaskAllConf().getPackConf().getId());
        assertEquals(1000L, tasks.get(4).getTaskAllConf().getPackConf().getStart());
        assertEquals(1005L, tasks.get(4).getTaskAllConf().getPackConf().getEnd());
        assertEquals(StepStateEnum.READY, job.getSteps().get(1).getState());
        assertEquals(TaskStateEnum.SUCCESS, job.getSteps().get(1).getTaskState(3L));
        assertEquals(TaskStateEnum.UNDONE, job.getSteps().get(1).getTaskState(10L));
    }

    @Test
    public void buildIndependentTasks3() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest1);
        job.getSteps().get(0).setState(StepStateEnum.SUCCESS);
        job.getSteps().get(1).setState(StepStateEnum.SUCCESS);

        List<Task> tasks = taskBuilder.buildNewTasks(job);
        assertEquals(2, tasks.size());
        assertEquals("aggr-1", tasks.get(0).getTaskName());
        assertEquals("1", tasks.get(0).getParam("p1"));
        assertEquals("2", tasks.get(0).getParam("p2"));
        assertEquals("aggr-2", tasks.get(1).getTaskName());
        assertEquals("2", tasks.get(1).getParam("p1"));
        assertEquals("4", tasks.get(1).getParam("p2"));
        assertEquals(StepStateEnum.READY, job.getSteps().get(2).getState());
        assertEquals(TaskStateEnum.UNDONE, job.getSteps().get(2).getTaskState(JobStep.SIMPLE_TASK_INDEX));
        assertEquals(StepStateEnum.READY, job.getSteps().get(3).getState());
        assertEquals(TaskStateEnum.UNDONE, job.getSteps().get(3).getTaskState(JobStep.SIMPLE_TASK_INDEX));
    }

    // -----

    @Test
    public void buildShardTasks1() {
        Job job = new Job();
        JobStep step = new JobStep();
        StepShardConf conf = new StepShardConf();
        conf.setShard(10);
        step.getStepAllConf().setShardConf(conf);

        List<Task> tasks = taskBuilder.buildShardTasks(step, job, 3);
        assertEquals(3, tasks.size());
        assertEquals(0, tasks.get(0).getTaskAllConf().getShardConf().getIndex());
        assertEquals(1, tasks.get(1).getTaskAllConf().getShardConf().getIndex());
        assertEquals(2, tasks.get(2).getTaskAllConf().getShardConf().getIndex());
    }

    @Test
    public void buildShardTasks2() {
        Job job = new Job();
        JobStep step = new JobStep();
        StepShardConf conf = new StepShardConf();
        conf.setShard(10);
        step.getStepAllConf().setShardConf(conf);
        TaskShardCreation creation = new TaskShardCreation();
        creation.setLastShardIndex(7);
        step.getTaskAllCreation().setShardCreation(creation);

        List<Task> tasks = taskBuilder.buildShardTasks(step, job, 3);
        assertEquals(2, tasks.size());
        assertEquals(8, tasks.get(0).getTaskAllConf().getShardConf().getIndex());
        assertEquals(9, tasks.get(1).getTaskAllConf().getShardConf().getIndex());
    }

    @Test
    public void buildPackTasks1() {
        Job job = new Job();
        JobStep step = new JobStep();
        StepPackConf conf = new StepPackConf();
        conf.setSize(10L);
        conf.setStart(15L);
        conf.setEnd(45L);
        step.getStepAllConf().setPackConf(conf);

        List<Task> tasks = taskBuilder.buildPackTasks(step, job, 8);
        assertEquals(4, tasks.size());
        assertEquals(0L, tasks.get(0).getTaskAllConf().getPackConf().getId());
        assertEquals(15L, tasks.get(0).getTaskAllConf().getPackConf().getStart());
        assertEquals(24L, tasks.get(0).getTaskAllConf().getPackConf().getEnd());
        assertEquals(45L, tasks.get(3).getTaskAllConf().getPackConf().getStart());
        assertEquals(45L, tasks.get(3).getTaskAllConf().getPackConf().getEnd());
    }

    @Test
    public void buildPackTasks2() {
        Job job = new Job();
        JobStep step = new JobStep();
        StepPackConf conf = new StepPackConf();
        conf.setSize(10L);
        conf.setStart(15L);
        conf.setEnd(45L);
        step.getStepAllConf().setPackConf(conf);
        TaskPackCreation creation = new TaskPackCreation();
        creation.setLastPackId(2L);
        creation.setLastOffset(24L);
        step.getTaskAllCreation().setPackCreation(creation);

        List<Task> tasks = taskBuilder.buildPackTasks(step, job, 8);
        assertEquals(3, tasks.size());
        assertEquals(3L, tasks.get(0).getTaskAllConf().getPackConf().getId());
        assertEquals(25L, tasks.get(0).getTaskAllConf().getPackConf().getStart());
        assertEquals(34L, tasks.get(0).getTaskAllConf().getPackConf().getEnd());
        assertEquals(45L, tasks.get(2).getTaskAllConf().getPackConf().getStart());
        assertEquals(45L, tasks.get(2).getTaskAllConf().getPackConf().getEnd());
    }

    @Test
    public void buildPackTasks3() {
        Job job = new Job();
        JobStep step = new JobStep();
        StepPackConf conf = new StepPackConf();
        conf.setSize(10L);
        conf.setStart(46L);
        conf.setEnd(45L);
        step.getStepAllConf().setPackConf(conf);

        List<Task> tasks = taskBuilder.buildPackTasks(step, job, 8);
        assertEquals(0, tasks.size());
    }

    @Test
    public void buildPackTasksInfinite() {
        Job job = new Job();
        JobStep step = new JobStep();
        StepPackConf conf = new StepPackConf();
        conf.setSize(10L);
        conf.setStart(15L);
        conf.setInfinite(true);
        conf.setStartPackId(2L);
        conf.setMaxTaskCount(13);
        step.getStepAllConf().setPackConf(conf);

        List<Task> tasks = taskBuilder.buildPackTasks(step, job, 8);
        assertEquals(8, tasks.size());
        assertEquals(2L, tasks.get(0).getTaskAllConf().getPackConf().getId());
        assertEquals(15L, tasks.get(0).getTaskAllConf().getPackConf().getStart());
        assertEquals(24L, tasks.get(0).getTaskAllConf().getPackConf().getEnd());
        assertEquals(35L, tasks.get(2).getTaskAllConf().getPackConf().getStart());
        assertEquals(44L, tasks.get(2).getTaskAllConf().getPackConf().getEnd());

        tasks = taskBuilder.buildPackTasks(step, job, 8);
        assertEquals(5, tasks.size());

        tasks = taskBuilder.buildPackTasks(step, job, 8);
        assertEquals(0, tasks.size());
    }
}
