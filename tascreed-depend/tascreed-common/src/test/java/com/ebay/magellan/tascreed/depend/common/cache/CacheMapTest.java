package com.ebay.magellan.tascreed.depend.common.cache;

import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CacheMapTest {

    private String getValue(String key) {
        return String.format("value[%s]", key);
    }

    @Test
    public void testGetCacheValue() throws Exception {
        CacheMap<String, String> cacheMap = spy(
                CacheMap.build(this::getValue, -1L));
        assertNotNull(cacheMap.getCacheValue(null));
    }

    @Test
    public void testCacheMap1() throws Exception {
        CacheMap<String, String> cacheMap = spy(
                CacheMap.build(this::getValue, -1L));

        verify(cacheMap, never()).setKeyValue(any(), any());

        CacheValueResp<String> cacheValue1 = cacheMap.getCacheValue("1");
        assertTrue(cacheValue1.isRefreshed());
        verify(cacheMap).setKeyValue(any(), any());
        verify(cacheMap, times(2)).needSetKeyValue(any(), anyBoolean());

        CacheValueResp<String> cacheValue2 = cacheMap.getCacheValue("1");
        assertFalse(cacheValue2.isRefreshed());
        verify(cacheMap).setKeyValue(any(), any());
        verify(cacheMap, times(3)).needSetKeyValue(any(), anyBoolean());

        assertTrue(cacheValue1.getValue() == cacheValue2.getValue());

        CacheValueResp<String> cacheValue3 = cacheMap.getCacheValue("2");
        assertTrue(cacheValue3.isRefreshed());
        verify(cacheMap, times(2)).setKeyValue(any(), any());
        verify(cacheMap, times(5)).needSetKeyValue(any(), anyBoolean());
        assertEquals("value[2]", cacheValue3.getValue());
    }

    @Test
    public void testCacheMap2() throws Exception {
        CacheMap<String, String> cacheMap = spy(
                CacheMap.build(this::getValue, 0L));

        verify(cacheMap, never()).setKeyValue(any(), any());

        CacheValueResp<String> cacheValue1 = cacheMap.getCacheValue("1");
        assertTrue(cacheValue1.isRefreshed());
        verify(cacheMap).setKeyValue(any(), any());

        CacheValueResp<String> cacheValue2 = cacheMap.getCacheValue("1");
        assertTrue(cacheValue2.isRefreshed());
        verify(cacheMap, times(2)).setKeyValue(any(), any());

        assertFalse(cacheValue1.getValue() == cacheValue2.getValue());

        CacheValueResp<String> cacheValue3 = cacheMap.getCacheValue("2");
        assertTrue(cacheValue3.isRefreshed());
        verify(cacheMap, times(3)).setKeyValue(any(), any());
        assertEquals("value[2]", cacheValue3.getValue());
    }

    @Test
    public void testCacheMap3() throws Exception {
        CacheMap<String, String> cacheMap = spy(
                CacheMap.build((k) -> null, 0L));

        verify(cacheMap, never()).setKeyValue(any(), any());

        CacheValueResp<String> cacheValue1 = cacheMap.getCacheValue("1");
        assertTrue(cacheValue1.isRefreshed());
        verify(cacheMap).setKeyValue(any(), any());
        assertNull(cacheValue1.getValue());

        CacheValueResp<String> cacheValue2 = cacheMap.getCacheValue("1");
        assertTrue(cacheValue2.isRefreshed());
        verify(cacheMap, times(2)).setKeyValue(any(), any());
        assertNull(cacheValue2.getValue());

        boolean refreshed = cacheMap.setKeyValue("1", getValue("1"));
        assertTrue(refreshed);
        verify(cacheMap, times(3)).setKeyValue(any(), any());

        CacheValueResp<String> cacheValue3 = cacheMap.getCacheValue("1");
        assertTrue(cacheValue3.isRefreshed());
        verify(cacheMap, times(4)).setKeyValue(any(), any());
        assertNull(cacheValue3.getValue());
    }

    @Test
    public void testCacheMap4() throws Exception {
        CacheMap<String, String> cacheMap = spy(
                CacheMap.build((k) -> null, -1L));

        verify(cacheMap, never()).setKeyValue(any(), any());

        CacheValueResp<String> cacheValue1 = cacheMap.getCacheValue("1");
        assertTrue(cacheValue1.isRefreshed());
        verify(cacheMap).setKeyValue(any(), any());
        assertNull(cacheValue1.getValue());

        CacheValueResp<String> cacheValue2 = cacheMap.getCacheValue("1");
        assertTrue(cacheValue2.isRefreshed());
        verify(cacheMap, times(2)).setKeyValue(any(), any());
        assertNull(cacheValue2.getValue());

        boolean refreshed = cacheMap.setKeyValue("1", getValue("1"));
        assertTrue(refreshed);
        verify(cacheMap, times(3)).setKeyValue(any(), any());

        CacheValueResp<String> cacheValue3 = cacheMap.getCacheValue("1");
        assertFalse(cacheValue3.isRefreshed());
        verify(cacheMap, times(3)).setKeyValue(any(), any());
        assertNotNull(cacheValue3.getValue());
    }

    @Test
    public void testCacheMap5() throws Exception {
        CacheMap<String, String> cacheMap = spy(
                CacheMap.build((k) -> null, 0L, true));

        verify(cacheMap, never()).setKeyValue(any(), any());

        CacheValueResp<String> cacheValue1 = cacheMap.getCacheValue("1");
        assertTrue(cacheValue1.isRefreshed());
        verify(cacheMap).setKeyValue(any(), any());
        assertNull(cacheValue1.getValue());

        CacheValueResp<String> cacheValue2 = cacheMap.getCacheValue("1");
        assertTrue(cacheValue2.isRefreshed());
        verify(cacheMap, times(2)).setKeyValue(any(), any());
        assertNull(cacheValue2.getValue());

        boolean refreshed = cacheMap.setKeyValue("1", getValue("1"));
        assertTrue(refreshed);
        verify(cacheMap, times(3)).setKeyValue(any(), any());

        CacheValueResp<String> cacheValue3 = cacheMap.getCacheValue("1");
        assertTrue(cacheValue3.isRefreshed());
        verify(cacheMap, times(4)).setKeyValue(any(), any());
        assertNull(cacheValue3.getValue());
    }

    @Test
    public void testCacheMap6() throws Exception {
        CacheMap<String, String> cacheMap = spy(
                CacheMap.build((k) -> null, -1L, true));

        verify(cacheMap, never()).setKeyValue(any(), any());

        CacheValueResp<String> cacheValue1 = cacheMap.getCacheValue("1");
        assertTrue(cacheValue1.isRefreshed());
        verify(cacheMap).setKeyValue(any(), any());
        assertNull(cacheValue1.getValue());

        CacheValueResp<String> cacheValue2 = cacheMap.getCacheValue("1");
        assertFalse(cacheValue2.isRefreshed());
        verify(cacheMap).setKeyValue(any(), any());
        assertNull(cacheValue2.getValue());

        boolean refreshed = cacheMap.setKeyValue("1", getValue("1"));
        assertTrue(refreshed);
        verify(cacheMap, times(2)).setKeyValue(any(), any());

        CacheValueResp<String> cacheValue3 = cacheMap.getCacheValue("1");
        assertFalse(cacheValue3.isRefreshed());
        verify(cacheMap, times(2)).setKeyValue(any(), any());
        assertNotNull(cacheValue3.getValue());
    }

    @Test
    public void testCacheMap7() throws Exception {
        CacheMap<String, String> cacheMap = spy(
                CacheMap.build((k) -> {
                    throw new TcException();
                    }, -1L, true));

        verify(cacheMap, never()).setKeyValue(any(), any());

        try {
            CacheValueResp<String> cacheValue1 = cacheMap.getCacheValue("1");
        } catch (Exception e) {
            // do nothing
        }
        verify(cacheMap, never()).setKeyValue(any(), any());
    }

    @Test
    public void testCacheMap1_expire() throws Exception {
        CacheMap<String, String> cacheMap = spy(
                CacheMap.build(this::getValue, -1L));

        verify(cacheMap, never()).setKeyValue(any(), any());

        CacheValueResp<String> cacheValue1 = cacheMap.getCacheValue("1");
        assertTrue(cacheValue1.isRefreshed());
        verify(cacheMap).setKeyValue(any(), any());
        verify(cacheMap, times(2)).needSetKeyValue(any(), anyBoolean());

        cacheMap.expire("1");

        CacheValueResp<String> cacheValue2 = cacheMap.getCacheValue("1");
        assertTrue(cacheValue2.isRefreshed());
        verify(cacheMap, times(2)).setKeyValue(any(), any());
        verify(cacheMap, times(4)).needSetKeyValue(any(), anyBoolean());

        assertFalse(cacheValue1.getValue() == cacheValue2.getValue());
    }

    @Test
    public void testCacheMap_concurrent() throws Exception {
        CacheMap<String, String> cacheMap = spy(
                CacheMap.build(this::getValue, -1L));

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(20, 20,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(20));

        AtomicInteger refreshCount = new AtomicInteger(0);
        AtomicInteger nonRefreshCount = new AtomicInteger(0);

        List<Thread> list = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            int t = i;
            list.add(new Thread(() -> {
                int refreshed = 0;
                int nonRefreshed = 0;
                for (int j = 0; j < 1000; j++) {
                    try {
                        CacheValueResp<String> cacheValue = cacheMap.getCacheValue(String.format("%d", j % 10));
                        if (cacheValue.isRefreshed()) {
                            refreshed++;
                        } else {
                            nonRefreshed++;
                        }
                    } catch (Exception e) {
                        // do nothing
                    }
                }
                refreshCount.addAndGet(refreshed);
                nonRefreshCount.addAndGet(nonRefreshed);
                System.out.println(String.format("thread %d: refreshed %d nonRefreshed %d", t, refreshed, nonRefreshed));
            }));
        }

        for (int i = 0; i < 20; i++) {
            poolExecutor.submit(list.get(i));
        }

        poolExecutor.shutdown();
        poolExecutor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("done");

        assertEquals(10, refreshCount.get());
    }

}
