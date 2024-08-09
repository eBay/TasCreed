package com.ebay.magellan.tascreed.core.infra.executor.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TaskExecs {
    TaskExec[] value();
}
