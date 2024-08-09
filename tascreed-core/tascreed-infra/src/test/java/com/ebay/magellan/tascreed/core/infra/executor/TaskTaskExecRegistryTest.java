package com.ebay.magellan.tascreed.core.infra.executor;

import com.ebay.magellan.tascreed.core.infra.executor.help.TestCpExecutor;
import com.ebay.magellan.tascreed.core.infra.executor.help.TestNmExecutor;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class TaskTaskExecRegistryTest {

    @InjectMocks
    private TaskExecutorRegistry registry = new TaskExecutorRegistry();

    @Mock
    private TcLogger logger;

    @Test
    public void testRegisterTaskExecutor() {
        registry.registerTaskExecutor("j1", "s1", TestNmExecutor.class);
        registry.registerTaskExecutor("j1", "s2", TestNmExecutor.class);
        registry.registerTaskExecutor("j1", "s3", TestCpExecutor.class);

        registry.registerTaskExecutor("j2", "s1", TestNmExecutor.class);
        registry.registerTaskExecutor("j2", "s2", TestNmExecutor.class);
        registry.registerTaskExecutor("j2", "s3", TestCpExecutor.class);

        assertEquals(TestNmExecutor.class, registry.getTaskExecutor("j1", "s1"));
        assertEquals(TestNmExecutor.class, registry.getTaskExecutor("j1", "s2"));
        assertEquals(TestCpExecutor.class, registry.getTaskExecutor("j1", "s3"));

        assertEquals(TestNmExecutor.class, registry.getTaskExecutor("j2", "s1"));
        assertEquals(TestNmExecutor.class, registry.getTaskExecutor("j2", "s2"));
        assertEquals(TestCpExecutor.class, registry.getTaskExecutor("j2", "s3"));

        assertNull(registry.getTaskExecutor("j3", "s1"));
    }
}
