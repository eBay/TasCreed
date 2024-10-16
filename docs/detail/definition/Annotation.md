# Annotation

Users need to implement their own task executors, and register them to the framework.   
Annotation helps to register the executors more efficiently.

## Without annotation

Task executors can be registered explicitly in code like this:
``` java
taskExecutorRegistry.registerTaskExecutor("job1", "step1", MyTaskExecutor.class);
```

It should be registered at the initialization phase of the application, then the framework can find the executor for the specific step.

This way is more likely to be ignored by users, leading to unexpected exceptions.

## With annotation

Then we've introduced the annotation way to register task executors. For example,

``` java
@TaskExec(job="job1", step="step1")
@TaskExec(job="job2", step={"step2", "step3"})
@Component
@Scope("prototype")
public class MyTaskExecutor extends NormalTaskExecutor {
	...
}
```

- `job`: job name
- `step`: list of step names

The annotation describes which steps the task executor is bound to, during startup, the framework will register these executors automatically.

This way is much more convenient and less error-prone.

*`@Component` and `@Scope("prototype")` are both necessary for task executors, because the executors are scanned by spring boot.*

## Routine executor

Similarly, the routine executor can also be registered by annotation. For example,

``` java
@RoutineExec(routine="job-watcher", scale = 3, priority = 100, interval = 30 * 1000L)
@Component
@Scope("prototype")
public class JobWatcherRoutineExecutor extends NormalRoutineExecutor {
	...
}
```

- `routine`: routine name
- `scale`: the number of threads to run the routine, for high availability
- `priority`: priority of the routine
- `interval`: interval of the routine execution round