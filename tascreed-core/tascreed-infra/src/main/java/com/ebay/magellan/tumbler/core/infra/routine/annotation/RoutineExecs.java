package com.ebay.magellan.tumbler.core.infra.routine.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RoutineExecs {
    RoutineExec[] value();
}
