package com.ebay.magellan.tumbler.depend.common.help;

public class BackoffPeriodCounter {

    private long beginPeriod;
    private long endPeriod;
    private int beginCount;
    private int endCount;

    private int counter;

    private long step;

    public static BackoffPeriodCounter newDefaultInstance() {
        return new BackoffPeriodCounter(0, 5000, 3, 13);
    }

    public BackoffPeriodCounter(long beginPeriod, long endPeriod, int beginCount, int endCount) {
        this.beginPeriod = beginPeriod;
        this.endPeriod = endPeriod;
        this.beginCount = beginCount;
        this.endCount = endCount;
        init();
    }

    private void init() {
        counter = 0;
        if (endCount > beginCount) {
            step = (endPeriod - beginPeriod) / (endCount - beginCount);
        }
    }

    public long getPeriod() {
        if (counter <= beginCount) {
            return beginPeriod;
        } else if (counter >= endCount) {
            return endPeriod;
        } else {
            return beginPeriod + step * (counter - beginCount);
        }
    }

    public void increase() {
        if (counter < endCount) {
            counter++;
        }
    }

    public void reset() {
        counter = 0;
    }

}
