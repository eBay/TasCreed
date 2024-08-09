package com.ebay.magellan.tascreed.depend.common.exception;

import org.junit.Test;

public class TcExceptionBuilderTest {

    @Test(expected = TcException.class)
    public void testThrowException1() throws TcException {
        TcExceptionBuilder.throwTumblerException(TcErrorEnum.TUMBLER_IGNORE_EXCEPTION, "");
    }

    @Test(expected = TcException.class)
    public void testThrowException2() throws TcException {
        TcExceptionBuilder.throwTumblerException(TcErrorEnum.TUMBLER_IGNORE_EXCEPTION, "", new Exception());
    }

    @Test(expected = TcException.class)
    public void testThrowException3() throws TcException {
        TcExceptionBuilder.throwUnknownException(new Exception());
    }

}
