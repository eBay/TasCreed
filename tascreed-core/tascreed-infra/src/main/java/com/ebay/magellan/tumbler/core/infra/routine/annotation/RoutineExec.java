package com.ebay.magellan.tumbler.core.infra.routine.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(RoutineExecs.class)
public @interface RoutineExec {
    // routine name
    String routine();

    // scale
    int scale() default 1;

    // priority
    int priority() default 1;

    // interval, default 1 min
    long interval() default 60 * 1000L;
}
