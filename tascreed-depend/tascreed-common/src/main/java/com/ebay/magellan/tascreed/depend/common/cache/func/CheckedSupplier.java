package com.ebay.magellan.tascreed.depend.common.cache.func;

import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;

@FunctionalInterface
public interface CheckedSupplier<T> {
    T get() throws TumblerException;
}
