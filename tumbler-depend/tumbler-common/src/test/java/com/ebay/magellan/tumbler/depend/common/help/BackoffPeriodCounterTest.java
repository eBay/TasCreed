package com.ebay.magellan.tumbler.depend.common.help;

import org.junit.Test;

public class BackoffPeriodCounterTest {

    @Test
    public void newDefaultInstanceTest() {
        BackoffPeriodCounter backoffPeriodCounter = BackoffPeriodCounter.newDefaultInstance();
        backoffPeriodCounter.increase();
    }

    @Test
    public void getPeriodTest() {
        BackoffPeriodCounter backoffPeriodCounter = BackoffPeriodCounter.newDefaultInstance();
        long period = backoffPeriodCounter.getPeriod();
        assert (period == 0);
    }

    @Test
    public void increaseTest() {
        BackoffPeriodCounter backoffPeriodCounter = BackoffPeriodCounter.newDefaultInstance();
        backoffPeriodCounter.increase();
        assert (backoffPeriodCounter.getPeriod() == 0);
    }

    @Test
    public void resetTest() {
        BackoffPeriodCounter backoffPeriodCounter = BackoffPeriodCounter.newDefaultInstance();
        backoffPeriodCounter.reset();
        assert (backoffPeriodCounter.getPeriod() == 0);
    }

}
