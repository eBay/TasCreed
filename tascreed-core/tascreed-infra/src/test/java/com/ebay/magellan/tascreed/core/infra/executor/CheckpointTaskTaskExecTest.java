package com.ebay.magellan.tascreed.core.infra.executor;

import com.ebay.magellan.tascreed.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.infra.executor.help.TestCpExecutor;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CheckpointTaskTaskExecTest {

    @Test
    public void test() throws Exception {
        TestCpExecutor testExecutor = new TestCpExecutor();
        Task task = new Task();
        testExecutor.init(task, null);

        assertEquals(TaskStateEnum.UNDONE, task.getTaskState());

        testExecutor.executeRound();

        assertEquals(TaskStateEnum.UNDONE, task.getTaskState());
        assertEquals("1", task.getTaskCheckpoint().getValue());

        while (task.getTaskState().isUndone()) {
            // execute once
            testExecutor.executeRound();
        }

        assertEquals("10", task.getTaskCheckpoint().getValue());
        assertEquals(TaskStateEnum.SUCCESS, task.getTaskState());
    }

    @Test(expected = TumblerException.class)
    public void test1() throws Exception {
        TestCpExecutor testExecutor = new TestCpExecutor();
        Task task = new Task();
        testExecutor.init(task, null);

        testExecutor.execute();
    }

}
