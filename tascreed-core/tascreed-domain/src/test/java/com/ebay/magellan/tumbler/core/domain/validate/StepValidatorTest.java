package com.ebay.magellan.tumbler.core.domain.validate;

import com.ebay.magellan.tumbler.core.domain.define.StepDefine;
import com.ebay.magellan.tumbler.core.domain.define.StepTypeEnum;
import com.ebay.magellan.tumbler.core.domain.define.conf.StepShardConf;
import com.ebay.magellan.tumbler.core.domain.job.JobStep;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StepValidatorTest {

    StepValidator validator = new StepValidator();

    @Test
    public void validate1() {
        ValidateResult vr = validator.validate(null);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate2() {
        JobStep step = new JobStep();
        ValidateResult vr = validator.validate(step);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate3() {
        JobStep step = new JobStep(new StepDefine());
        step.setStepName("step");

        ValidateResult vr = validator.validate(step);
        assertTrue(vr.isValid());
    }

    @Test
    public void validate4() {
        JobStep step = new JobStep(new StepDefine());
        step.setStepName("step");
        step.getStepDefine().setStepType(StepTypeEnum.SHARD);
        step.getStepDefine().getStepAllConf().setShardConf(new StepShardConf());

        ValidateResult vr = validator.validate(step);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate5() {
        JobStep step = new JobStep(new StepDefine());
        step.setStepName("step");
        step.getStepDefine().setStepType(StepTypeEnum.SHARD);
        step.getStepAllConf().setShardConf(new StepShardConf());

        ValidateResult vr = validator.validate(step);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate6() {
        JobStep step = new JobStep(new StepDefine());
        step.setStepName("step");
        step.getStepDefine().setStepType(StepTypeEnum.SHARD);
        StepShardConf conf = new StepShardConf();
        conf.setShard(10);
        step.getStepAllConf().setShardConf(conf);

        ValidateResult vr = validator.validate(step);
        assertTrue(vr.isValid());
    }

}
