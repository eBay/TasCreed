package com.ebay.magellan.tascreed.depend.common.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PercentageUtilTest {

    private String percent(long numerator, long denominator) {
        return String.format("%.2f%%", PercentageUtil.convertPercentage(numerator, denominator));
    }

    @Test
    public void convertPercentage() {
        assertEquals("10.00%", percent(1, 10));
        assertEquals("20.00%", percent(2, 10));
        assertEquals("13.33%", percent(2, 15));
        assertEquals("100.00%", percent(20, 15));
        assertEquals("100.00%", percent(15, 15));
        assertEquals("0.99%", percent(1, 101));
        assertEquals("1.00%", percent(1, 100));
        assertEquals("0.00%", percent(-1, 100));
        assertEquals("0.00%", percent(0, 100));
        assertEquals("2.00%", percent(1, 50));
    }

}
