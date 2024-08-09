package com.ebay.magellan.tumbler.core.domain.builder;

import com.ebay.magellan.tumbler.core.domain.define.conf.StepPackConf;
import com.ebay.magellan.tumbler.core.domain.job.Job;
import com.ebay.magellan.tumbler.core.domain.job.JobStep;
import com.ebay.magellan.tumbler.core.domain.request.JobRequest;
import com.ebay.magellan.tumbler.core.domain.request.StepRequest;
import com.ebay.magellan.tumbler.core.domain.state.JobStateEnum;
import com.ebay.magellan.tumbler.core.domain.state.StepStateEnum;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JobUpdateBuilderTest {

    private JobUpdateBuilder jobUpdateBuilder = new JobUpdateBuilder();

    StepPackConf buildStepPackConf(long packSize) {
        StepPackConf pc = new StepPackConf();
        pc.setSize(packSize);
        return pc;
    }

    StepRequest buildStepRequest(String name) {
        StepRequest request = new StepRequest();
        request.setStepName(name);
        return request;
    }

    JobRequest buildJobRequest(String jobName) {
        JobRequest request = new JobRequest();
        request.setJobName(jobName);
        request.setSteps(new ArrayList<>());
        return request;
    }

    JobStep buildStep(String name) {
        JobStep step = new JobStep();
        step.setStepName(name);
        return step;
    }

    Job buildJob(String jobName) {
        Job job = new Job();
        job.setJobName(jobName);
        job.setSteps(new ArrayList<>());
        return job;
    }

    @Test
    public void updateStep1() {
        JobStep step = buildStep("s1");
        JobRequest jr = buildJobRequest("job");
        StepRequest sr = buildStepRequest("s1");
        jr.getSteps().add(sr);

        step.getStepAllConf().setPackConf(buildStepPackConf(10));
        sr.getStepAllConf().setPackConf(buildStepPackConf(20));

        boolean updated = jobUpdateBuilder.updateStep(step, jr);
        assertTrue(updated);
        assertEquals(Long.valueOf(20L), step.getStepAllConf().getPackConf().getSize());
    }

    @Test
    public void updateStep2() {
        JobStep step = buildStep("s1");
        JobRequest jr = buildJobRequest("job");
        StepRequest sr = buildStepRequest("s1");
        jr.getSteps().add(sr);

        step.getStepAllConf().setPackConf(buildStepPackConf(20));
        sr.getStepAllConf().setPackConf(buildStepPackConf(20));

        boolean updated = jobUpdateBuilder.updateStep(step, jr);
        assertFalse(updated);
        assertEquals(Long.valueOf(20L), step.getStepAllConf().getPackConf().getSize());
    }

    @Test
    public void updateStep3() {
        JobStep step = buildStep("s1");
        JobRequest jr = buildJobRequest("job");
        StepRequest sr = buildStepRequest("s2");
        jr.getSteps().add(sr);

        step.getStepAllConf().setPackConf(buildStepPackConf(10));
        sr.getStepAllConf().setPackConf(buildStepPackConf(20));

        boolean updated = jobUpdateBuilder.updateStep(step, jr);
        assertFalse(updated);
        assertEquals(Long.valueOf(10L), step.getStepAllConf().getPackConf().getSize());
    }

    @Test
    public void updateStep4() {
        JobStep step = buildStep("s1");
        JobRequest jr = buildJobRequest("job");
        StepRequest sr = buildStepRequest("s2");
        jr.getSteps().add(sr);

        step.getStepAllConf().setPackConf(buildStepPackConf(10));
        sr.getStepAllConf().setPackConf(buildStepPackConf(20));
        step.setState(StepStateEnum.ACCEPTABLE_FAILED);

        boolean updated = jobUpdateBuilder.updateStep(step, jr);
        assertFalse(updated);
        assertEquals(Long.valueOf(10L), step.getStepAllConf().getPackConf().getSize());
    }

    @Test
    public void updateJob1() {
        Job job = buildJob("job");
        JobStep step = buildStep("s1");
        job.getSteps().add(step);
        JobRequest jr = buildJobRequest("job");
        StepRequest sr = buildStepRequest("s1");
        jr.getSteps().add(sr);

        step.getStepAllConf().setPackConf(buildStepPackConf(10));
        sr.getStepAllConf().setPackConf(buildStepPackConf(20));

        boolean updated = jobUpdateBuilder.updateJob(job, jr);
        assertTrue(updated);
        assertEquals(Long.valueOf(20L), step.getStepAllConf().getPackConf().getSize());
    }

    @Test
    public void updateJob2() {
        Job job = buildJob("job");
        JobStep step = buildStep("s1");
        job.getSteps().add(step);
        JobRequest jr = buildJobRequest("job");
        StepRequest sr = buildStepRequest("s1");
        jr.getSteps().add(sr);

        step.getStepAllConf().setPackConf(buildStepPackConf(10));
        sr.getStepAllConf().setPackConf(buildStepPackConf(20));
        job.setState(JobStateEnum.SUCCESS);

        boolean updated = jobUpdateBuilder.updateJob(job, jr);
        assertFalse(updated);
        assertEquals(Long.valueOf(10L), step.getStepAllConf().getPackConf().getSize());
    }
}
