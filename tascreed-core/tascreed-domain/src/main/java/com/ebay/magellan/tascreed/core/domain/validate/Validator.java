package com.ebay.magellan.tascreed.core.domain.validate;

public interface Validator<T> {

    ValidateResult validate(T t);

}
