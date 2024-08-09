package com.ebay.magellan.tascreed.depend.common.retry;

import org.junit.Test;

public class RetryEqualStrategyTest {
    @Test
    public void RetryEqualStrategyTest() {
        RetryEqualStrategy retryEqualStrategy = new RetryEqualStrategy(10);
        assert (retryEqualStrategy.getSleepSecond(1) == 10);
    }

    @Test
    public void getSleepMsTest(){
        RetryEqualStrategy retryEqualStrategy = new RetryEqualStrategy(10);
        long time = retryEqualStrategy.getSleepMs(10);
        assert (time == 10000);
    }
    @Test
    public void getSleepSecondTest() {
        RetryEqualStrategy retryEqualStrategy = new RetryEqualStrategy(10);
        long time = retryEqualStrategy.getSleepSecond(1);
        assert (time == 10);

    }
    @Test
    public void waitForNextRetryTest(){
        RetryEqualStrategy retryEqualStrategy = new RetryEqualStrategy(1);
        retryEqualStrategy.waitForNextRetry(0);
    }
}
