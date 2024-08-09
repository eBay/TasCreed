package com.ebay.magellan.tumbler.core.domain.validate;

import com.ebay.magellan.tumbler.core.domain.define.JobDefine;
import com.ebay.magellan.tumbler.core.domain.define.StepDefine;
import com.ebay.magellan.tumbler.core.domain.define.dep.Dependency;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StepGraphValidatorTest {

    StepGraphValidator validator = new StepGraphValidator();

    StepDefine buildStepDefine(String name, String... dependentSteps) {
        StepDefine sd = new StepDefine();
        sd.setStepName(name);
        if (dependentSteps != null) {
            Dependency d = new Dependency();
            d.setDoneSteps(new ArrayList<>());
            for (String ds : dependentSteps) {
                d.getDoneSteps().add(ds);
            }
            sd.setDependency(d);
        }
        return sd;
    }

    @Test
    public void validate1() {
        // s1 -> s2 -> s4, s1 -> s3 -> s4
        JobDefine jd = new JobDefine();
        jd.getSteps().add(buildStepDefine("s1"));
        jd.getSteps().add(buildStepDefine("s2", "s1"));
        jd.getSteps().add(buildStepDefine("s3", "s1"));
        jd.getSteps().add(buildStepDefine("s4", "s2", "s3"));
        jd.postBuild();

        ValidateResult vr = validator.validate(jd);
        assertTrue(vr.isValid());
    }

    @Test
    public void validate2() {
        // s1 -> s2 -> s4, s1 -> s3 -> s4, s4 -> s5 -> s2
        JobDefine jd = new JobDefine();
        jd.getSteps().add(buildStepDefine("s1"));
        jd.getSteps().add(buildStepDefine("s2", "s1", "s5"));
        jd.getSteps().add(buildStepDefine("s3", "s1"));
        jd.getSteps().add(buildStepDefine("s4", "s2", "s3"));
        jd.getSteps().add(buildStepDefine("s5", "s4"));
        jd.postBuild();

        ValidateResult vr = validator.validate(jd);
        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }
}
