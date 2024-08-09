package com.ebay.magellan.tumbler.core.domain.state.partial.step;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PackDoneRangeTest {

    @Test
    public void doneOffsetRangesStr() {
        String s = "[1,5];[7,12];[34,350]";
        PackDoneRange packDoneRange = new PackDoneRange();
        packDoneRange.setOffsetRangesStr(s);
        String s1 = packDoneRange.getOffsetRangesStr();
        assertEquals(s, s1);
    }

    @Test
    public void addOffsetRange() {
        String s = "[1,5];[7,12];[34,350]";
        PackDoneRange packDoneRange = new PackDoneRange();
        packDoneRange.setOffsetRangesStr(s);
        packDoneRange.addDoneOffsetRange(6, 6);
        packDoneRange.addDoneOffsetRange(13, 20);
        String s1 = packDoneRange.getOffsetRangesStr();
        assertEquals("[1,20];[34,350]", s1);
    }
}
