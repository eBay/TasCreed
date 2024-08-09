package com.ebay.magellan.tumbler.core.infra.executor;

import com.ebay.magellan.tumbler.core.domain.task.Task;
import com.ebay.magellan.tumbler.core.infra.executor.help.TestCpExecutor;
import com.ebay.magellan.tumbler.core.infra.executor.help.TestNmExecutor;
import com.ebay.magellan.tumbler.depend.common.logger.TumblerLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskTaskExecFactoryTest {
    @InjectMocks
    private TaskExecutorFactory factory = new TaskExecutorFactory();

    @Mock
    private TaskExecutorRegistry taskExecutorRegistry;
    @Mock
    private ApplicationContext context;
    @Mock
    private TumblerLogger logger;

    @Before
    public void init() {
        doReturn(TestNmExecutor.class).when(taskExecutorRegistry).getTaskExecutor("j1", "s1");
        doReturn(null).when(taskExecutorRegistry).getTaskExecutor("j1", "s2");
    }

    @Test
    public void getTaskExecutorClassFromRegistry() {
        assertEquals(TestNmExecutor.class, factory.getTaskExecutorClassFromRegistry("j1", "s1"));
        assertNull(factory.getTaskExecutorClassFromRegistry("j1", "s2"));
    }

    @Test
    public void getTaskExecutorClassByName() {
        assertEquals(TestNmExecutor.class, factory.getTaskExecutorClassByName("com.ebay.magellan.tumbler.core.infra.executor.help.TestNmExecutor"));
        assertEquals(TestCpExecutor.class, factory.getTaskExecutorClassByName("com.ebay.magellan.tumbler.core.infra.executor.help.TestCpExecutor"));
        assertNull(factory.getTaskExecutorClassByName("com.ebay.magellan.tumbler.core.infra.executor.help.UnknownExecutor"));
    }

    Task buildTask(String jobName, String stepName, String exeClass) {
        Task task = new Task();
        task.setJobName(jobName);
        task.setStepName(stepName);
        task.setExeClass(exeClass);
        return task;
    }

    @Test
    public void getTaskExecutorClass() {
        assertEquals(TestNmExecutor.class, factory.getTaskExecutorClass(
                buildTask("j1", "s1", null)));
        assertNull(factory.getTaskExecutorClass(
                buildTask("j1", "s2", null)));
        assertEquals(TestNmExecutor.class, factory.getTaskExecutorClass(
                buildTask("j1", "s2", "com.ebay.magellan.tumbler.core.infra.executor.help.TestNmExecutor")));
        assertEquals(TestCpExecutor.class, factory.getTaskExecutorClass(
                buildTask("j1", "s1", "com.ebay.magellan.tumbler.core.infra.executor.help.TestCpExecutor")));
        assertNull(factory.getTaskExecutorClass(
                buildTask("j1", "s2", "com.ebay.magellan.tumbler.core.infra.executor.help.UnknownExecutor")));
    }

    @Test
    public void taskExecutorExists() {
        assertTrue(factory.taskExecutorExists(
                buildTask("j1", "s1", null)));
        assertFalse(factory.taskExecutorExists(
                buildTask("j1", "s2", null)));
    }

    @Test
    public void buildTaskExecutor() {
        doReturn(new TestNmExecutor()).when(context).getBean(TestNmExecutor.class);

        Task t1 = buildTask("j1", "s1", null);
        TaskExecutor te1 = factory.buildTaskExecutor(t1);
        assertNotNull(te1);
        assertEquals(TestNmExecutor.class, te1.getClass());

        Task t2 = buildTask("j1", "s2", null);
        TaskExecutor te2 = factory.buildTaskExecutor(t2);
        assertNull(te2);
    }
}
