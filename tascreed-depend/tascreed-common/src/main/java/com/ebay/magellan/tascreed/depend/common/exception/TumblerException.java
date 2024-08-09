package com.ebay.magellan.tascreed.depend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TumblerException extends Exception {

    private TumblerErrorEnum error;
    private String shortMessage;

    public TumblerException(TumblerErrorEnum error, String shortMessage, Throwable cause) {
        super(cause);
        this.setShortMessage(shortMessage);
        this.error = error;
    }

    public boolean isIgnore() {
        if (error == null || error.getErrorType() == null) return false;
        return error.getErrorType().isIgnore();
    }

    public boolean isRetry() {
        if (error == null || error.getErrorType() == null) return false;
        return error.getErrorType().isRetry();
    }

    public boolean isNonRetry() {
        if (error == null || error.getErrorType() == null) return false;
        return error.getErrorType().isNonRetry();
    }

    public boolean isFatal() {
        if (error == null || error.getErrorType() == null) return false;
        return error.getErrorType().isFatal();
    }

    @Override
    public String getMessage() {
        String type = error != null ? error.getDesc() : "[]";
        String msg = super.getMessage();
        String errMsg = StringUtils.isNotBlank(msg) ? msg : shortMessage;
        return String.format("%s %s", type, errMsg);
    }
}
