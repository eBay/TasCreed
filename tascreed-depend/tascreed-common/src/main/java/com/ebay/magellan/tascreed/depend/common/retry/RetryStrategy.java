package com.ebay.magellan.tascreed.depend.common.retry;

public interface RetryStrategy {
    long getSleepMs(int count);
    long getSleepSecond(int count);

    void waitForNextRetry(int count);
}
