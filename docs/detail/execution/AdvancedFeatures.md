# Advanced Features

## Specify step executor

The task executor can be registered to a step:

- in code (by `@TaskExec` annotation or explicitly call the `registerTaskExecutor` method of the `TaskExecutorRegistry`), 
- or specify in job define,
- or overwrite in job request.

The overwrite level is from low to high.

This feature can be used to overwrite the executor for a specific run.

## Job define directories

The job defines are loaded when application starts up, from the configured directories in `tascreed.define.dirs`, the directories can be separated by `,`.

These directory paths are prefixed by `classpath:/`, all the files with `.json` suffix in the directories will be loaded.  
Therefore, the job define directories can locate in the places that springboot can load resources from.

## Priority

Users can specify the priority of a job define, or overwrite it in job request, the tasks of higher prioritized jobs will be picked up first.

*The priority is an integer, the larger number the higher priority.*

Similarly, the routine define can also be prioritized.

## Ignorable Step

In step define, users can specify the step is ignorable or not, by default not.

Example of ignorable step define:
``` json
{
  "jobName": "job1",
  "steps": [
    {
      "stepName": "step1",
      "traits": ["ignorable"]
    },
    ...
  ]
}
```

An ignorable step can be ignored in job request, like this:
``` json
{
  "jobName": "job1",
  "steps": [
    {
      "stepName": "step1",
      "ignore": true
    }
  ]
}
```

Then the step of this job instance will be set as `IGNORED` state, with a `SUCCESS` result.

## Long run pack mode

This step mode is used for the parallel consumption of an endless data stream. For example,
``` json
{
  "jobName": "job1",
  "steps": [
    {
      "stepName": "step1",
      "stepType": "PACK",
      "packConf": {
        "size": 100,
        "start": 1000,
        "infinite": true
      }
    }
  ]
}
```

The `infinite` flag indicates a long run pack mode step, the packs will be generated continuously, without an end offset.
```
1000-1099, 1100-1199, 1200-1299, ...
```

Then each pack task can consume the data stream by the pack range, in parallel.

## Unique alive job

By default, multiple job instances of a job define can be executed at the same time.  
But in some scenarios, we need to ensure only one job instance is alive at the same time, then the `uniqueAliveInstance` flag can be set in job define.

For example,
``` json
{
  "jobName": "job1",
  "uniqueAliveInstance": true,
  ...
}
```

Then users can trigger at most one job instance of the job define at the same time.

## Task checkpoint

`CheckpointTaskExecutor` can persist the checkpoint in bulletin, then the task can continue from the checkpoint if it recovers from failure, such as node restart.

<div class="annotate" markdown>

To leverage this feature, users need to: 

- implement `TaskExecCheckpoint` interface for the checkpoint serialization & deserialization
- implement `CheckpointTaskExecutor` for the task executor
- implement `executeRoundImpl` method in the task executor, the checkpoint will be updated after each round of execution (1)

</div>

1. In contrast, users need to implement `executeImpl` method for `NormalTaskExecutor`

## Execution timeout metric

Users can set the expected execution time for a job or step, using the `duration` field (in milliseconds) in the job/step define, or overwrite in the job request.  

``` json
{
  "jobName": "job1",
  "duration": 300000,
  ...
}
```

If the alive job/task execution time exceeds the duration, the metric can be reported by TasCreed infra, to tell the users there are some slow jobs or tasks.

## Others

### Affinity rule

Dispatch task executor to worker node with specific affinity rule, it is useful in specific environment, but not generic or configurable enough in the open source version. Need to be enhanced.