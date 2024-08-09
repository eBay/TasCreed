package com.ebay.magellan.tascreed.depend.common.util;

import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import org.junit.Test;

import java.util.Date;

public class DateUtilTest {

    @Test
    public void parseWithoutTimeZoneTest() throws TumblerException {
        Date date = DateUtil.parseWithoutTimeZone("2019-12-10T16:59:09.506Z");
        assert (date != null);
    }

    @Test
    public void formatWithoutTimeZoneTest(){
        Date date = new Date(System.currentTimeMillis());
        String dateStr = DateUtil.formatWithoutTimeZone(date);
        System.out.println(dateStr);
        assert (dateStr.contains("-"));
    }

    @Test
    public void parseWithoutTimeTest() throws TumblerException {
        Date date = DateUtil.parseWithoutTime("20191210");
        assert (date != null);
    }

    @Test
    public void formatWithoutTimeTest() {
        Date date = new Date(System.currentTimeMillis());
        String dateStr = DateUtil.formatWithoutTime(date);
        System.out.println(dateStr);
        assert (!dateStr.contains("-"));
    }
    @Test
    public void parseWithUTCTest() throws TumblerException {
        Date date = DateUtil.parseWithUTC("2019-12-10T16:59:09.506Z");
        assert (date != null);
    }
    @Test
    public void formatWithUTCTest() {
        Date date = new Date(System.currentTimeMillis());
        String dateStr = DateUtil.formatWithUTC(date);
        System.out.println(dateStr);
        assert (dateStr.contains("-"));
    }

    @Test
    public void parseCetDateTime() throws TumblerException {
        Date date = DateUtil.parseCetDateTime("2019-10-01");
        System.out.println(date);
        System.out.println(DateUtil.oneDayBefore(date));
        System.out.println(DateUtil.oneDayAfter(date));
    }

    @Test
    public void testReportFileDate() throws TumblerException {
        Date date = DateUtil.parseCetDateTime("2019-10-01");
        System.out.println(date);
        System.out.println(DateUtil.formatReportFileNameDate(date));
        System.out.println(DateUtil.formatReportFileMetaDate(date));
    }

    @Test
    public void testString2Date() throws TumblerException {
        Date date = DateUtil.string2Date("2020-01-02 22:30:00");
        System.out.println(date);
    }

    @Test
    public void testToUTCDateString() throws TumblerException {
        String utcTime = DateUtil.toUTCDateString("2020-01-02 22:30:00");
        System.out.println(utcTime);
        assert(utcTime.equals("2020-01-03T05:30:00.000Z"));
    }
}
