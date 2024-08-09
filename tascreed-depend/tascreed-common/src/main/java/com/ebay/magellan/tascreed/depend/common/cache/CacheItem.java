package com.ebay.magellan.tascreed.depend.common.cache;

import com.ebay.magellan.tascreed.depend.common.cache.func.CheckedSupplier;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;

public class CacheItem<T> {
    private CacheValue<T> cv = CacheValue.empty();

    private CheckedSupplier<T> valueSupplier;
    private long expireAfterTimeInMs;
    private boolean valueNullable;

    // -----

    private CacheItem(CheckedSupplier<T> valueSupplier, long expireAfterTimeInMs, boolean valueNullable) {
        this.valueSupplier = valueSupplier;
        this.expireAfterTimeInMs = expireAfterTimeInMs;
        this.valueNullable = valueNullable;
    }

    public static <T> CacheItem<T> build(CheckedSupplier<T> itemSupplier, long expireAfterTimeInMs) {
        return new CacheItem<>(itemSupplier, expireAfterTimeInMs, false);
    }

    public static <T> CacheItem<T> build(CheckedSupplier<T> itemSupplier, long expireAfterTimeInMs, boolean valueNullable) {
        return new CacheItem<>(itemSupplier, expireAfterTimeInMs, valueNullable);
    }

    // -----

    boolean setValue(T value) {
        long expireTime = expireAfterTimeInMs;
        if (!valueNullable && value == null) expireTime = 0L;
        this.cv.setValue(value, expireTime);
        return true;
    }

    synchronized boolean trySetValue(boolean forceRefresh) throws TcException {
        if (needSetValue(forceRefresh)) {
            return setValue(valueSupplier.get());
        }
        return false;
    }

    boolean needSetValue(boolean forceRefresh) {
        return forceRefresh || cv.needRefresh();
    }

    public CacheValueResp<T> getCacheValue(boolean forceRefresh) throws TcException {
        boolean refreshed = false;
        if (needSetValue(forceRefresh)) {
            refreshed = trySetValue(forceRefresh);
        }
        return CacheValueResp.of(cv.getValue(), refreshed);
    }

    public CacheValueResp<T> getCacheValue() throws TcException {
        return getCacheValue(false);
    }

    public void expire() {
        this.cv.expire();
    }

    // -----
}
