package com.ebay.magellan.tascreed.core.domain.state;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ebay.magellan.tascreed.core.domain.state.StepStateEnum.*;
import static org.junit.Assert.*;

public class JobStateTest {

    @Test
    public void test_UNDONE() {
        assertTrue(JobStateEnum.UNDONE.undone());
        assertFalse(JobStateEnum.UNDONE.done());

        assertTrue(JobStateEnum.UNDONE.resultUnknown());
        assertFalse(JobStateEnum.UNDONE.resultSuccess());
        assertFalse(JobStateEnum.UNDONE.resultFailed());
        assertFalse(JobStateEnum.UNDONE.resultError());

        assertTrue(JobStateEnum.UNDONE.canCreateTask());
        assertFalse(JobStateEnum.UNDONE.canAutoArchive());
        assertFalse(JobStateEnum.UNDONE.canRetry());

        assertTrue(JobStateEnum.UNDONE.isUndone());
        assertFalse(JobStateEnum.UNDONE.isStuck());
        assertFalse(JobStateEnum.UNDONE.isSuccess());
        assertFalse(JobStateEnum.UNDONE.isFailed());
        assertFalse(JobStateEnum.UNDONE.isError());
    }

    @Test
    public void test_STUCK() {
        assertTrue(JobStateEnum.STUCK.undone());
        assertFalse(JobStateEnum.STUCK.done());

        assertFalse(JobStateEnum.STUCK.resultUnknown());
        assertFalse(JobStateEnum.STUCK.resultSuccess());
        assertFalse(JobStateEnum.STUCK.resultFailed());
        assertTrue(JobStateEnum.STUCK.resultError());

        assertFalse(JobStateEnum.STUCK.canCreateTask());
        assertFalse(JobStateEnum.STUCK.canAutoArchive());
        assertFalse(JobStateEnum.STUCK.canRetry());

        assertFalse(JobStateEnum.STUCK.isUndone());
        assertTrue(JobStateEnum.STUCK.isStuck());
        assertFalse(JobStateEnum.STUCK.isSuccess());
        assertFalse(JobStateEnum.STUCK.isFailed());
        assertFalse(JobStateEnum.STUCK.isError());
    }

    @Test
    public void test_SUCCESS() {
        assertFalse(JobStateEnum.SUCCESS.undone());
        assertTrue(JobStateEnum.SUCCESS.done());

        assertFalse(JobStateEnum.SUCCESS.resultUnknown());
        assertTrue(JobStateEnum.SUCCESS.resultSuccess());
        assertFalse(JobStateEnum.SUCCESS.resultFailed());
        assertFalse(JobStateEnum.SUCCESS.resultError());

        assertFalse(JobStateEnum.SUCCESS.canCreateTask());
        assertTrue(JobStateEnum.SUCCESS.canAutoArchive());
        assertFalse(JobStateEnum.SUCCESS.canRetry());

        assertFalse(JobStateEnum.SUCCESS.isUndone());
        assertFalse(JobStateEnum.SUCCESS.isStuck());
        assertTrue(JobStateEnum.SUCCESS.isSuccess());
        assertFalse(JobStateEnum.SUCCESS.isFailed());
        assertFalse(JobStateEnum.SUCCESS.isError());
    }

    @Test
    public void test_FAILED() {
        assertFalse(JobStateEnum.FAILED.undone());
        assertTrue(JobStateEnum.FAILED.done());

        assertFalse(JobStateEnum.FAILED.resultUnknown());
        assertFalse(JobStateEnum.FAILED.resultSuccess());
        assertTrue(JobStateEnum.FAILED.resultFailed());
        assertFalse(JobStateEnum.FAILED.resultError());

        assertFalse(JobStateEnum.FAILED.canCreateTask());
        assertTrue(JobStateEnum.FAILED.canAutoArchive());
        assertFalse(JobStateEnum.FAILED.canRetry());

        assertFalse(JobStateEnum.FAILED.isUndone());
        assertFalse(JobStateEnum.FAILED.isStuck());
        assertFalse(JobStateEnum.FAILED.isSuccess());
        assertTrue(JobStateEnum.FAILED.isFailed());
        assertFalse(JobStateEnum.FAILED.isError());
    }

    @Test
    public void test_ERROR() {
        assertFalse(JobStateEnum.ERROR.undone());
        assertTrue(JobStateEnum.ERROR.done());

        assertFalse(JobStateEnum.ERROR.resultUnknown());
        assertFalse(JobStateEnum.ERROR.resultSuccess());
        assertFalse(JobStateEnum.ERROR.resultFailed());
        assertTrue(JobStateEnum.ERROR.resultError());

        assertFalse(JobStateEnum.ERROR.canCreateTask());
        assertFalse(JobStateEnum.ERROR.canAutoArchive());
        assertTrue(JobStateEnum.ERROR.canRetry());

        assertFalse(JobStateEnum.ERROR.isUndone());
        assertFalse(JobStateEnum.ERROR.isStuck());
        assertFalse(JobStateEnum.ERROR.isSuccess());
        assertFalse(JobStateEnum.ERROR.isFailed());
        assertTrue(JobStateEnum.ERROR.isError());
    }

    private List<StepStateEnum> buildStepStates(StepStateEnum... stepStates) {
        return Arrays.stream(stepStates).collect(Collectors.toList());
    }

    @Test
    public void testGetStepDoneState() {
        assertNull(JobStateEnum.getJobState(JobStateEnum.UNDONE, buildStepStates()));

        assertEquals(JobStateEnum.UNDONE, JobStateEnum.getJobState(JobStateEnum.UNDONE,
                buildStepStates(ERROR, DORMANT, FAILED, SUCCESS, IGNORED, ACCEPTABLE_FAILED, SKIP_BY_FAILED)));
        assertEquals(JobStateEnum.UNDONE, JobStateEnum.getJobState(JobStateEnum.UNDONE,
                buildStepStates(DORMANT, FAILED, SUCCESS, IGNORED, ACCEPTABLE_FAILED, SKIP_BY_FAILED)));
        assertEquals(JobStateEnum.FAILED, JobStateEnum.getJobState(JobStateEnum.UNDONE,
                buildStepStates(FAILED, SUCCESS, IGNORED, ACCEPTABLE_FAILED, SKIP_BY_FAILED)));
        assertEquals(JobStateEnum.ERROR, JobStateEnum.getJobState(JobStateEnum.UNDONE,
                buildStepStates(SUCCESS, IGNORED, ACCEPTABLE_FAILED, SKIP_BY_FAILED, SKIP_BY_ERROR)));
        assertEquals(JobStateEnum.FAILED, JobStateEnum.getJobState(JobStateEnum.UNDONE,
                buildStepStates(SUCCESS, IGNORED, ACCEPTABLE_FAILED, SKIP_BY_FAILED)));
        assertEquals(JobStateEnum.SUCCESS, JobStateEnum.getJobState(JobStateEnum.UNDONE,
                buildStepStates(SUCCESS, IGNORED, ACCEPTABLE_FAILED)));
        assertEquals(JobStateEnum.SUCCESS, JobStateEnum.getJobState(JobStateEnum.UNDONE,
                buildStepStates(SUCCESS, SUCCESS, SUCCESS)));
    }
    
}
