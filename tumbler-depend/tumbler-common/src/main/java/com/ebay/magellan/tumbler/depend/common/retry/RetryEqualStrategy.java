package com.ebay.magellan.tumbler.depend.common.retry;

public class RetryEqualStrategy implements RetryStrategy {

    private long sleepSecond;

    public RetryEqualStrategy(long sleepSecond) {
        this.sleepSecond = sleepSecond;
    }

    public long getSleepMs(int count) {
        return getSleepSecond(count) * 1000L;
    }

    public long getSleepSecond(int count) {
        return sleepSecond;
    }

    public void waitForNextRetry(int count) {
        long ms = getSleepMs(count);
        if (ms > 0) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
