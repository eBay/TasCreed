package com.ebay.magellan.tascreed.core.domain.validate;

import com.ebay.magellan.tascreed.core.domain.define.conf.StepPackConf;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StepPackConfValidatorTest {

    StepPackConfValidator validator = new StepPackConfValidator();

    @Test
    public void validate1() {
        ValidateResult vr = validator.validate(null);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate2() {
        StepPackConf conf = new StepPackConf();
        ValidateResult vr = validator.validate(conf);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void validate3() {
        StepPackConf conf = new StepPackConf();
        conf.setSize(100L);
        conf.setStart(0L);
        conf.setEnd(5043L);

        ValidateResult vr = validator.validate(conf);
        assertTrue(vr.isValid());
    }

    @Test
    public void validate4() {
        StepPackConf conf = new StepPackConf();
        conf.setSize(100L);
        conf.setStart(0L);
        conf.setInfinite(true);

        ValidateResult vr = validator.validate(conf);
        assertTrue(vr.isValid());
    }

    @Test
    public void validate5() {
        StepPackConf conf = new StepPackConf();
        conf.setSize(100L);
        conf.setStart(0L);
        conf.setEnd(5043L);
        conf.setStartPackId(-2L);

        ValidateResult vr = validator.validate(conf);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }
}
