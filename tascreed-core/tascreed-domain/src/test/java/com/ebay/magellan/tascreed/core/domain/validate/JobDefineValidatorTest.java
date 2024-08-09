package com.ebay.magellan.tascreed.core.domain.validate;

import com.ebay.magellan.tascreed.core.domain.define.JobDefine;
import com.ebay.magellan.tascreed.core.domain.define.StepDefine;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JobDefineValidatorTest {

    JobDefineValidator validator = new JobDefineValidator();

    @Test
    public void validate1() {
        ValidateResult vr = validator.validate(null);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate2() {
        JobDefine jd = new JobDefine();
        ValidateResult vr = validator.validate(jd);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate3() {
        JobDefine jd = new JobDefine();
        jd.setJobName("job");
        StepDefine sd = new StepDefine();
        sd.setStepName("step");
        jd.getSteps().add(sd);

        ValidateResult vr = validator.validate(jd);
        assertTrue(vr.isValid());
    }

    @Test
    public void validate4() {
        JobDefine jd = new JobDefine();
        jd.setJobName("123");

        ValidateResult vr = validator.validate(jd);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate5() {
        JobDefine jd = new JobDefine();
        jd.setJobName("job");

        ValidateResult vr = validator.validate(jd);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate6() {
        JobDefine jd = new JobDefine();
        jd.setJobName("job");
        StepDefine sd = new StepDefine();
        sd.setStepName("123");
        jd.getSteps().add(sd);

        ValidateResult vr = validator.validate(jd);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate7() {
        JobDefine jd = new JobDefine();
        jd.setJobName("job");
        StepDefine sd = new StepDefine();
        sd.setStepName("step");
        sd.setEffort(-1);
        jd.getSteps().add(sd);

        ValidateResult vr = validator.validate(jd);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate8() {
        JobDefine jd = new JobDefine();
        jd.setJobName("job");
        StepDefine sd1 = new StepDefine();
        sd1.setStepName("s1");
        jd.getSteps().add(sd1);
        StepDefine sd2 = new StepDefine();
        sd2.setStepName("s2");
        jd.getSteps().add(sd2);
        StepDefine sd3 = new StepDefine();
        sd3.setStepName("s1");
        jd.getSteps().add(sd3);

        ValidateResult vr = validator.validate(jd);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }
}
