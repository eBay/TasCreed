package com.ebay.magellan.tascreed.core.domain.validate;

import com.ebay.magellan.tascreed.core.domain.define.StepDefine;
import com.ebay.magellan.tascreed.core.domain.define.StepTypeEnum;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StepDefineValidatorTest {

    StepDefineValidator validator = new StepDefineValidator();

    @Test
    public void validate1() {
        ValidateResult vr = validator.validate(null);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate2() {
        StepDefine sd = new StepDefine();
        ValidateResult vr = validator.validate(sd);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate3() {
        StepDefine sd = new StepDefine();
        sd.setStepName("step");

        ValidateResult vr = validator.validate(sd);
        assertTrue(vr.isValid());
    }

    @Test
    public void validate4() {
        StepDefine sd = new StepDefine();
        sd.setStepName("123");

        ValidateResult vr = validator.validate(sd);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate5() {
        StepDefine sd = new StepDefine();
        sd.setStepName("step");
        sd.setEffort(-10);

        ValidateResult vr = validator.validate(sd);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate6() {
        StepDefine sd = new StepDefine();
        sd.setStepName("step");
        sd.setStepType(StepTypeEnum.PACK);

        ValidateResult vr = validator.validate(sd);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

}
