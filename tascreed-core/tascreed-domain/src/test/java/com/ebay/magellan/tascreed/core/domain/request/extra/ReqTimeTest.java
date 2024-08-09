package com.ebay.magellan.tascreed.core.domain.request.extra;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ReqTimeTest {

    @Test
    public void testGetTime1() {
        Date now = new Date();
        ReqTime reqTime = new ReqTime();
        reqTime.setTimeLong(now.getTime());

        System.out.println(now);
        System.out.println(now.getTime());

        assertEquals(now, reqTime.getTime());
    }

    @Test
    public void testGetTime2() {
        ReqTime reqTime = new ReqTime();
        reqTime.setTimeString("2021-10-12 00:53:20");
        reqTime.setTimePattern("yyyy-MM-dd HH:mm:ss");
        assertEquals(new Date(1634000000000L), reqTime.getTime());
    }
}
