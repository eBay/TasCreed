package com.ebay.magellan.tascreed.depend.common.retry;

import org.junit.Test;
import org.mockito.Mock;

public class RetryCounterFactoryTest {

    @Mock
    private RetryStrategy retryStrategy;

    @Test
    public void buildRetryCounterTest() {
        RetryCounter retryCounter = RetryCounterFactory.buildRetryCounter(retryStrategy);
        assert (retryCounter.getMaxCount() == 5);
    }

    @Test
    public void buildRetryCounter1Test() {
        RetryCounter retryCounter = RetryCounterFactory.buildRetryCounter(10, retryStrategy);
        assert (retryCounter.getMaxCount() == 10);
    }

    @Test
    public void buildInfiniteRetryCounter() {
        RetryCounter retryCounter = RetryCounterFactory.buildInfiniteRetryCounter(retryStrategy);
        assert (retryCounter.getMaxCount() == -1);
    }
}
