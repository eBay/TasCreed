package com.ebay.magellan.tascreed.core.domain.state;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TaskStateTest {

    @Test
    public void test_UNDONE() {
        assertTrue(TaskStateEnum.UNDONE.undone());
        assertFalse(TaskStateEnum.UNDONE.done());

        assertTrue(TaskStateEnum.UNDONE.resultUnknown());
        assertFalse(TaskStateEnum.UNDONE.resultSuccess());
        assertFalse(TaskStateEnum.UNDONE.resultFailed());
        assertFalse(TaskStateEnum.UNDONE.resultError());

        assertTrue(TaskStateEnum.UNDONE.onHold());
        assertFalse(TaskStateEnum.UNDONE.canRetry());

        assertTrue(TaskStateEnum.UNDONE.isUndone());
        assertFalse(TaskStateEnum.UNDONE.isSuccess());
        assertFalse(TaskStateEnum.UNDONE.isFailed());
        assertFalse(TaskStateEnum.UNDONE.isError());
    }

    @Test
    public void test_SUCCESS() {
        assertFalse(TaskStateEnum.SUCCESS.undone());
        assertTrue(TaskStateEnum.SUCCESS.done());

        assertFalse(TaskStateEnum.SUCCESS.resultUnknown());
        assertTrue(TaskStateEnum.SUCCESS.resultSuccess());
        assertFalse(TaskStateEnum.SUCCESS.resultFailed());
        assertFalse(TaskStateEnum.SUCCESS.resultError());

        assertFalse(TaskStateEnum.SUCCESS.onHold());
        assertFalse(TaskStateEnum.SUCCESS.canRetry());

        assertFalse(TaskStateEnum.SUCCESS.isUndone());
        assertTrue(TaskStateEnum.SUCCESS.isSuccess());
        assertFalse(TaskStateEnum.SUCCESS.isFailed());
        assertFalse(TaskStateEnum.SUCCESS.isError());
    }

    @Test
    public void test_FAILED() {
        assertFalse(TaskStateEnum.FAILED.undone());
        assertTrue(TaskStateEnum.FAILED.done());

        assertFalse(TaskStateEnum.FAILED.resultUnknown());
        assertTrue(TaskStateEnum.FAILED.resultFailed());
        assertFalse(TaskStateEnum.FAILED.resultError());

        assertFalse(TaskStateEnum.FAILED.onHold());
        assertFalse(TaskStateEnum.FAILED.canRetry());

        assertFalse(TaskStateEnum.FAILED.isUndone());
        assertFalse(TaskStateEnum.FAILED.isSuccess());
        assertTrue(TaskStateEnum.FAILED.isFailed());
        assertFalse(TaskStateEnum.FAILED.isError());
    }

    @Test
    public void test_ERROR() {
        assertFalse(TaskStateEnum.ERROR.undone());
        assertTrue(TaskStateEnum.ERROR.done());

        assertFalse(TaskStateEnum.ERROR.resultUnknown());
        assertFalse(TaskStateEnum.ERROR.resultSuccess());
        assertFalse(TaskStateEnum.ERROR.resultFailed());
        assertTrue(TaskStateEnum.ERROR.resultError());

        assertFalse(TaskStateEnum.ERROR.onHold());
        assertTrue(TaskStateEnum.ERROR.canRetry());

        assertFalse(TaskStateEnum.ERROR.isUndone());
        assertFalse(TaskStateEnum.ERROR.isSuccess());
        assertFalse(TaskStateEnum.ERROR.isFailed());
        assertTrue(TaskStateEnum.ERROR.isError());
    }

}
