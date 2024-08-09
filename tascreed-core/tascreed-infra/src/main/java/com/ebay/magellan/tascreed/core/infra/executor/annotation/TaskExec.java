package com.ebay.magellan.tascreed.core.infra.executor.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(TaskExecs.class)
public @interface TaskExec {
    // job name
    String job();
    // list of step names
    String[] step() default {};
}
