package com.ebay.magellan.tascreed.core.infra.routine.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RoutineExecs {
    RoutineExec[] value();
}
