package com.ebay.magellan.tascreed.core.domain.state.basic;

/**
 * indicates a job/step/task result
 * unknown: not a certain result
 * success: a certain result which means an expected success
 * failed: a certain result which means an expected failed
 * error: a certain result which means an exception
 */
public enum ResultEnum {
    UNKNOWN,
    SUCCESS,
    FAILED,
    ERROR,
    ;
}
