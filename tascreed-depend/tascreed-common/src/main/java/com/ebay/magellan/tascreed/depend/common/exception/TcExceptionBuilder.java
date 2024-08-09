package com.ebay.magellan.tascreed.depend.common.exception;

public class TcExceptionBuilder {

    private TcExceptionBuilder() {}

    public static void throwTumblerException(
            TcErrorEnum errorEnum, String errorMessage) throws TcException {
        throwTumblerException(errorEnum, errorMessage, null);
    }

    public static void throwTumblerException(
            TcErrorEnum errorEnum, String errorMessage, Throwable cause) throws TcException {
        if (cause == null) {
            throw new TcException(errorEnum, errorMessage);
        } else {
            throw new TcException(errorEnum, errorMessage, cause);
        }
    }

    public static void throwUnknownException(Throwable e) throws TcException {
        throw new TcException(TcErrorEnum.TUMBLER_UNKNOWN_EXCEPTION, e.getMessage(), e);
    }

    public static void throwEtcdRetryableException(Throwable e) throws TcException {
        throw new TcException(TcErrorEnum.TUMBLER_RETRY_EXCEPTION, e.getMessage(), e);
    }
}
