package com.ebay.magellan.tascreed.depend.common.exception;

public enum TumblerErrorTypeEnum {
    IGNORE,
    RETRY,
    NON_RETRY,
    FATAL;

    public boolean isIgnore() {
        return IGNORE == this;
    }

    public boolean isRetry() {
        return RETRY == this;
    }

    public boolean isNonRetry() {
        return NON_RETRY == this;
    }

    public boolean isFatal() {
        return FATAL == this;
    }
}
