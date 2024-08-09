package com.ebay.magellan.tumbler.depend.common.util;

import java.util.function.Function;

public class DefaultValueUtil {

    public static boolean booleanValue(Boolean b) {
        return booleanValue(b, false);
    }
    public static boolean booleanValue(Boolean b, boolean d) {
        return b != null ? b : d;
    }

    public static int intValue(Integer i) {
        return intValue(i, 0);
    }
    public static int intValue(Integer i, int d) {
        return i != null ? i : d;
    }

    public static long longValue(Long l) {
        return longValue(l, 0L);
    }
    public static long longValue(Long l, long d) {
        return l != null ? l : d;
    }

    // -----

    public static <T> T defValue(T... vs) {
        for (T v : vs) {
            if (v != null) return v;
        }
        return null;
    }

    public static <T, U> U defValue(Function<T, U> func, T... vs) {
        for (T v : vs) {
            if (v != null) return func.apply(v);
        }
        return null;
    }

    // -----

}
