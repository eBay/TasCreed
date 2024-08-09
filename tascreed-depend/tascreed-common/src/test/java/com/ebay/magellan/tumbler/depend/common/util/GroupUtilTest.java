package com.ebay.magellan.tumbler.depend.common.util;

import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GroupUtilTest {

    @Test
    public void groupListBySizePerGroup() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 104; i++) {
            list.add(String.valueOf(i));
        }

        List<List<String>> groups = GroupUtil.groupListBySizePerGroup(list, 10);
        assertEquals(11, groups.size());
        assertEquals(10, groups.get(1).size());
        assertEquals(4, groups.get(10).size());

        List<String> list1 = new ArrayList<>();
        List<List<String>> groups1 = GroupUtil.groupListBySizePerGroup(list1, 10);
        assertEquals(0, groups1.size());
    }

    @Test
    public void groupListByGroupCount() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 104; i++) {
            list.add(String.valueOf(i));
        }

        List<List<String>> groups = GroupUtil.groupListByGroupCount(list, 10);
        assertEquals(10, groups.size());
        assertEquals(11, groups.get(0).size());
        assertEquals(5, groups.get(9).size());
    }

    @Test
    public void splitDateRangeByCount() throws TumblerException {
        Date start = DateUtil.parseWithoutTime("20190101");
        Date end = DateUtil.parseWithoutTime("20190102");
        List<Pair<Date, Date>> list = GroupUtil.splitDateRangeByCount(start, end, 24);
        assertEquals(DateUtil.parseWithoutTimeZone("2019-01-01T02:00:00.000Z"), list.get(1).getRight());
        assertEquals(24, list.size());
    }

    @Test
    public void splitDateRangeByTimeInterval1() throws TumblerException {
        Date start = DateUtil.parseWithoutTime("20190101");
        Date end = DateUtil.parseWithoutTime("20190102");
        List<Pair<Date, Date>> list = GroupUtil.splitDateRangeByTimeInterval(start, end, 59 * GroupUtil.MINUTE);
        assertEquals(DateUtil.parseWithoutTimeZone("2019-01-01T00:59:00.000Z"), list.get(0).getRight());
        assertEquals(25, list.size());
    }


    @Test
    public void splitDateRangeByTimeInterval2() {
        Date start = new Date(2019 - 1900, 12 - 1, 9, 0, 0, 0);
        Date end = new Date(2019 - 1900, 12 - 1, 10, 0, 0, 0);
        List<Pair<Date, Date>> list = GroupUtil.splitDateRangeByTimeInterval(start, end, GroupUtil.HOUR);
        assertEquals(24, list.size());
    }

    @Test
    public void splitDateRangeByTimeInterval3() {
        Date start = new Date(2019 - 1900, 12 - 1, 9, 0, 0, 0);
        Date end = new Date(2019 - 1900, 12 - 1, 10, 0, 0, 1);
        List<Pair<Date, Date>> list = GroupUtil.splitDateRangeByTimeInterval(start, end, GroupUtil.HOUR);
        assertEquals(25, list.size());
    }

    @Test
    public void splitDateRangeByTimeInterval4() {
        Date start = new Date(2019 - 1900, 12 - 1, 9, 0, 0, 0);
        Date end = new Date(2019 - 1900, 12 - 1, 9, 23, 45, 12);
        List<Pair<Date, Date>> list = GroupUtil.splitDateRangeByTimeInterval(start, end, GroupUtil.HOUR);
        assertEquals(24, list.size());
    }

    @Test
    public void splitDateRangeByTimeInterval5() {
        Date start = new Date(2019 - 1900, 12 - 1, 9, 0, 0, 0);
        Date end = start;
        List<Pair<Date, Date>> list = GroupUtil.splitDateRangeByTimeInterval(start, end, GroupUtil.HOUR);
        assertEquals(0, list.size());
    }
}
