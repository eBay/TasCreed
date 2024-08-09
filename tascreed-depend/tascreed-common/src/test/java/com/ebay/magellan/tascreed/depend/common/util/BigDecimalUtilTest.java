package com.ebay.magellan.tascreed.depend.common.util;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class BigDecimalUtilTest {

    @Test
    public void testBuild() {
        BigDecimal a = BigDecimalUtil.buildBigDecimal("12.3");
        BigDecimal b = BigDecimalUtil.buildBigDecimal(12.3);
        assertEquals(a, b);

        BigDecimal c = BigDecimalUtil.buildBigDecimal(12);
        BigDecimal d = BigDecimalUtil.buildBigDecimal(12L);
        assertEquals(c, d);
    }

    @Test
    public void testParse() {
        BigDecimal a = BigDecimalUtil.buildBigDecimal(12.3);
        assertEquals("12.30", BigDecimalUtil.parseString(a, 2));
        assertEquals("12.30000000", BigDecimalUtil.parseString(a));

        assertEquals(12L, BigDecimalUtil.parseLong(a));
        assertEquals(12, BigDecimalUtil.parseInteger(a));

        BigDecimal b1 = BigDecimalUtil.buildBigDecimal(12.345);
        assertEquals("12.345", String.format("%.3f", BigDecimalUtil.parseDouble(b1)));
        assertEquals("12.340", String.format("%.3f", BigDecimalUtil.parseDouble(b1, 2)));
        assertEquals("12.34", String.format("%.2f", BigDecimalUtil.parseDouble(b1, 2)));

        BigDecimal b2 = BigDecimalUtil.buildBigDecimal(12.355);
        assertEquals("12.355", String.format("%.3f", BigDecimalUtil.parseDouble(b2)));
        assertEquals("12.360", String.format("%.3f", BigDecimalUtil.parseDouble(b2, 2)));
        assertEquals("12.36", String.format("%.2f", BigDecimalUtil.parseDouble(b2, 2)));
    }

    @Test
    public void testCalc() {
        BigDecimal a = BigDecimalUtil.buildBigDecimal(12.3);
        BigDecimal b = BigDecimalUtil.buildBigDecimal(24.6);

        BigDecimal x = BigDecimalUtil.add(a, b);
        BigDecimal y = BigDecimalUtil.subtract(a, b);

        assertEquals("36.90", BigDecimalUtil.parseString(x, 2));
        assertEquals("-12.30", BigDecimalUtil.parseString(y, 2));

        BigDecimal a1 = BigDecimalUtil.negate(a);
        BigDecimal a2 = BigDecimalUtil.abs(a1);

        assertEquals("-12.30", BigDecimalUtil.parseString(a1, 2));
        assertEquals("12.30", BigDecimalUtil.parseString(a2, 2));
    }

}