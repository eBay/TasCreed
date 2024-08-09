package com.ebay.magellan.tumbler.depend.common.retry;

import org.junit.Test;

public class RetryBackoffStrategyTest {

    @Test
    public void RetryBackoffStrategyTest(){
        RetryBackoffStrategy retryBackoffStrategy = new RetryBackoffStrategy(1,10);
        assert (retryBackoffStrategy.maxSleepSecond == 10);
    }
    @Test
    public void RetryBackoffStrategy1Test() {
        RetryBackoffStrategy retryBackoffStrategy =  new RetryBackoffStrategy(1, 10, 2, 20);
        assert (retryBackoffStrategy.maxCount == 20);
    }
    @Test
    public void newDefaultInstanceTest() {
        RetryBackoffStrategy retryBackoffStrategy =  RetryBackoffStrategy.newDefaultInstance();
        assert (retryBackoffStrategy.maxSleepSecond == 10);
        assert (retryBackoffStrategy.maxCount == 5);
    }

    @Test
    public void getSleepMsTest() {
        RetryBackoffStrategy retryBackoffStrategy =  RetryBackoffStrategy.newDefaultInstance();
        long time = retryBackoffStrategy.getSleepMs(3);
        assert (time == 4000);
    }

    @Test
    public void getSleepSecondTest() {
        RetryBackoffStrategy retryBackoffStrategy =  RetryBackoffStrategy.newDefaultInstance();
        long time = retryBackoffStrategy.getSleepSecond(3);
        assert (time == 4);
    }
    @Test
    public void waitForNextRetryTest() {
        RetryBackoffStrategy retryBackoffStrategy =  RetryBackoffStrategy.newDefaultInstance();
        retryBackoffStrategy.waitForNextRetry(1);
    }
}
