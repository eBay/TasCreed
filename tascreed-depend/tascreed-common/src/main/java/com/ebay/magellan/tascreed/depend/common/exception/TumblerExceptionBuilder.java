package com.ebay.magellan.tascreed.depend.common.exception;

public class TumblerExceptionBuilder {

    private TumblerExceptionBuilder() {}

    public static void throwTumblerException(
            TumblerErrorEnum errorEnum, String errorMessage) throws TumblerException {
        throwTumblerException(errorEnum, errorMessage, null);
    }

    public static void throwTumblerException(
            TumblerErrorEnum errorEnum, String errorMessage, Throwable cause) throws TumblerException {
        if (cause == null) {
            throw new TumblerException(errorEnum, errorMessage);
        } else {
            throw new TumblerException(errorEnum, errorMessage, cause);
        }
    }

    public static void throwUnknownException(Throwable e) throws TumblerException {
        throw new TumblerException(TumblerErrorEnum.TUMBLER_UNKNOWN_EXCEPTION, e.getMessage(), e);
    }

    public static void throwEtcdRetryableException(Throwable e) throws TumblerException {
        throw new TumblerException(TumblerErrorEnum.TUMBLER_RETRY_EXCEPTION, e.getMessage(), e);
    }
}
