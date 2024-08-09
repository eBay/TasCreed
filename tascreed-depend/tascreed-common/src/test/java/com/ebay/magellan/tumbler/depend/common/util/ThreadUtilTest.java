package com.ebay.magellan.tumbler.depend.common.util;

import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class ThreadUtilTest {
    @Test
    public void getCurrentThreadNameTest() {
        String name = ThreadUtil.getCurrentThreadName();
        System.out.println(name);
        assert (name.contains("/"));
    }

    @Test
    public void invokeAllThreadsTest() throws TumblerException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Callable<Boolean>> threads = new ArrayList<>();
        Callable<Boolean> callable1 = () -> true;
        Callable<Boolean> callable2 = () -> false;
        threads.add(callable1);
        threads.add(callable2);
        List<Boolean> results = ThreadUtil.invokeAllThreads(executorService, threads);
        assert (results.size() == 2);
        assertTrue(results.get(0));
        assertFalse(results.get(1));
    }
}
