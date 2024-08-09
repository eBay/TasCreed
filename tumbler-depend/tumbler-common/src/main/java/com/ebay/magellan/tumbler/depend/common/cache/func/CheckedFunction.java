package com.ebay.magellan.tumbler.depend.common.cache.func;

import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;

import java.util.Objects;

@FunctionalInterface
public interface CheckedFunction<T, R> {
    R apply(T t) throws TumblerException;

    default <V> CheckedFunction<V, R> compose(CheckedFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    default <V> CheckedFunction<T, V> andThen(CheckedFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }

    static <T> CheckedFunction<T, T> identity() {
        return t -> t;
    }
}
