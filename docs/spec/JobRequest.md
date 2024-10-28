# Job Request

When job define is ready, users can trigger a job instance by job request. 

## Example

``` json
{
  "jobName": "sample",
  "trigger": "20210104-05",
  "priority": 10,
  "params": {
    "k1": "v3"
  },
  "steps": [
    {
      "stepName": "prep",
      "ignore": true
    }, {
      "stepName": "calc",
      "shardConf": {
        "shard": 10
      }
    }
  ]
}
```

## Job request

Generally, a job request can simply contain two parts:

- `jobName`: unique name of the job define, TasCreed finds a job by it
- `trigger`: ID of this trigger, should be unique for the same job define

*The pair of `jobName` and `trigger` in a `Job` should be globally unique.*

Other optional fields are used to overwrite the job define for this specific job:

- `priority`: overwrite the priority for this job
- `params`: update or provide more params for this job
- `steps`: overwrite the step defines for this job
- `traits`: modify the traits for this job
- `after`: specify the time to start task creation of this job
- `duration`: overwrite the expected execution time of this job

### Trait

<div class="grid" markdown>

``` json title="sample of modify traits"
{
  "traits": {
    "enable": ["CAN_IGNORE"],
    "disable": ["CAN_FAIL"]
  }
}
```

!!! note "Modify traits"
    - `enable`: enable some traits for this job
    - `disable`: disable some traits for this job

</div>

### After

<div class="grid" markdown>

``` json title="sample of job start time"
{
  "after": {
    "timeString": "2024-10-28 10:00:00",
    "timePattern": "yyyy-MM-dd HH:mm:ss",
    "timeZone": "UTC"
  }
}
```

!!! note "Specify start time"
    - `timeString`: time in string format
    - `timePattern`: pattern of the time string, by default is `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'`
    - `timeZone`: time zone, by default is `UTC`
    - `timestamp`: timestamp in numeric format; if this field is set, skip the other fields

</div>

## Step request

It is usually unnecessary to provide `StepRequest` in `JobRequest`, unless you want to overwrite some steps for this job.

- `stepName`: used to identify the step to overwrite
- `exeClass`: overwrite the task executor class full name for this step
- `affinityRule`: overwrite the affinity rule name for this step
- `ignore`: if true, and the step define is `ignorable`, then this step will be ignored in this job
- `traits`: modify the traits for this step
- `params`: update or provide more params for this step
- `after`: specify the time to start task creation of this step
- `duration`: overwrite the expected execution time of this step
- `maxPickTimes`: overwrite max times to pick a task of the step
- `shardConf`: available for `SHARD` mode step, overwrite the shard config for this step
- `packConf`: available for `PACK` mode step, overwrite the pack config for this step
