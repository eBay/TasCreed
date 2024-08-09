package com.ebay.magellan.tumbler.depend.common.cache;

import lombok.Getter;

@Getter
public class CacheValueResp<T> {
    private T value;
    private boolean refreshed;

    private CacheValueResp(T value, boolean refreshed) {
        this.value = value;
        this.refreshed = refreshed;
    }

    public static <T> CacheValueResp<T> of(T v, boolean refreshed) {
        return new CacheValueResp<>(v, refreshed);
    }

    public static <T> CacheValueResp<T> empty() {
        return new CacheValueResp<>(null, true);
    }

}
