package com.ebay.magellan.tumbler.depend.common.retry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetryCounter {
    private static final int INIT_COUNT = 0;

    private int maxCount;
    private int count = INIT_COUNT;

    private boolean forceStopped = false;

    private RetryStrategy strategy;

    RetryCounter(int maxCount, RetryStrategy strategy) {
        this.maxCount = maxCount;
        this.strategy = strategy;
    }

    public void forceStop() {
        forceStopped = true;
    }

    private boolean isInfinite() {
        return maxCount < 0;
    }

    public boolean isAlive() {
        if (isForceStopped()) {
            return false;
        } else {
            return isInfinite() || (count < maxCount);
        }
    }

    public void grow() {
        grow(1);
    }

    public void grow(int n) {
        count = count + n;
    }

    public void reset() {
        count = INIT_COUNT;
    }

    public RetryCounter asFinite(int maxCount) {
        setMaxCount(maxCount);
        return this;
    }

    public String status() {
        String head, tail;
        if (isForceStopped()) {
            head = String.format("[stopped] have tried %d times and force stopped", count);
        } else if (isInfinite()) {
            head = String.format("[infinitely] have tried %d times", count);
        } else {
            head = String.format("[total %d times] have tried %d times", maxCount, count);
        }
        if (isAlive()) {
            tail = "will keep retrying ...";
        } else {
            tail = "will not retry any more.";
        }
        return String.format("%s, %s", head, tail);
    }

    public String status(String head) {
        return String.format("%s, %s", head, status());
    }

    public void waitForNextRetry() {
        if (strategy != null) {
            strategy.waitForNextRetry(count);
        }
    }
}
