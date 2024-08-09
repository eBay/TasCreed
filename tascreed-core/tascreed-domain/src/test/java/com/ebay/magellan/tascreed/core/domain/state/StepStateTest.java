package com.ebay.magellan.tascreed.core.domain.state;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ebay.magellan.tascreed.core.domain.state.TaskStateEnum.*;
import static org.junit.Assert.*;

public class StepStateTest {

    @Test
    public void test_DORMANT() {
        assertTrue(StepStateEnum.DORMANT.undone());
        assertFalse(StepStateEnum.DORMANT.done());

        assertTrue(StepStateEnum.DORMANT.resultUnknown());
        assertFalse(StepStateEnum.DORMANT.resultSuccess());
        assertFalse(StepStateEnum.DORMANT.resultFailed());
        assertFalse(StepStateEnum.DORMANT.resultError());

        assertTrue(StepStateEnum.DORMANT.canCreateTask());
        assertTrue(StepStateEnum.DORMANT.notStarted());
        assertFalse(StepStateEnum.DORMANT.canRetry());

        assertTrue(StepStateEnum.DORMANT.isDormant());
        assertFalse(StepStateEnum.DORMANT.isStart());
        assertFalse(StepStateEnum.DORMANT.isReady());
        assertFalse(StepStateEnum.DORMANT.isSuccess());
        assertFalse(StepStateEnum.DORMANT.isIgnored());
        assertFalse(StepStateEnum.DORMANT.isAcceptableFailed());
        assertFalse(StepStateEnum.DORMANT.isFailed());
        assertFalse(StepStateEnum.DORMANT.isError());
        assertFalse(StepStateEnum.DORMANT.isSkipByFailed());
        assertFalse(StepStateEnum.DORMANT.isSkipByError());
    }

    @Test
    public void test_START() {
        assertTrue(StepStateEnum.START.undone());
        assertFalse(StepStateEnum.START.done());

        assertTrue(StepStateEnum.START.resultUnknown());
        assertFalse(StepStateEnum.START.resultSuccess());
        assertFalse(StepStateEnum.START.resultFailed());
        assertFalse(StepStateEnum.START.resultError());

        assertTrue(StepStateEnum.START.canCreateTask());
        assertFalse(StepStateEnum.START.notStarted());
        assertFalse(StepStateEnum.START.canRetry());

        assertFalse(StepStateEnum.START.isDormant());
        assertTrue(StepStateEnum.START.isStart());
        assertFalse(StepStateEnum.START.isReady());
        assertFalse(StepStateEnum.START.isSuccess());
        assertFalse(StepStateEnum.START.isIgnored());
        assertFalse(StepStateEnum.START.isAcceptableFailed());
        assertFalse(StepStateEnum.START.isFailed());
        assertFalse(StepStateEnum.START.isError());
        assertFalse(StepStateEnum.START.isSkipByFailed());
        assertFalse(StepStateEnum.START.isSkipByError());
    }

    @Test
    public void test_READY() {
        assertTrue(StepStateEnum.READY.undone());
        assertFalse(StepStateEnum.READY.done());

        assertTrue(StepStateEnum.READY.resultUnknown());
        assertFalse(StepStateEnum.READY.resultSuccess());
        assertFalse(StepStateEnum.READY.resultFailed());
        assertFalse(StepStateEnum.READY.resultError());

        assertFalse(StepStateEnum.READY.canCreateTask());
        assertFalse(StepStateEnum.READY.notStarted());
        assertFalse(StepStateEnum.READY.canRetry());

        assertFalse(StepStateEnum.READY.isDormant());
        assertFalse(StepStateEnum.READY.isStart());
        assertTrue(StepStateEnum.READY.isReady());
        assertFalse(StepStateEnum.READY.isSuccess());
        assertFalse(StepStateEnum.READY.isIgnored());
        assertFalse(StepStateEnum.READY.isAcceptableFailed());
        assertFalse(StepStateEnum.READY.isFailed());
        assertFalse(StepStateEnum.READY.isError());
        assertFalse(StepStateEnum.READY.isSkipByFailed());
        assertFalse(StepStateEnum.READY.isSkipByError());
    }

    @Test
    public void test_SUCCESS() {
        assertFalse(StepStateEnum.SUCCESS.undone());
        assertTrue(StepStateEnum.SUCCESS.done());

        assertFalse(StepStateEnum.SUCCESS.resultUnknown());
        assertTrue(StepStateEnum.SUCCESS.resultSuccess());
        assertFalse(StepStateEnum.SUCCESS.resultFailed());
        assertFalse(StepStateEnum.SUCCESS.resultError());

        assertFalse(StepStateEnum.SUCCESS.canCreateTask());
        assertFalse(StepStateEnum.SUCCESS.notStarted());
        assertFalse(StepStateEnum.SUCCESS.canRetry());

        assertFalse(StepStateEnum.SUCCESS.isDormant());
        assertFalse(StepStateEnum.SUCCESS.isStart());
        assertFalse(StepStateEnum.SUCCESS.isReady());
        assertTrue(StepStateEnum.SUCCESS.isSuccess());
        assertFalse(StepStateEnum.SUCCESS.isIgnored());
        assertFalse(StepStateEnum.SUCCESS.isAcceptableFailed());
        assertFalse(StepStateEnum.SUCCESS.isFailed());
        assertFalse(StepStateEnum.SUCCESS.isError());
        assertFalse(StepStateEnum.SUCCESS.isSkipByFailed());
        assertFalse(StepStateEnum.SUCCESS.isSkipByError());
    }

    @Test
    public void test_IGNORED() {
        assertFalse(StepStateEnum.IGNORED.undone());
        assertTrue(StepStateEnum.IGNORED.done());

        assertFalse(StepStateEnum.IGNORED.resultUnknown());
        assertTrue(StepStateEnum.IGNORED.resultSuccess());
        assertFalse(StepStateEnum.IGNORED.resultFailed());
        assertFalse(StepStateEnum.IGNORED.resultError());

        assertFalse(StepStateEnum.IGNORED.canCreateTask());
        assertFalse(StepStateEnum.IGNORED.notStarted());
        assertFalse(StepStateEnum.IGNORED.canRetry());

        assertFalse(StepStateEnum.IGNORED.isDormant());
        assertFalse(StepStateEnum.IGNORED.isStart());
        assertFalse(StepStateEnum.IGNORED.isReady());
        assertFalse(StepStateEnum.IGNORED.isSuccess());
        assertTrue(StepStateEnum.IGNORED.isIgnored());
        assertFalse(StepStateEnum.IGNORED.isAcceptableFailed());
        assertFalse(StepStateEnum.IGNORED.isFailed());
        assertFalse(StepStateEnum.IGNORED.isError());
        assertFalse(StepStateEnum.IGNORED.isSkipByFailed());
        assertFalse(StepStateEnum.IGNORED.isSkipByError());
    }

    @Test
    public void test_ACCEPTABLE_FAILED() {
        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.undone());
        assertTrue(StepStateEnum.ACCEPTABLE_FAILED.done());

        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.resultUnknown());
        assertTrue(StepStateEnum.ACCEPTABLE_FAILED.resultSuccess());
        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.resultFailed());
        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.resultError());

        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.canCreateTask());
        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.notStarted());
        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.canRetry());

        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.isDormant());
        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.isStart());
        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.isReady());
        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.isSuccess());
        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.isIgnored());
        assertTrue(StepStateEnum.ACCEPTABLE_FAILED.isAcceptableFailed());
        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.isFailed());
        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.isError());
        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.isSkipByFailed());
        assertFalse(StepStateEnum.ACCEPTABLE_FAILED.isSkipByError());
    }

    @Test
    public void test_FAILED() {
        assertFalse(StepStateEnum.FAILED.undone());
        assertTrue(StepStateEnum.FAILED.done());

        assertFalse(StepStateEnum.FAILED.resultUnknown());
        assertFalse(StepStateEnum.FAILED.resultSuccess());
        assertTrue(StepStateEnum.FAILED.resultFailed());
        assertFalse(StepStateEnum.FAILED.resultError());

        assertFalse(StepStateEnum.FAILED.canCreateTask());
        assertFalse(StepStateEnum.FAILED.notStarted());
        assertFalse(StepStateEnum.FAILED.canRetry());

        assertFalse(StepStateEnum.FAILED.isDormant());
        assertFalse(StepStateEnum.FAILED.isStart());
        assertFalse(StepStateEnum.FAILED.isReady());
        assertFalse(StepStateEnum.FAILED.isSuccess());
        assertFalse(StepStateEnum.FAILED.isIgnored());
        assertFalse(StepStateEnum.FAILED.isAcceptableFailed());
        assertTrue(StepStateEnum.FAILED.isFailed());
        assertFalse(StepStateEnum.FAILED.isError());
        assertFalse(StepStateEnum.FAILED.isSkipByFailed());
        assertFalse(StepStateEnum.FAILED.isSkipByError());
    }

    @Test
    public void test_ERROR() {
        assertFalse(StepStateEnum.ERROR.undone());
        assertTrue(StepStateEnum.ERROR.done());

        assertFalse(StepStateEnum.ERROR.resultUnknown());
        assertFalse(StepStateEnum.ERROR.resultSuccess());
        assertFalse(StepStateEnum.ERROR.resultFailed());
        assertTrue(StepStateEnum.ERROR.resultError());

        assertFalse(StepStateEnum.ERROR.canCreateTask());
        assertFalse(StepStateEnum.ERROR.notStarted());
        assertTrue(StepStateEnum.ERROR.canRetry());

        assertFalse(StepStateEnum.ERROR.isDormant());
        assertFalse(StepStateEnum.ERROR.isStart());
        assertFalse(StepStateEnum.ERROR.isReady());
        assertFalse(StepStateEnum.ERROR.isSuccess());
        assertFalse(StepStateEnum.ERROR.isIgnored());
        assertFalse(StepStateEnum.ERROR.isAcceptableFailed());
        assertFalse(StepStateEnum.ERROR.isFailed());
        assertTrue(StepStateEnum.ERROR.isError());
        assertFalse(StepStateEnum.ERROR.isSkipByFailed());
        assertFalse(StepStateEnum.ERROR.isSkipByError());
    }

    @Test
    public void test_SKIP_BY_FAILED() {
        assertFalse(StepStateEnum.SKIP_BY_FAILED.undone());
        assertTrue(StepStateEnum.SKIP_BY_FAILED.done());

        assertFalse(StepStateEnum.SKIP_BY_FAILED.resultUnknown());
        assertFalse(StepStateEnum.SKIP_BY_FAILED.resultSuccess());
        assertTrue(StepStateEnum.SKIP_BY_FAILED.resultFailed());
        assertFalse(StepStateEnum.SKIP_BY_FAILED.resultError());

        assertFalse(StepStateEnum.SKIP_BY_FAILED.canCreateTask());
        assertTrue(StepStateEnum.SKIP_BY_FAILED.notStarted());
        assertFalse(StepStateEnum.SKIP_BY_FAILED.canRetry());

        assertFalse(StepStateEnum.SKIP_BY_FAILED.isDormant());
        assertFalse(StepStateEnum.SKIP_BY_FAILED.isStart());
        assertFalse(StepStateEnum.SKIP_BY_FAILED.isReady());
        assertFalse(StepStateEnum.SKIP_BY_FAILED.isSuccess());
        assertFalse(StepStateEnum.SKIP_BY_FAILED.isIgnored());
        assertFalse(StepStateEnum.SKIP_BY_FAILED.isAcceptableFailed());
        assertFalse(StepStateEnum.SKIP_BY_FAILED.isFailed());
        assertFalse(StepStateEnum.SKIP_BY_FAILED.isError());
        assertTrue(StepStateEnum.SKIP_BY_FAILED.isSkipByFailed());
        assertFalse(StepStateEnum.SKIP_BY_FAILED.isSkipByError());
    }

    @Test
    public void test_SKIP_BY_ERROR() {
        assertFalse(StepStateEnum.SKIP_BY_ERROR.undone());
        assertTrue(StepStateEnum.SKIP_BY_ERROR.done());

        assertFalse(StepStateEnum.SKIP_BY_ERROR.resultUnknown());
        assertFalse(StepStateEnum.SKIP_BY_ERROR.resultSuccess());
        assertFalse(StepStateEnum.SKIP_BY_ERROR.resultFailed());
        assertTrue(StepStateEnum.SKIP_BY_ERROR.resultError());

        assertFalse(StepStateEnum.SKIP_BY_ERROR.canCreateTask());
        assertTrue(StepStateEnum.SKIP_BY_ERROR.notStarted());
        assertTrue(StepStateEnum.SKIP_BY_ERROR.canRetry());

        assertFalse(StepStateEnum.SKIP_BY_ERROR.isDormant());
        assertFalse(StepStateEnum.SKIP_BY_ERROR.isStart());
        assertFalse(StepStateEnum.SKIP_BY_ERROR.isReady());
        assertFalse(StepStateEnum.SKIP_BY_ERROR.isSuccess());
        assertFalse(StepStateEnum.SKIP_BY_ERROR.isIgnored());
        assertFalse(StepStateEnum.SKIP_BY_ERROR.isAcceptableFailed());
        assertFalse(StepStateEnum.SKIP_BY_ERROR.isFailed());
        assertFalse(StepStateEnum.SKIP_BY_ERROR.isError());
        assertFalse(StepStateEnum.SKIP_BY_ERROR.isSkipByFailed());
        assertTrue(StepStateEnum.SKIP_BY_ERROR.isSkipByError());
    }

    private List<TaskStateEnum> buildTaskStates(TaskStateEnum... taskStates) {
        return Arrays.stream(taskStates).collect(Collectors.toList());
    }

    @Test
    public void testGetStepDoneState() {
        assertNull(StepStateEnum.getStepDoneState(
                StepStateEnum.DORMANT, buildTaskStates(), false));

        assertEquals(StepStateEnum.READY, StepStateEnum.getStepDoneState(
                StepStateEnum.READY, buildTaskStates(ERROR, UNDONE, FAILED, SUCCESS), false));
        assertEquals(StepStateEnum.READY, StepStateEnum.getStepDoneState(
                StepStateEnum.READY, buildTaskStates(ERROR, UNDONE, FAILED, SUCCESS), true));

        assertEquals(StepStateEnum.READY, StepStateEnum.getStepDoneState(
                StepStateEnum.READY, buildTaskStates(UNDONE, FAILED, SUCCESS), false));
        assertEquals(StepStateEnum.READY, StepStateEnum.getStepDoneState(
                StepStateEnum.READY, buildTaskStates(UNDONE, FAILED, SUCCESS), true));

        assertEquals(StepStateEnum.ERROR, StepStateEnum.getStepDoneState(
                StepStateEnum.READY, buildTaskStates(ERROR, FAILED, SUCCESS), false));
        assertEquals(StepStateEnum.ERROR, StepStateEnum.getStepDoneState(
                StepStateEnum.READY, buildTaskStates(ERROR, FAILED, SUCCESS), true));

        assertEquals(StepStateEnum.FAILED, StepStateEnum.getStepDoneState(
                StepStateEnum.READY, buildTaskStates(FAILED, SUCCESS), false));
        assertEquals(StepStateEnum.ACCEPTABLE_FAILED, StepStateEnum.getStepDoneState(
                StepStateEnum.READY, buildTaskStates(FAILED, SUCCESS), true));

        assertEquals(StepStateEnum.SUCCESS, StepStateEnum.getStepDoneState(
                StepStateEnum.READY, buildTaskStates(SUCCESS, SUCCESS), false));
    }

}
