package com.ebay.magellan.tascreed.core.domain.validate;

import com.ebay.magellan.tascreed.core.domain.define.JobDefine;
import com.ebay.magellan.tascreed.core.domain.define.StepDefine;
import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.job.JobStep;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JobValidatorTest {

    JobValidator validator = new JobValidator();

    JobStep buildStep(String name) {
        JobStep step = new JobStep(new StepDefine());
        step.setStepName(name);
        return step;
    }
    List<JobStep> buildSteps(String... names) {
        List<JobStep> steps = new ArrayList<>();
        for (String name : names) {
            steps.add(buildStep(name));
        }
        return steps;
    }

    @Test
    public void validate1() {
        ValidateResult vr = validator.validate(null);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate2() {
        Job job = new Job();
        ValidateResult vr = validator.validate(job);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate3() {
        Job job = new Job(new JobDefine());
        job.setJobName("job");
        job.setTrigger("trigger");
        job.setSteps(buildSteps("s1", "s2", "s3", "s4"));

        ValidateResult vr = validator.validate(job);
        assertTrue(vr.isValid());
    }

    @Test
    public void validate4() {
        Job job = new Job(new JobDefine());
        job.setJobName("123");
        job.setTrigger("trigger");
        job.setSteps(buildSteps("s1", "s2", "s3", "s4"));

        ValidateResult vr = validator.validate(job);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate5() {
        Job job = new Job(new JobDefine());
        job.setJobName("job");
        job.setTrigger("-123");
        job.setSteps(buildSteps("s1", "s2", "s3", "s4"));

        ValidateResult vr = validator.validate(job);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate6() {
        Job job = new Job(new JobDefine());
        job.setJobName("job");
        job.setTrigger("123");
        job.setSteps(buildSteps());

        ValidateResult vr = validator.validate(job);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate7() {
        Job job = new Job();
        job.setJobName("job");
        job.setTrigger("23");
        job.setSteps(buildSteps("s1", "s2", "s3", "s4"));

        ValidateResult vr = validator.validate(job);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate8() {
        Job job = new Job(new JobDefine());
        job.setJobName("job");
        job.setTrigger("23");
        job.setSteps(buildSteps("s1", "s2", "s3", "s4"));
        JobStep ns = new JobStep();
        ns.setStepName("ns");
        job.getSteps().add(ns);

        ValidateResult vr = validator.validate(job);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate9() {
        Job job = new Job(new JobDefine());
        job.setJobName("job");
        job.setTrigger("trigger");
        job.setSteps(buildSteps("s1", "s2", "s3", "s4", "s2", "s3", "s2"));

        ValidateResult vr = validator.validate(job);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

}
