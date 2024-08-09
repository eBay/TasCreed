package com.ebay.magellan.tumbler.depend.common.cache.func;

import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;

@FunctionalInterface
public interface CheckedSupplier<T> {
    T get() throws TumblerException;
}
