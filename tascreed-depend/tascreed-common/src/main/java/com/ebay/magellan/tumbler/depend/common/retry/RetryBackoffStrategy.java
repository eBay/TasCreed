package com.ebay.magellan.tumbler.depend.common.retry;

public class RetryBackoffStrategy implements RetryStrategy {

    protected long minSleepSecond;
    protected long maxSleepSecond;

    protected int minCount = 1;
    protected int maxCount = 10;

    public RetryBackoffStrategy(long minSleepSecond, long maxSleepSecond) {
        this.minSleepSecond = minSleepSecond;
        this.maxSleepSecond = maxSleepSecond;
    }

    public RetryBackoffStrategy(long minSleepSecond, long maxSleepSecond, int minCount, int maxCount) {
        this.minSleepSecond = minSleepSecond;
        this.maxSleepSecond = maxSleepSecond;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    public static RetryBackoffStrategy newDefaultInstance() {
        return new RetryBackoffStrategy(1, 10, 1, 5);
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
            sec = Math.min(minSleepSecond * (long) Math.pow(2, count - minCount), maxSleepSecond);
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
