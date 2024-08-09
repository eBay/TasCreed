package com.ebay.magellan.tascreed.depend.common.exception;

import lombok.Getter;

@Getter
public enum TcErrorEnum {

    // -- ignore-able exception start from 100XXX

    TC_IGNORE_EXCEPTION(100000, "TC_IGNORE_EXCEPTION", TcErrorTypeEnum.IGNORE),
    TC_ERROR_ENUM(100100, "TC_ERROR_ENUM", TcErrorTypeEnum.IGNORE),
    TC_IGNORE_TASK_EXCEPTION(100200, "TC_IGNORE_TASK_EXCEPTION", TcErrorTypeEnum.IGNORE),
    TC_IGNORE_BIZ_EXCEPTION(100900, "TC_IGNORE_BIZ_EXCEPTION", TcErrorTypeEnum.IGNORE),

    // -- retry-able exception start from 200XXX

    TC_RETRY_EXCEPTION(200000, "TC_RETRY_EXCEPTION", TcErrorTypeEnum.RETRY),
    TC_RETRY_TASK_EXCEPTION(200200, "TC_RETRY_TASK_EXCEPTION", TcErrorTypeEnum.RETRY),
    TC_RETRY_BIZ_EXCEPTION(200900, "TC_RETRY_BIZ_EXCEPTION", TcErrorTypeEnum.RETRY),

    // -- non-retry-able exception start from 300XXX

    TC_NON_RETRY_EXCEPTION(300000, "TC_NON_RETRY_EXCEPTION", TcErrorTypeEnum.NON_RETRY),
    TC_NON_RETRY_TASK_EXCEPTION(300200, "TC_NON_RETRY_TASK_EXCEPTION", TcErrorTypeEnum.NON_RETRY),
    TC_NON_RETRY_HEARTBEAT_EXCEPTION(300201, "TC_NON_RETRY_HEARTBEAT_EXCEPTION", TcErrorTypeEnum.NON_RETRY),
    TC_NON_RETRY_BIZ_EXCEPTION(300900, "TC_NON_RETRY_BIZ_EXCEPTION", TcErrorTypeEnum.NON_RETRY),

    // -- fatal exception start from 400XXX

    TC_FATAL_EXCEPTION(400000, "TC_FATAL_EXCEPTION", TcErrorTypeEnum.FATAL),
    TC_FATAL_CONFIG_EXCEPTION(400100, "TC_FATAL_CONFIG_EXCEPTION", TcErrorTypeEnum.FATAL),
    TC_FATAL_VALIDATION_EXCEPTION(400101, "TC_FATAL_VALIDATION_EXCEPTION", TcErrorTypeEnum.FATAL),
    TC_FATAL_DUTY_EXCEPTION(400102, "TC_FATAL_DUTY_EXCEPTION", TcErrorTypeEnum.FATAL),
    TC_FATAL_JOB_EXCEPTION(400200, "TC_FATAL_JOB_EXCEPTION", TcErrorTypeEnum.FATAL),
    TC_FATAL_TASK_EXCEPTION(400201, "TC_FATAL_TASK_EXCEPTION", TcErrorTypeEnum.FATAL),
    TC_FATAL_BIZ_EXCEPTION(400900, "TC_FATAL_BIZ_EXCEPTION", TcErrorTypeEnum.FATAL),

    // -- unknown exception start from 500XXX
    TC_UNKNOWN_EXCEPTION(500000, "TC_UNKNOWN_EXCEPTION", TcErrorTypeEnum.FATAL);

    // -----

    private final int errorId;
    private final String errorMessage;
    private final TcErrorTypeEnum errorType;

    TcErrorEnum(int errorId, String message, TcErrorTypeEnum errorType) {
        this.errorId = errorId;
        this.errorMessage = message;
        this.errorType = errorType;
    }

    public String getDesc() {
        return String.format("[%d, %s]", getErrorId(), getErrorMessage());
    }

}
