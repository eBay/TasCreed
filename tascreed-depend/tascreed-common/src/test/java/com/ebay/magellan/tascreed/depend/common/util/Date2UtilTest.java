package com.ebay.magellan.tascreed.depend.common.util;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class Date2UtilTest {

    @Test
    public void parseDate_UTC() throws Exception {
        String s = "2020-06-01T15:00:00.000Z";
        Date d = Date2Util.parseDate(s);

        String s1 = Date2Util.formatDate(d);
        assertEquals(s, s1);
    }

    @Test
    public void parseDate1() throws Exception {
        String s = "2020-06-01 15:00:00";
        Date d = Date2Util.parseDate(s, Date2Util.DatePattern.TIME_CET);

        String s1 = Date2Util.formatDate(d);
        assertNotEquals(s, s1);
        assertEquals("2020-06-01T14:00:00.000Z", s1);

        String s2 = Date2Util.formatDate(d, Date2Util.DatePattern.TIME_CET);
        assertEquals(s, s2);
    }
}
