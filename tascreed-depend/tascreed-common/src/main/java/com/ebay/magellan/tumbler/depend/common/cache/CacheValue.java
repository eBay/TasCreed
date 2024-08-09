package com.ebay.magellan.tumbler.depend.common.cache;

import lombok.Getter;

public class CacheValue<T> {
    @Getter
    private volatile T value;

    private volatile long lastUpdateTime = -1L;
    private volatile long expireTime = -1L;

    public static <T> CacheValue<T> empty() {
        return new CacheValue<>();
    }

    // -----

    boolean notInit() {
        return lastUpdateTime < 0;
    }

    boolean expired() {
        if (expireTime >= 0) {
            long now = System.currentTimeMillis();
            return now >= expireTime;
        }
        return false;
    }

    boolean needRefresh() {
        return notInit() || expired();
    }

    void setValue(T value, long expireAfterTimeInMs) {
        this.value = value;
        long now = System.currentTimeMillis();
        this.lastUpdateTime = now;
        if (expireAfterTimeInMs >= 0) {
            this.expireTime = now + expireAfterTimeInMs;
        } else {
            this.expireTime = -1L;
        }
    }

    void expire() {
        expireTime = 0L;
    }

}
