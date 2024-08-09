package com.ebay.magellan.tumbler.depend.common.exception;

import org.junit.Test;

public class TumblerExceptionBuilderTest {

    @Test(expected = TumblerException.class)
    public void testThrowException1() throws TumblerException {
        TumblerExceptionBuilder.throwTumblerException(TumblerErrorEnum.TUMBLER_IGNORE_EXCEPTION, "");
    }

    @Test(expected = TumblerException.class)
    public void testThrowException2() throws TumblerException {
        TumblerExceptionBuilder.throwTumblerException(TumblerErrorEnum.TUMBLER_IGNORE_EXCEPTION, "", new Exception());
    }

    @Test(expected = TumblerException.class)
    public void testThrowException3() throws TumblerException {
        TumblerExceptionBuilder.throwUnknownException(new Exception());
    }

}
