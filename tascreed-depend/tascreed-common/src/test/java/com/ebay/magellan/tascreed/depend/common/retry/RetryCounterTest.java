package com.ebay.magellan.tascreed.depend.common.retry;

import org.junit.Test;
import org.mockito.InjectMocks;

import static org.mockito.Mockito.mock;

public class RetryCounterTest {
    @InjectMocks
    private RetryStrategy retryStrategy;

    @Test
    public void RetryCounterTest() {
        RetryStrategy retryStrategy = mock(RetryStrategy.class);
        RetryCounter retryCounter = new RetryCounter(10,retryStrategy);
        assert (retryCounter.getMaxCount() == 10);
    }
    @Test
    public void forceStopTest() {
        RetryStrategy retryStrategy = mock(RetryStrategy.class);
        RetryCounter retryCounter = new RetryCounter(10,retryStrategy);
        retryCounter.forceStop();
        assert (retryCounter.isForceStopped());
    }
    @Test
    public void isAliveTest() {
        RetryStrategy retryStrategy = mock(RetryStrategy.class);
        RetryCounter retryCounter = new RetryCounter(10,retryStrategy);
        assert (retryCounter.isAlive());
    }
    @Test
    public void growTest() {
        RetryStrategy retryStrategy = mock(RetryStrategy.class);
        RetryCounter retryCounter = new RetryCounter(10,retryStrategy);
        retryCounter.grow();
        assert (retryCounter.getCount() == 1);
    }
    @Test
    public void grow1Test() {
        RetryStrategy retryStrategy = mock(RetryStrategy.class);
        RetryCounter retryCounter = new RetryCounter(10,retryStrategy);
        retryCounter.grow(2);
        assert (retryCounter.getCount() == 2);
    }
    @Test
    public void resetTest() {
        RetryStrategy retryStrategy = mock(RetryStrategy.class);
        RetryCounter retryCounter = new RetryCounter(10,retryStrategy);
        retryCounter.reset();
        assert (retryCounter.getCount() == 0);
    }
    @Test
    public void asFiniteTest() {
        RetryStrategy retryStrategy = mock(RetryStrategy.class);
        RetryCounter retryCounter = new RetryCounter(10,retryStrategy);
        retryCounter.asFinite(20);
        assert (retryCounter.getMaxCount() == 20);
    }
    @Test
    public void statusTest() {
        RetryStrategy retryStrategy = mock(RetryStrategy.class);
        RetryCounter retryCounter = new RetryCounter(10,retryStrategy);
        String ret = retryCounter.status();
        assert (ret.contains("will keep retrying"));
    }
    @Test
    public void status1Test() {
        RetryStrategy retryStrategy = mock(RetryStrategy.class);
        RetryCounter retryCounter = new RetryCounter(10,retryStrategy);
        String ret = retryCounter.status("start:");
        assert (ret.startsWith("start:"));
    }
    @Test
    public void waitForNextRetryTest() {

        RetryCounter retryCounter = new RetryCounter(10, retryStrategy);
        retryCounter.waitForNextRetry();
    }
}
