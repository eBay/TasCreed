package com.ebay.magellan.tascreed.depend.common.exception;

import org.junit.Test;

import static org.junit.Assert.*;

public class TcExceptionTest {

    @Test
    public void testStatus() {
        assertTrue(new TcException(TcErrorEnum.TUMBLER_IGNORE_EXCEPTION, "").isIgnore());
        assertTrue(new TcException(TcErrorEnum.TUMBLER_RETRY_EXCEPTION, "").isRetry());
        assertTrue(new TcException(TcErrorEnum.TUMBLER_NON_RETRY_EXCEPTION, "").isNonRetry());
        assertTrue(new TcException(TcErrorEnum.TUMBLER_FATAL_EXCEPTION, "").isFatal());
        assertTrue(new TcException(TcErrorEnum.TUMBLER_UNKNOWN_EXCEPTION, "").isFatal());
    }

    @Test
    public void testGetMsg() {
        TcException e = new TcException(TcErrorEnum.TUMBLER_IGNORE_EXCEPTION, "");
        assertNotNull(e.getMessage());
    }

}
