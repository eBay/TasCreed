package com.ebay.magellan.tascreed.depend.common.cache;

import com.ebay.magellan.tascreed.depend.common.cache.func.CheckedFunction;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;

import java.util.HashMap;
import java.util.Map;

public class CacheMap<K, V> {
    private Map<K, CacheValue<V>> map = new HashMap<>();

    private CheckedFunction<K, V> valueFunction;
    private long expireAfterTimeInMs;
    private boolean valueNullable;

    // -----

    private CacheMap(CheckedFunction<K, V> valueFunction, long expireAfterTimeInMs, boolean valueNullable) {
        this.valueFunction = valueFunction;
        this.expireAfterTimeInMs = expireAfterTimeInMs;
        this.valueNullable = valueNullable;
    }

    public static <K, V> CacheMap<K, V> build(CheckedFunction<K, V> valueFunction, long expireAfterTimeInMs) {
        return new CacheMap<>(valueFunction, expireAfterTimeInMs, false);
    }

    public static <K, V> CacheMap<K, V> build(CheckedFunction<K, V> valueFunction, long expireAfterTimeInMs, boolean valueNullable) {
        return new CacheMap<>(valueFunction, expireAfterTimeInMs, valueNullable);
    }

    // -----

    boolean setKeyValue(K key, V value) {
        if (key == null) return false;
        CacheValue<V> cv = map.get(key);
        if (cv == null) {
            cv = CacheValue.empty();
            map.put(key, cv);
        }

        long expireTime = expireAfterTimeInMs;
        if (!valueNullable && value == null) expireTime = 0L;
        cv.setValue(value, expireTime);
        return true;
    }

    synchronized boolean trySetKeyValue(K key, boolean forceRefresh) throws TumblerException {
        if (needSetKeyValue(key, forceRefresh)) {
            return setKeyValue(key, valueFunction.apply(key));
        }
        return false;
    }

    boolean needSetKeyValue(K key, boolean forceRefresh) {
        CacheValue<V> cv = map.get(key);
        return forceRefresh || cv == null || (cv != null && cv.needRefresh());
    }

    public CacheValueResp<V> getCacheValue(K key, boolean forceRefresh) throws TumblerException {
        if (key == null) return CacheValueResp.empty();
        boolean refreshed = false;
        if (needSetKeyValue(key, forceRefresh)) {
            refreshed = trySetKeyValue(key, forceRefresh);
        }
        CacheValue<V> cv = map.get(key);
        if (cv == null) return CacheValueResp.empty();
        return CacheValueResp.of(cv.getValue(), refreshed);
    }

    public CacheValueResp<V> getCacheValue(K key) throws TumblerException {
        return getCacheValue(key, false);
    }

    public void expire(K key) {
        if (key == null) return;
        synchronized (this) {
            CacheValue<V> cv = map.get(key);
            if (cv != null) {
                cv.expire();
            }
        }
    }

    // -----
}
