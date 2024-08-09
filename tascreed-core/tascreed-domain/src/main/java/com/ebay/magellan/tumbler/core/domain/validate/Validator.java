package com.ebay.magellan.tumbler.core.domain.validate;

public interface Validator<T> {

    ValidateResult validate(T t);

}
