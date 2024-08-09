package com.ebay.magellan.tascreed.core.domain.validate;

import com.ebay.magellan.tascreed.core.domain.define.conf.StepShardConf;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StepShardConfValidatorTest {

    StepShardConfValidator validator = new StepShardConfValidator();

    @Test
    public void validate1() {
        ValidateResult vr = validator.validate(null);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate2() {
        StepShardConf conf = new StepShardConf();
        ValidateResult vr = validator.validate(conf);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate3() {
        StepShardConf conf = new StepShardConf();
        conf.setShard(12);

        ValidateResult vr = validator.validate(conf);
        assertTrue(vr.isValid());
    }

    @Test
    public void validate4() {
        StepShardConf conf = new StepShardConf();
        conf.setShard(12);
        conf.setStartShardId(-2);

        ValidateResult vr = validator.validate(conf);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }
}
