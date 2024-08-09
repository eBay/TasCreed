package com.ebay.magellan.tascreed.core.infra.controller;

import com.ebay.magellan.tascreed.depend.common.exception.TumblerErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TumblerException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public Object TumblerExceptionHandler(TumblerException e){
        e.printStackTrace();
        TumblerErrorEnum error = e.getError();
        String msg = e.getMessage();
        return new ErrorResponse(error.getErrorId(), error.getErrorMessage(), msg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public Object exceptionHandler(Exception e){
        e.printStackTrace();
        TumblerErrorEnum error = TumblerErrorEnum.TUMBLER_UNKNOWN_EXCEPTION;
        String msg = e.getMessage();
        return new ErrorResponse(error.getErrorId(), error.getErrorMessage(), msg);
    }
}