package com.ebay.magellan.tascreed.depend.common.cache;

import org.junit.Test;

import static org.junit.Assert.*;

public class CacheValueTest {

    @Test
    public void testCacheValue() {
        CacheValue<String> cv = CacheValue.empty();
        assertTrue(cv.notInit());
        assertFalse(cv.expired());
        assertTrue(cv.needRefresh());

        cv.setValue("a", 0L);
        assertFalse(cv.notInit());
        assertTrue(cv.expired());
        assertTrue(cv.needRefresh());
        assertEquals("a", cv.getValue());

        cv.setValue("b", -1L);
        assertFalse(cv.notInit());
        assertFalse(cv.expired());
        assertFalse(cv.needRefresh());
        assertEquals("b", cv.getValue());

        cv.setValue("c", 1000000L);
        assertFalse(cv.notInit());
        assertFalse(cv.expired());
        assertFalse(cv.needRefresh());
        assertEquals("c", cv.getValue());

        cv.expire();
        assertFalse(cv.notInit());
        assertTrue(cv.expired());
        assertTrue(cv.needRefresh());
    }
}
