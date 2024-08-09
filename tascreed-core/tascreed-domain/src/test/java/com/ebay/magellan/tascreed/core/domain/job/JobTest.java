package com.ebay.magellan.tascreed.core.domain.job;

import com.ebay.magellan.tascreed.core.domain.state.JobStateEnum;
import com.ebay.magellan.tascreed.core.domain.state.StepStateEnum;
import com.ebay.magellan.tascreed.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tascreed.core.domain.trait.Trait;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class JobTest {

    private JobStep genStep(String name) {
        JobStep step = new JobStep();
        step.setStepName(name);
        return step;
    }

    private Job genJob() {
        Job job = new Job();
        job.setJobName("job");
        List<JobStep> steps = new ArrayList<>();
        steps.add(genStep("s1"));
        steps.add(genStep("s2"));
        steps.add(genStep("s3"));
        job.setSteps(steps);
        return job;
    }

    @Test
    public void testUpdateJobStateByStepStates0() {
        Job job = genJob();
        job.setSteps(null);

        job.updateJobStateByStepStates();
        assertEquals(JobStateEnum.SUCCESS, job.getState());
    }
    @Test
    public void testUpdateJobStateByStepStates1() {
        Job job = genJob();
        job.findStepByName("s1").setState(StepStateEnum.SUCCESS);
        job.findStepByName("s2").setState(StepStateEnum.SUCCESS);
        job.findStepByName("s3").setState(StepStateEnum.SUCCESS);

        job.updateJobStateByStepStates();
        assertEquals(JobStateEnum.SUCCESS, job.getState());
    }
    @Test
    public void testUpdateJobStateByStepStates2() {
        Job job = genJob();
        job.findStepByName("s1").setState(StepStateEnum.SUCCESS);
        job.findStepByName("s2").setState(StepStateEnum.FAILED);
        job.findStepByName("s3").setState(StepStateEnum.SUCCESS);

        job.updateJobStateByStepStates();
        assertEquals(JobStateEnum.FAILED, job.getState());
    }
    @Test
    public void testUpdateJobStateByStepStates3() {
        Job job = genJob();
        job.findStepByName("s1").setState(StepStateEnum.SUCCESS);
        job.findStepByName("s2").setState(StepStateEnum.ACCEPTABLE_FAILED);
        job.findStepByName("s3").setState(StepStateEnum.SUCCESS);

        job.updateJobStateByStepStates();
        assertEquals(JobStateEnum.SUCCESS, job.getState());
    }
    @Test
    public void testUpdateJobStateByStepStates4() {
        Job job = genJob();
        job.findStepByName("s1").setState(StepStateEnum.SUCCESS);
        job.findStepByName("s2").setState(StepStateEnum.ERROR);
        job.findStepByName("s3").setState(StepStateEnum.SUCCESS);

        job.updateJobStateByStepStates();
        assertEquals(JobStateEnum.ERROR, job.getState());
    }
    @Test
    public void testUpdateJobStateByStepStates5() {
        Job job = genJob();
        job.findStepByName("s1").setState(StepStateEnum.SUCCESS);
        job.findStepByName("s2").setState(StepStateEnum.ERROR);
        job.findStepByName("s3").setState(StepStateEnum.SUCCESS);

        job.updateJobStateByStepStates();
        assertEquals(JobStateEnum.ERROR, job.getState());
    }

    // -----

    @Test
    public void testJson() throws Exception {
        Job job = genJob();
        job.addParam("k1", "v1");
        job.addUpdatedParam("k2", "v2");
        job.getTraits().trySetTrait(Trait.DELETED, true);

        String s = job.toJson();

        Job job1 = Job.fromJson(s);
        assertEquals(job.getJobName(), job1.getJobName());
        assertEquals("v1", job.getParams().get("k1"));
        assertEquals("v2", job.getUpdatedParams().get("k2"));
        assertTrue(job1.getTraits().containsTrait(Trait.DELETED));
    }

    @Test
    public void testUpdateProgression() {
        Job job = genJob();
        assertNull(job.getProgression());

        job.updateProgression();
        assertEquals("0.00%", job.getProgression().getValue());

        job.getSteps().get(0).setState(StepStateEnum.SUCCESS);
        job.updateProgression();
        assertEquals("33.33%", job.getProgression().getValue());

        job.getSteps().get(1).setState(StepStateEnum.SUCCESS);
        job.getSteps().get(2).setState(StepStateEnum.SUCCESS);
        job.updateProgression();
        assertEquals("100.00%", job.getProgression().getValue());
    }

    @Test
    public void testResetForRetry() {
        Job job = genJob();
        assertFalse(job.resetForRetry());

        job.setState(JobStateEnum.ERROR);
        assertTrue(job.resetForRetry());
        assertFalse(job.resetForRetry());

        job.getSteps().get(0).setState(StepStateEnum.ERROR);
        assertTrue(job.resetForRetry());
        assertFalse(job.resetForRetry());

        job.getSteps().get(0).setTaskStates(new HashMap<>());
        job.getSteps().get(0).getTaskStates().put(-1L, TaskStateEnum.ERROR);
        assertTrue(job.resetForRetry());
        assertFalse(job.resetForRetry());
    }

}
