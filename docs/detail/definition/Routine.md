# Routine

Routine introduces a new kind of executor, working as a long run job across the whole cluster, round and round again.

When TasCreed cluster starts up, the routines will be occupied and executed automatically.

Some system routines are defined to work as

- monitor: collect and export whole cluster status
- job watcher: update job status and create new tasks
- schedule watcher: check schedule trigger condition and trigger jobs

## Define a routine

Routine executor can be implemented based on two types of parent classes

- `NormalRoutineExecutor`
- `CheckpointRoutineExecutor`, can persist checkpoint in bulletin, for failure recovery

Normal routine executor is usually enough.

A sample routine executor is defined as below:

``` java
@RoutineExec(routine="simple", scale = 2, interval = 15 * 1000L)
@Component
@Scope("prototype")
public class SimpleRoutineExecutor extends NormalRoutineExecutor {

    int i = 0;

    @Override
    protected void initImpl() throws TcException {
        System.out.println(String.format("routine [%s] init", routine.getFullName()));
    }

    @Override
    protected void executeRoundImpl() throws TcException {
        try {
            System.out.println("===== routine executor run round begins =====");
            i++;

            System.out.println(String.format("routine [%s] running %d", routine.getFullName(), i));

            System.out.println("===== routine executor run round ends =====");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void closeImpl() throws TcException {
        System.out.println(String.format("routine [%s] close", routine.getFullName()));
    }

}
```

The routine executor needs to implement the methods:

- `initImpl`: invoked when executor starts
- `executeRoundImpl`: invoked in a while loop, with a configured interval
- `closeImpl`: invoked when executor ends

The annotation part is introduced in the [document of annotation](Annotation.md#routine-executor).

## Configure the routine resources

### Configure routine threads

``` yaml
tascreed:
  routine:
    # num of overall available routines
    max.count.overall:
      key: "%s/max_routine_count_overall"
      default: 20
    # num of available routine thread per host
    max.count.per.host:
      key: "%s/max_routine_count_per_host"
      default: 5
```

This is the default configuration of routine threads

- whole cluster can run at most `20` routine threads, which can be overwritten by the properties file or config bulletin
- each TasCreed node can run at most `5` routine threads, which can be overwritten by the properties file or config bulletin

### Configure routine parameters

The routine parameters are initially configured in the code, they can also be overwritten by properties file.

``` properties
# tascreed routine param
tascreed.routine.param.job-watcher.scale=1
tascreed.routine.param.job-watcher.interval=10000
tascreed.routine.param.monitor.interval=5000
tascreed.routine.param.monitor.priority=101
```

The format of the parameter configuration is like `tascreed.routine.param.<routineName>.<paramName>`.

## Ban routines

To enable or disable the routines, you can refer to the [document of ban feature](../execution/BanJobs.md#ban-routines).
