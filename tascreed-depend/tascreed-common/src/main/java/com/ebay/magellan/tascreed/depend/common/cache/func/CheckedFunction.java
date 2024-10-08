package com.ebay.magellan.tascreed.depend.common.cache.func;

import com.ebay.magellan.tascreed.depend.common.exception.TcException;

import java.util.Objects;

@FunctionalInterface
public interface CheckedFunction<T, R> {
    R apply(T t) throws TcException;

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
