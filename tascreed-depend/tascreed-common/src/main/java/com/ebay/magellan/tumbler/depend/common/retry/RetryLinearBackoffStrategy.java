package com.ebay.magellan.tumbler.depend.common.retry;

public class RetryLinearBackoffStrategy implements RetryStrategy {

    protected long minSleepSecond;
    protected long maxSleepSecond;

    protected long sleepSecondStep;

    protected int minCount = 1;
    protected int maxCount = 10;

    public RetryLinearBackoffStrategy(
            long minSleepSecond, long maxSleepSecond, long sleepSecondStep) {
        this.minSleepSecond = minSleepSecond;
        this.maxSleepSecond = maxSleepSecond;
        this.sleepSecondStep = sleepSecondStep;
    }

    public RetryLinearBackoffStrategy(
            long minSleepSecond, long maxSleepSecond, long sleepSecondStep,
            int minCount, int maxCount) {
        this.minSleepSecond = minSleepSecond;
        this.maxSleepSecond = maxSleepSecond;
        this.sleepSecondStep = sleepSecondStep;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    private long getMs(long second) {
        return second * 1000L;
    }

    public long getSleepMs(int count) {
        return getMs(getSleepSecond(count));
    }

    public long getSleepSecond(int count) {
        long sec = 0;
        if (count <= minCount) {
            sec = minSleepSecond;
        } else if (count >= maxCount) {
            sec = maxSleepSecond;
        } else {
            long sleepSec = minSleepSecond + sleepSecondStep * (count - minCount);
            sec = Math.min(Math.max(minSleepSecond, sleepSec), maxSleepSecond);
        }
        return sec;
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
