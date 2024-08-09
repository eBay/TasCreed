package com.ebay.magellan.tascreed.depend.common.util;

import java.math.BigDecimal;
import java.math.MathContext;

public class BigDecimalUtil {

    private static final MathContext mc = MathContext.DECIMAL64;
    private static final int DEFAULT_SCALE = 8;

    // -----

    public static BigDecimal buildBigDecimal(String s) {
        return new BigDecimal(s, mc);
    }
    public static BigDecimal buildBigDecimal(double d) {
        return new BigDecimal(Double.toString(d), mc);
    }
    public static BigDecimal buildBigDecimal(long d) {
        return new BigDecimal(Long.toString(d), mc);
    }
    public static BigDecimal buildBigDecimal(int d) {
        return new BigDecimal(Integer.toString(d), mc);
    }

    // -----

    private static BigDecimal scale(BigDecimal a) {
        return scale(a, DEFAULT_SCALE);
    }
    private static BigDecimal scale(BigDecimal a, int scale) {
        return a.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
    }

    public static String parseString(BigDecimal a) {
        return scale(a).toString();
    }
    public static String parseString(BigDecimal a, int scale) {
        return scale(a, scale).toString();
    }
    public static double parseDouble(BigDecimal a) {
        return scale(a).doubleValue();
    }
    public static double parseDouble(BigDecimal a, int scale) {
        return scale(a, scale).doubleValue();
    }
    public static long parseLong(BigDecimal a) {
        return scale(a).longValue();
    }
    public static int parseInteger(BigDecimal a) {
        return scale(a).intValue();
    }

    // -----

    public static BigDecimal negate(BigDecimal a) {
        return a.negate();
    }
    public static BigDecimal abs(BigDecimal a) {
        return a.abs();
    }

    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b, mc);
    }
    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract(b, mc);
    }

}