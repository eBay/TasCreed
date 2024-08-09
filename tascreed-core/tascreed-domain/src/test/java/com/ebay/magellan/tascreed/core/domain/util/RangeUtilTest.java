package com.ebay.magellan.tascreed.core.domain.util;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RangeUtilTest {

    @Test
    public void rangeSetFromString() {
        String s = "[1,5];[7,12];[34,350]";
        RangeSet<Long> rs = RangeUtil.rangeSetFromString(s);
        assertEquals(3, rs.asRanges().size());
        System.out.println(rs);
    }

    @Test
    public void rangeSetToString() {
        RangeSet<Long> rs = TreeRangeSet.create();
        RangeUtil.addRange(rs, Range.closed(1L, 3L));
        RangeUtil.addRange(rs, Range.closed(4L, 5L));
        RangeUtil.addRange(rs, Range.closed(7L, 10L));
        RangeUtil.addRange(rs, Range.closed(9L, 12L));
        RangeUtil.addRange(rs, Range.closed(34L, 350L));

        String s = RangeUtil.rangeSetToString(rs);
        assertEquals("[1,5];[7,12];[34,350]", s);
    }

    @Test
    public void findWatermark1() {
        Long startPoint = 1L;

        RangeSet<Long> rs = TreeRangeSet.create();
        RangeUtil.addRange(rs, Range.closed(1L, 3L));
        RangeUtil.addRange(rs, Range.closed(4L, 5L));
        RangeUtil.addRange(rs, Range.closed(7L, 10L));
        RangeUtil.addRange(rs, Range.closed(9L, 12L));
        RangeUtil.addRange(rs, Range.closed(34L, 350L));
        assertEquals(Long.valueOf(5L), RangeUtil.findWatermark(rs, startPoint));

        RangeUtil.addRange(rs, Range.closed(4L, 6L));
        assertEquals(Long.valueOf(12L), RangeUtil.findWatermark(rs, startPoint));

        RangeUtil.addRange(rs, Range.closed(13L, 36L));
        assertEquals(Long.valueOf(350L), RangeUtil.findWatermark(rs, startPoint));

        RangeUtil.addRange(rs, Range.closed(700L, 1000L));
        assertEquals(Long.valueOf(350L), RangeUtil.findWatermark(rs, startPoint));

        RangeUtil.addRange(rs, Range.closed(100L, 500L));
        assertEquals(Long.valueOf(500L), RangeUtil.findWatermark(rs, startPoint));
    }

    @Test
    public void findWatermark2() {
        Long startPoint = 1L;

        RangeSet<Long> rs = TreeRangeSet.create();
        RangeUtil.addRange(rs, Range.closed(10L, 19L));
        RangeUtil.addRange(rs, Range.closed(40L, 49L));
        RangeUtil.addRange(rs, Range.closed(70L, 79L));
        assertEquals(Long.valueOf(0L), RangeUtil.findWatermark(rs, startPoint));
        assertEquals(Long.valueOf(19L), RangeUtil.findWatermark(rs, null));

        RangeUtil.addRange(rs, Range.closed(20L, 39L));
        assertEquals(Long.valueOf(0L), RangeUtil.findWatermark(rs, startPoint));
        assertEquals(Long.valueOf(49L), RangeUtil.findWatermark(rs, null));

        RangeUtil.addRange(rs, Range.closed(50L, 69L));
        assertEquals(Long.valueOf(0L), RangeUtil.findWatermark(rs, startPoint));
        assertEquals(Long.valueOf(79L), RangeUtil.findWatermark(rs, null));

        RangeUtil.addRange(rs, Range.closed(1L, 5L));
        assertEquals(Long.valueOf(5L), RangeUtil.findWatermark(rs, startPoint));
        assertEquals(Long.valueOf(5L), RangeUtil.findWatermark(rs, null));

        RangeUtil.addRange(rs, Range.closed(6L, 9L));
        assertEquals(Long.valueOf(79L), RangeUtil.findWatermark(rs, startPoint));
        assertEquals(Long.valueOf(79L), RangeUtil.findWatermark(rs, null));
    }

}
