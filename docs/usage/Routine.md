# Routine

**from: 0.3.1-RELEASE**

Routine is a new kind of executor, which is like a cluster level long run job, running a round and a round again, with a configured interval.

Not like a Tumbler job, users don't need to submit a routine to activate it, they just need to define the routines in the code, with the configured parameters. When the Tumbler nodes start up, they will try to occupy the routine job and start process.

At current, the Tumbler internal monitor thread and job watcher thread (to update job status and create new tasks) are refactored to be routines.

## How to define your own routine

The routine executor can be implemented based on two types of parent classes
- NormalRoutineExecutor
- CheckpointRoutineExecutor: with running state can be recorded in the bulletin

Usually, a normal routine executor is enough. The sample code defines a routine executor.

```java
@RoutineExec(routine="simple", scale = 2, interval = 15 * 1000L)
@Component
@Scope("prototype")
public class SimpleRoutineExecutor extends NormalRoutineExecutor {

    int i = 0;

    @Override
    protected void initImpl() throws TumblerException {
        System.out.println(String.format("routine [%s] init", routine.getFullName()));
    }

    @Override
    protected void executeRoundImpl() throws TumblerException {
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
    protected void closeImpl() throws TumblerException {
        System.out.println(String.format("routine [%s] close", routine.getFullName()));
    }

}
```

Similar to the task executor, the routine executor needs to implement some methods:
- initImpl: invoked when executor starts
- executeRoundImpl: invoked in a while loop, with a configured interval
- closeImpl: invoked when executor ends

The annotations are important.
```java
@RoutineExec(routine="simple", scale = 2, interval = 15 * 1000L)
@Component
@Scope("prototype")
```
- The `RoutineExec` defines a routine executor, and also defines a routine name, with some pre-defined parameters
	+ routine: string, routine name, mandatory
	+ scale: integer, default 1, indicates how many routine threads can be running in parallel across the whole cluster
	+ priority: integer, default 1, the routine with larger priority can be occupied in prioritize
	+ interval: long, in millisecond, default `60000` which is 1 minute, indicates the interval of the while loop to invoke the `executeRoundImpl` method
- The `Component` is the spring boot annotation, it is mandatory because
	+ The executors are constructed by spring boot
	+ The `RoutineExec` annotation implemention actually depends on the `Component` annotation
- The `Scope` is better to be defined as `@Scope("prototype")`

That's all, the routine is defined and will be registered by Tumbler, and it will be occupied and executed by the routine threads across the whole cluster.

## How to configure the routine threads across cluster

### Configuration of routine threads
```yaml
tumbler:
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
- whole cluster can run at most 20 routine threads, which can be overwritten by the properties file or etcd
- each Tumbler node can run at most 5 routine threads, which can be overwritten by the properties file or etcd

### Configure routine parameters
```properties
# tumbler routine param
tumbler.routine.param.job-watcher.scale=1
tumbler.routine.param.job-watcher.interval=10000
tumbler.routine.param.monitor.interval=5000
tumbler.routine.param.monitor.priority=101
```
The routine parameters are initially configured in the code, they can also be overwritten by properties file.

The format of the parameter configuration is like `{tumbler.routine.param}.{routineName}.{paramName}`, in this way, we can overwrite the default routine parameters as you like.

### Ban and resume routines

You can refer to the [doc of ban feature](BanJobs.md) to check out how to ban and resume routines.
