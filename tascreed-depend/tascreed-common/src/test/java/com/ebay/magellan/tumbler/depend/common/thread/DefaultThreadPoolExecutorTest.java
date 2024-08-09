package com.ebay.magellan.tumbler.depend.common.thread;

import com.ebay.magellan.tumbler.depend.common.logger.TumblerLogger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DefaultThreadPoolExecutorTest {

    @Mock
    private TumblerLogger logger;

    private int counter = 0;

    @Test
    public void testHasVacancy() {
        DefaultThreadPoolExecutor poolExecutor = new DefaultThreadPoolExecutor(
                1, new TestThreadFactory(), logger);
        assertTrue(poolExecutor.hasVacancy());
    }

    @Test
    public void testSubmit() throws Exception {
        DefaultThreadPoolExecutor poolExecutor = new DefaultThreadPoolExecutor(
                1, new TestThreadFactory(), logger);
        poolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                counter++;
            }
        }).get();
        assertEquals(1, counter);
    }

    private static class TestThreadFactory extends DefaultThreadFactory {
    }
}
