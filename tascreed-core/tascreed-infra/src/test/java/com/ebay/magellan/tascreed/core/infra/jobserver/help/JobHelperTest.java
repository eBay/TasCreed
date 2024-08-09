package com.ebay.magellan.tascreed.core.infra.jobserver.help;

import com.ebay.magellan.tascreed.core.domain.builder.JobBuilder;
import com.ebay.magellan.tascreed.core.domain.builder.TaskBuilder;
import com.ebay.magellan.tascreed.core.domain.define.conf.StepAllConf;
import com.ebay.magellan.tascreed.core.domain.define.conf.StepPackConf;
import com.ebay.magellan.tascreed.core.domain.define.conf.StepShardConf;
import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.job.JobStep;
import com.ebay.magellan.tascreed.core.domain.state.StateChange;
import com.ebay.magellan.tascreed.core.domain.state.StepStateEnum;
import com.ebay.magellan.tascreed.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.domain.task.TaskResult;
import com.ebay.magellan.tascreed.core.infra.help.TestRepo;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.JobBulletin;
import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class JobHelperTest {

    @InjectMocks
    private JobHelper jobHelper = new JobHelper();

    @Mock
    private JobBulletin jobBulletin;

    @Mock
    private TumblerLogger logger;

    private JobBuilder jobBuilder = new JobBuilder();
    private TaskBuilder taskBuilder = new TaskBuilder();

    @Test
    public void updateJobStateWithDoneTasks1() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest);
        List<Task> tasks = taskBuilder.buildNewTasks(job);
        for (Task task : tasks) {
            task.setResult(new TaskResult(TaskStateEnum.SUCCESS, ""));
        }

        StateChange stateChange = StateChange.init();
        jobHelper.updateStatesInJob(stateChange, job, tasks, System.currentTimeMillis());
        assertTrue(stateChange.isTaskStateChanged());
        assertTrue(stateChange.isStepStateChanged());
        assertFalse(stateChange.isJobStateChanged());
    }

    @Test
    public void updateJobStateWithDoneTasks2() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest);
        List<Task> tasks = taskBuilder.buildNewTasks(job);
        for (Task task : tasks) {
            task.setResult(new TaskResult(TaskStateEnum.SUCCESS, ""));
        }
        tasks.get(0).setResult(new TaskResult(TaskStateEnum.UNDONE, ""));

        StateChange stateChange = StateChange.init();
        jobHelper.updateStatesInJob(stateChange, job, tasks, System.currentTimeMillis());
        assertTrue(stateChange.isTaskStateChanged());
        assertFalse(stateChange.isStepStateChanged());
        assertFalse(stateChange.isJobStateChanged());
    }

    @Test
    public void updateJobStateWithDoneTasks3() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest);
        List<Task> tasks = taskBuilder.buildNewTasks(job);
        for (Task task : tasks) {
            task.setResult(new TaskResult(TaskStateEnum.SUCCESS, ""));
        }
        tasks.get(0).setResult(new TaskResult(null, ""));

        StateChange stateChange = StateChange.init();
        jobHelper.updateStatesInJob(stateChange, job, tasks, System.currentTimeMillis());
        assertTrue(stateChange.isTaskStateChanged());
        assertFalse(stateChange.isStepStateChanged());
        assertFalse(stateChange.isJobStateChanged());
    }

    @Test
    public void updateJobStateWithDoneTasks4() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest);
        for (int i = 0; i < job.getSteps().size() - 1; i++) {
            JobStep step = job.getSteps().get(i);
            step.setState(StepStateEnum.SUCCESS);
        }
        List<Task> tasks = taskBuilder.buildNewTasks(job);
        for (Task task : tasks) {
            task.setResult(new TaskResult(TaskStateEnum.SUCCESS, ""));
        }

        StateChange stateChange = StateChange.init();
        jobHelper.updateStatesInJob(stateChange, job, tasks, System.currentTimeMillis());
        assertTrue(stateChange.isTaskStateChanged());
        assertTrue(stateChange.isStepStateChanged());
        assertTrue(stateChange.isJobStateChanged());
    }

    @Test
    public void assembleJobUpdatedParams() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest);
        job.addUpdatedParam("k2", "v1");
        Map<String, String> params = new HashMap<>();
        params.put("k1", "v1");
        params.put("k2", "v2");
        params.put("k3", null);

        jobHelper.assembleJobUpdatedParams(job, params);
        assertEquals(3, job.getUpdatedParams().size());
        assertEquals("v1", job.getUpdatedParams().get("k1"));
        assertEquals("v2", job.getUpdatedParams().get("k2"));
        assertNull(job.getUpdatedParams().get("k3"));
    }

    @Test
    public void assembleJobStepUpdatedConfigs1() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest);
        Map<String, StepAllConf> confs = new HashMap<>();
        StepAllConf conf = new StepAllConf();
        StepShardConf shardConf = new StepShardConf();
        shardConf.setShard(10);
        shardConf.setStartShardId(4);
        conf.setShardConf(shardConf);
        confs.put("prep", conf);

        jobHelper.assembleJobStepUpdatedConfigs(job, confs);
        assertEquals(10, job.getSteps().get(0).getStepAllConf().getShardConf().getShard().intValue());
        assertEquals(4, job.getSteps().get(0).getStepAllConf().getShardConf().getStartShardId().intValue());
        assertNull(job.getSteps().get(0).getStepAllConf().getShardConf().getMaxTaskCount());
    }

    @Test
    public void assembleJobStepUpdatedConfigs2() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest);
        Map<String, StepAllConf> confs = new HashMap<>();
        StepAllConf conf = new StepAllConf();
        StepPackConf packConf = new StepPackConf();
        packConf.setInfinite(true);
        packConf.setMaxTaskCount(3);
        packConf.setStart(10L);
        conf.setPackConf(packConf);
        confs.put("calc", conf);

        jobHelper.assembleJobStepUpdatedConfigs(job, confs);
        assertEquals(10L, job.getSteps().get(1).getStepAllConf().getPackConf().getStart().longValue());
        assertEquals(1005L, job.getSteps().get(1).getStepAllConf().getPackConf().getEnd().intValue());
        assertEquals(100L, job.getSteps().get(1).getStepAllConf().getPackConf().getSize().intValue());
        assertEquals(true, job.getSteps().get(1).getStepAllConf().getPackConf().getInfinite().booleanValue());
        assertEquals(3, job.getSteps().get(1).getStepAllConf().getPackConf().getMaxTaskCount().intValue());
        assertNull(job.getSteps().get(1).getStepAllConf().getPackConf().getStartPackId());
    }

    @Test
    public void assembleJobStepUpdatedConfigs3() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest);
        Map<String, StepAllConf> confs = new HashMap<>();
        StepAllConf conf = new StepAllConf();
        StepPackConf packConf = new StepPackConf();
        packConf.setInfinite(true);
        packConf.setMaxTaskCount(3);
        packConf.setStart(10L);
        conf.setPackConf(packConf);
        confs.put("calc", conf);
        job.getSteps().get(1).setState(StepStateEnum.START);

        jobHelper.assembleJobStepUpdatedConfigs(job, confs);
        assertEquals(0L, job.getSteps().get(1).getStepAllConf().getPackConf().getStart().longValue());
        assertEquals(1005L, job.getSteps().get(1).getStepAllConf().getPackConf().getEnd().intValue());
        assertEquals(100L, job.getSteps().get(1).getStepAllConf().getPackConf().getSize().intValue());
        assertNull(job.getSteps().get(1).getStepAllConf().getPackConf().getInfinite());
        assertEquals(6, job.getSteps().get(1).getStepAllConf().getPackConf().getMaxTaskCount().intValue());
        assertNull(job.getSteps().get(1).getStepAllConf().getPackConf().getStartPackId());
    }

    @Test
    public void assembleJobStepUpdatedConfigs4() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest);
        Map<String, StepAllConf> confs = new HashMap<>();
        confs.put("calc", null);

        jobHelper.assembleJobStepUpdatedConfigs(job, confs);
        assertEquals(0L, job.getSteps().get(1).getStepAllConf().getPackConf().getStart().longValue());
        assertEquals(1005L, job.getSteps().get(1).getStepAllConf().getPackConf().getEnd().intValue());
        assertEquals(100L, job.getSteps().get(1).getStepAllConf().getPackConf().getSize().intValue());
        assertNull(job.getSteps().get(1).getStepAllConf().getPackConf().getInfinite());
        assertEquals(6, job.getSteps().get(1).getStepAllConf().getPackConf().getMaxTaskCount().intValue());
        assertNull(job.getSteps().get(1).getStepAllConf().getPackConf().getStartPackId());
    }
}
