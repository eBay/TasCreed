# Annotation

**from: 0.3.1-RELEASE**

## TaskExec

In the previous version of TasCreed, a task executor can be only registered explicitly in code like this:
```
taskExecutorRegistry.registerTaskExecutor("sample-dag", "calc-1", ExceptionTaskExecutor.class);
```

Now with the `TaskExec` annotation, we can simply register the task executor class by the annotation.

Sample code
```
@TaskExec(job="sample", step={"prep", "calc", "aggr-1", "aggr-2"})
@TaskExec(job="sample-infinite", step="calc")
@TaskExec(job="sample-dag", step={"prep", "calc-1", "calc-2", "calc-3",
        "aggr-1", "aggr-2", "final-11", "final-12", "final-21"})
@Setter
@Component
@Scope("prototype")
public class DefaultTaskExecutor extends NormalTaskExecutor {
	...
}
```

One task executor class can be registered to mapping with multiple steps, even in different jobs, so the `TaskExec` annotation can be also registered multiple times.

The params of `TaskExec`
- job: the mapping job name
- step: list of the mapping step names

The annotation actually is scanned by the spring boot, so the `Component` and `Scope("prototype")` annotations are also necessary for the task executors.

We also keeps the old way to register task executors, which can be used to overwrite the default configuration by annotation, for debug or test purpose. And the old code is compatible.

## RoutineExec

The `RoutineExec` works for the feature of routine executor.

You can refer to the [Routine](Routine.md) to check out the usage of this annotation.