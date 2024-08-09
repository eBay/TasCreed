package com.ebay.magellan.tascreed.core.infra.executor.checkpoint;

import com.ebay.magellan.tascreed.core.domain.state.partial.TaskCheckpoint;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TaskExecCheckpointTest {

    class TestCheckpoint implements TaskExecCheckpoint {
        String value;
        @Override
        public void fromValue(TaskCheckpoint cp) {
            value = cp.getValue();
        }
        @Override
        public TaskCheckpoint toValue() {
            TaskCheckpoint cp = new TaskCheckpoint();
            cp.setValue(value);
            return cp;
        }
    }

    @Test
    public void test() {
        TestCheckpoint testCheckpoint = new TestCheckpoint();

        TaskCheckpoint cp = new TaskCheckpoint();
        cp.setValue("test");

        testCheckpoint.fromValue(cp);
        assertEquals("test", testCheckpoint.value);

        TaskCheckpoint cp1 = testCheckpoint.toValue();
        assertNotNull(cp1);
        assertEquals("test", cp1.getValue());
    }
}
