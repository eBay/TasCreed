package com.ebay.magellan.tumbler.depend.common.util;

import org.junit.Test;

import java.io.IOException;

public class ExceptionUtilTest {

    @Test
    public void ExceptionUtilTest() {
        IOException e = new IOException();
        String exception = ExceptionUtil.getStackTrace(e);
        System.out.println(exception);
        assert (exception.length() > 0);
    }
}
