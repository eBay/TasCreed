package com.ebay.magellan.tumbler.core.infra.executor.progression;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TaskExecProgressionTest {

    @Test
    public void test() {
        TaskExecProgression progression = new TaskExecProgression();

        progression.init(100L);
        progression.setCurrent(10L);

        assertEquals("10.00%", progression.toValue().getValue());

        progression.finish();

        assertEquals("100.00%", progression.toValue().getValue());
    }
}
