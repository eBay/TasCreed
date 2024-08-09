package com.ebay.magellan.tascreed.depend.common.exception;

import lombok.Getter;

@Getter
public enum TcErrorEnum {

    // -- ignore-able exception start from 100XXX

    TUMBLER_IGNORE_EXCEPTION(100000, "TUMBLER_IGNORE_EXCEPTION", TcErrorTypeEnum.IGNORE),
    TUMBLER_IGNORE_CONFIG_EXCEPTION(100100, "TUMBLER_IGNORE_CONFIG_EXCEPTION", TcErrorTypeEnum.IGNORE),
    TUMBLER_IGNORE_TASK_EXCEPTION(100200, "TUMBLER_IGNORE_TASK_EXCEPTION", TcErrorTypeEnum.IGNORE),
    TUMBLER_IGNORE_BIZ_EXCEPTION(100900, "TUMBLER_IGNORE_BIZ_EXCEPTION", TcErrorTypeEnum.IGNORE),

    // -- retry-able exception start from 200XXX

    TUMBLER_RETRY_EXCEPTION(200000, "TUMBLER_RETRY_EXCEPTION", TcErrorTypeEnum.RETRY),
    TUMBLER_RETRY_TASK_EXCEPTION(200200, "TUMBLER_RETRY_TASK_EXCEPTION", TcErrorTypeEnum.RETRY),
    TUMBLER_RETRY_BIZ_EXCEPTION(200900, "TUMBLER_RETRY_BIZ_EXCEPTION", TcErrorTypeEnum.RETRY),

    // -- non-retry-able exception start from 300XXX

    TUMBLER_NON_RETRY_EXCEPTION(300000, "TUMBLER_NON_RETRY_EXCEPTION", TcErrorTypeEnum.NON_RETRY),
    TUMBLER_NON_RETRY_TASK_EXCEPTION(300200, "TUMBLER_NON_RETRY_TASK_EXCEPTION", TcErrorTypeEnum.NON_RETRY),
    TUMBLER_NON_RETRY_HEARTBEAT_EXCEPTION(300201, "TUMBLER_NON_RETRY_HEARTBEAT_EXCEPTION", TcErrorTypeEnum.NON_RETRY),
    TUMBLER_NON_RETRY_BIZ_EXCEPTION(300900, "TUMBLER_NON_RETRY_BIZ_EXCEPTION", TcErrorTypeEnum.NON_RETRY),

    // -- fatal exception start from 400XXX

    TUMBLER_FATAL_EXCEPTION(400000, "TUMBLER_FATAL_EXCEPTION", TcErrorTypeEnum.FATAL),
    TUMBLER_FATAL_CONFIG_EXCEPTION(400100, "TUMBLER_FATAL_CONFIG_EXCEPTION", TcErrorTypeEnum.FATAL),
    TUMBLER_FATAL_VALIDATION_EXCEPTION(400101, "TUMBLER_FATAL_VALIDATION_EXCEPTION", TcErrorTypeEnum.FATAL),
    TUMBLER_FATAL_DUTY_EXCEPTION(400102, "TUMBLER_FATAL_DUTY_EXCEPTION", TcErrorTypeEnum.FATAL),
    TUMBLER_FATAL_JOB_EXCEPTION(400200, "TUMBLER_FATAL_JOB_EXCEPTION", TcErrorTypeEnum.FATAL),
    TUMBLER_FATAL_TASK_EXCEPTION(400201, "TUMBLER_FATAL_TASK_EXCEPTION", TcErrorTypeEnum.FATAL),
    TUMBLER_FATAL_BIZ_EXCEPTION(400900, "TUMBLER_FATAL_BIZ_EXCEPTION", TcErrorTypeEnum.FATAL),

    // -- unknown exception start from 500XXX
    TUMBLER_UNKNOWN_EXCEPTION(500000, "TUMBLER_UNKNOWN_EXCEPTION", TcErrorTypeEnum.FATAL);

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
