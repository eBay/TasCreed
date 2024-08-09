package com.ebay.magellan.tumbler.core.infra.executor;

import com.ebay.magellan.tumbler.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tumbler.core.domain.task.Task;
import com.ebay.magellan.tumbler.core.domain.task.TaskResult;
import com.ebay.magellan.tumbler.core.infra.executor.help.TestNmExecutor;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NormalTaskTaskExecTest {

    @Test
    public void test() throws Exception {
        TestNmExecutor testExecutor = new TestNmExecutor();
        Task task = new Task();
        testExecutor.init(task, null);

        assertEquals(TaskStateEnum.UNDONE, task.getTaskState());

        testExecutor.execute();

        assertEquals(1, testExecutor.getCount());
        assertEquals(TaskStateEnum.SUCCESS, task.getTaskState());
    }

    @Test(expected = TumblerException.class)
    public void test1() throws Exception {
        TestNmExecutor testExecutor = new TestNmExecutor();
        Task task = new Task();
        testExecutor.init(task, null);

        testExecutor.executeRound();
    }
}
