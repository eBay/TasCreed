package com.ebay.magellan.tascreed.depend.common.cache;

import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class CacheItemTest {

    private Map<String, String> genData() {
        Map<String, String> data = new HashMap<>();
        data.put("k1", "v1");
        data.put("k2", "v2");
        data.put("k3", "v3");
        return data;
    }

    @Test
    public void testCacheItem1() throws Exception {
        CacheItem<Map<String, String>> cacheItem = spy(
                CacheItem.build(this::genData, -1L));

        verify(cacheItem, never()).setValue(any());

        CacheValueResp<Map<String, String>> cacheValue1 = cacheItem.getCacheValue();
        assertTrue(cacheValue1.isRefreshed());
        verify(cacheItem).setValue(any());
        verify(cacheItem, times(2)).needSetValue(anyBoolean());

        CacheValueResp<Map<String, String>> cacheValue2 = cacheItem.getCacheValue();
        assertFalse(cacheValue2.isRefreshed());
        verify(cacheItem).setValue(any());
        verify(cacheItem, times(3)).needSetValue(anyBoolean());

        assertTrue(cacheValue1.getValue() == cacheValue2.getValue());
    }

    @Test
    public void testCacheItem2() throws Exception {
        CacheItem<Map<String, String>> cacheItem = spy(
                CacheItem.build(this::genData, 0L));

        verify(cacheItem, never()).setValue(any());

        CacheValueResp<Map<String, String>> cacheValue1 = cacheItem.getCacheValue();
        assertTrue(cacheValue1.isRefreshed());
        verify(cacheItem).setValue(any());

        CacheValueResp<Map<String, String>> cacheValue2 = cacheItem.getCacheValue();
        assertTrue(cacheValue2.isRefreshed());
        verify(cacheItem, times(2)).setValue(any());

        assertFalse(cacheValue1.getValue() == cacheValue2.getValue());
    }

    @Test
    public void testCacheItem3() throws Exception {
        CacheItem<Map<String, String>> cacheItem = spy(
                CacheItem.build(() -> null, 0L));

        verify(cacheItem, never()).setValue(any());

        CacheValueResp<Map<String, String>> cacheValue1 = cacheItem.getCacheValue();
        assertTrue(cacheValue1.isRefreshed());
        verify(cacheItem).setValue(any());
        assertNull(cacheValue1.getValue());

        CacheValueResp<Map<String, String>> cacheValue2 = cacheItem.getCacheValue();
        assertTrue(cacheValue2.isRefreshed());
        verify(cacheItem, times(2)).setValue(any());
        assertNull(cacheValue2.getValue());

        boolean refreshed = cacheItem.setValue(genData());
        assertTrue(refreshed);
        verify(cacheItem, times(3)).setValue(any());

        CacheValueResp<Map<String, String>> cacheValue3 = cacheItem.getCacheValue();
        assertTrue(cacheValue3.isRefreshed());
        verify(cacheItem, times(4)).setValue(any());
        assertNull(cacheValue3.getValue());
    }

    @Test
    public void testCacheItem4() throws Exception {
        CacheItem<Map<String, String>> cacheItem = spy(
                CacheItem.build(() -> null, -1L));

        verify(cacheItem, never()).setValue(any());

        CacheValueResp<Map<String, String>> cacheValue1 = cacheItem.getCacheValue();
        assertTrue(cacheValue1.isRefreshed());
        verify(cacheItem).setValue(any());
        assertNull(cacheValue1.getValue());

        CacheValueResp<Map<String, String>> cacheValue2 = cacheItem.getCacheValue();
        assertTrue(cacheValue2.isRefreshed());
        verify(cacheItem, times(2)).setValue(any());
        assertNull(cacheValue2.getValue());

        boolean refreshed = cacheItem.setValue(genData());
        assertTrue(refreshed);
        verify(cacheItem, times(3)).setValue(any());

        CacheValueResp<Map<String, String>> cacheValue3 = cacheItem.getCacheValue();
        assertFalse(cacheValue3.isRefreshed());
        verify(cacheItem, times(3)).setValue(any());
        assertNotNull(cacheValue3.getValue());
    }

    @Test
    public void testCacheItem5() throws Exception {
        CacheItem<Map<String, String>> cacheItem = spy(
                CacheItem.build(() -> null, 0L, true));

        verify(cacheItem, never()).setValue(any());

        CacheValueResp<Map<String, String>> cacheValue1 = cacheItem.getCacheValue();
        assertTrue(cacheValue1.isRefreshed());
        verify(cacheItem).setValue(any());
        assertNull(cacheValue1.getValue());

        CacheValueResp<Map<String, String>> cacheValue2 = cacheItem.getCacheValue();
        assertTrue(cacheValue2.isRefreshed());
        verify(cacheItem, times(2)).setValue(any());
        assertNull(cacheValue2.getValue());

        boolean refreshed = cacheItem.setValue(genData());
        assertTrue(refreshed);
        verify(cacheItem, times(3)).setValue(any());

        CacheValueResp<Map<String, String>> cacheValue3 = cacheItem.getCacheValue();
        assertTrue(cacheValue3.isRefreshed());
        verify(cacheItem, times(4)).setValue(any());
        assertNull(cacheValue3.getValue());
    }

    @Test
    public void testCacheItem6() throws Exception {
        CacheItem<Map<String, String>> cacheItem = spy(
                CacheItem.build(() -> null, -1L, true));

        verify(cacheItem, never()).setValue(any());

        CacheValueResp<Map<String, String>> cacheValue1 = cacheItem.getCacheValue();
        assertTrue(cacheValue1.isRefreshed());
        verify(cacheItem).setValue(any());
        assertNull(cacheValue1.getValue());

        CacheValueResp<Map<String, String>> cacheValue2 = cacheItem.getCacheValue();
        assertFalse(cacheValue2.isRefreshed());
        verify(cacheItem).setValue(any());
        assertNull(cacheValue2.getValue());

        boolean refreshed = cacheItem.setValue(genData());
        assertTrue(refreshed);
        verify(cacheItem, times(2)).setValue(any());

        CacheValueResp<Map<String, String>> cacheValue3 = cacheItem.getCacheValue();
        assertFalse(cacheValue3.isRefreshed());
        verify(cacheItem, times(2)).setValue(any());
        assertNotNull(cacheValue3.getValue());
    }

    @Test
    public void testCacheItem7() throws Exception {
        CacheItem<String> cacheItem = spy(
                CacheItem.build(() -> {
                    throw new TcException();
                    }, -1L, true));

        verify(cacheItem, never()).setValue(any());

        try {
            CacheValueResp<String> cacheValue1 = cacheItem.getCacheValue();
        } catch (Exception e) {
            // do nothing
        }
        verify(cacheItem, never()).setValue(any());
    }

    @Test
    public void testCacheItem1_expire() throws Exception {
        CacheItem<Map<String, String>> cacheItem = spy(
                CacheItem.build(this::genData, -1L));

        verify(cacheItem, never()).setValue(any());

        CacheValueResp<Map<String, String>> cacheValue1 = cacheItem.getCacheValue();
        assertTrue(cacheValue1.isRefreshed());
        verify(cacheItem).setValue(any());

        cacheItem.expire();

        CacheValueResp<Map<String, String>> cacheValue2 = cacheItem.getCacheValue();
        assertTrue(cacheValue2.isRefreshed());
        verify(cacheItem, times(2)).setValue(any());

        assertFalse(cacheValue1.getValue() == cacheValue2.getValue());
    }

    @Test
    public void testCacheItem_concurrent() throws Exception {
        CacheItem<Map<String, String>> cacheItem = spy(
                CacheItem.build(this::genData, -1L));

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
                        CacheValueResp<Map<String, String>> cacheValue = cacheItem.getCacheValue();
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

        assertEquals(1, refreshCount.get());
    }

}
