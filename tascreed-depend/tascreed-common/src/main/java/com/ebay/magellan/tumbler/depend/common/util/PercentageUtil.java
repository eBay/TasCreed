package com.ebay.magellan.tumbler.depend.common.util;

public class PercentageUtil {

    /**
     * convert numerator / denominator to a percentage value
     * @param numerator numerator
     * @param denominator denominator
     * @return percentage value = 100.0 * numerator / denominator
     */
    public static double convertPercentage(long numerator, long denominator) {
        long d = Math.max(denominator, 1L);
        long n = Math.max(Math.min(numerator, d), 0L);
        return (n * 100.0) / d;
    }

}
