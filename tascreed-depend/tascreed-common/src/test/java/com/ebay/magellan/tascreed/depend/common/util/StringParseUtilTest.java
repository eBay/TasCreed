package com.ebay.magellan.tascreed.depend.common.util;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class StringParseUtilTest {
    @Test
    public void parseBooleanTest() {
        assertTrue (StringParseUtil.parseBoolean("true"));
        assertFalse(StringParseUtil.parseBoolean("false"));
    }
    @Test
    public void parseLong() {
        long num = StringParseUtil.parseLong("123");
        assert(num == 123L);
    }
    @Test
    public void parseLong1Test(){
        long num = StringParseUtil.parseLong("123", 0);
        assert (num == 123L);
        long num1 = StringParseUtil.parseLong("123.9", 0);
        assert(num1 == 0);
    }
    @Test
    public void parseIntegerTest() {
        int num = StringParseUtil.parseInteger("12");
        assert(num == 12);
    }
    @Test
    public void parseInteger1Test() {
        int num = StringParseUtil.parseInteger("12", 0);
        assert (num == 12);
        int num1 = StringParseUtil.parseInteger("12.9", 0);
        assert(num1 == 0);
    }
    @Test
    public void parseDoubleTest() {
        double num = StringParseUtil.parseDouble("12.9");
        assert(num == 12.9);
    }
    @Test
    public void parseDouble1Test() {
        double num = StringParseUtil.parseDouble("12.9", 0);
        assert (num == 12.9);
        double num1 = StringParseUtil.parseDouble("12.9e", 0);
        assert(num1 == 0);
    }

}
