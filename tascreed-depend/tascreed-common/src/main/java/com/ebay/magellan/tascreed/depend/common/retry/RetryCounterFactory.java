package com.ebay.magellan.tascreed.depend.common.retry;

public class RetryCounterFactory {

    public static final int DEFAULT_FINITE_RETRY_TIMES = 5;

    public static RetryCounter buildRetryCounter(RetryStrategy strategy) {
        return new RetryCounter(DEFAULT_FINITE_RETRY_TIMES, strategy);
    }

    public static RetryCounter buildRetryCounter(int maxCount, RetryStrategy strategy) {
        return new RetryCounter(maxCount, strategy);
    }

    public static RetryCounter buildInfiniteRetryCounter(RetryStrategy strategy) {
        return buildRetryCounter(-1, strategy);
    }

}
