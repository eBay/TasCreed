package com.ebay.magellan.tumbler.depend.common.exception;

import org.junit.Test;

import static org.junit.Assert.*;

public class TumblerExceptionTest {

    @Test
    public void testStatus() {
        assertTrue(new TumblerException(TumblerErrorEnum.TUMBLER_IGNORE_EXCEPTION, "").isIgnore());
        assertTrue(new TumblerException(TumblerErrorEnum.TUMBLER_RETRY_EXCEPTION, "").isRetry());
        assertTrue(new TumblerException(TumblerErrorEnum.TUMBLER_NON_RETRY_EXCEPTION, "").isNonRetry());
        assertTrue(new TumblerException(TumblerErrorEnum.TUMBLER_FATAL_EXCEPTION, "").isFatal());
        assertTrue(new TumblerException(TumblerErrorEnum.TUMBLER_UNKNOWN_EXCEPTION, "").isFatal());
    }

    @Test
    public void testGetMsg() {
        TumblerException e = new TumblerException(TumblerErrorEnum.TUMBLER_IGNORE_EXCEPTION, "");
        assertNotNull(e.getMessage());
    }

}
