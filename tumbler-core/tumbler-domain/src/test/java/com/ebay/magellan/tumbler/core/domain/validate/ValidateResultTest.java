package com.ebay.magellan.tumbler.core.domain.validate;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidateResultTest {

    @Test
    public void addMsg() {
        ValidateResult vr = ValidateResult.init("title");
        vr.addMsg("msg1");
        vr.addMsg("msg2");

        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }

    @Test
    public void addChild() {
        ValidateResult vr = ValidateResult.init("title");
        vr.addChild(ValidateResult.init("child1"));
        vr.addChild(ValidateResult.init("child2"));

        assertTrue(vr.isValid());

        ValidateResult vr1 = ValidateResult.init("child3");
        vr1.addMsg("msg");
        vr.addChild(vr1);

        vr.addMsg("msg1");
        vr.addMsg("msg2");

        assertFalse(vr.isValid());
        System.out.println(vr.showMsg());
    }
}
