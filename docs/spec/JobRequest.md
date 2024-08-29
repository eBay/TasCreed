# Job Request

The job request triggers TasCreed server to submit a TasCreed job instance, the params and configs in job request will overwrite the default values in job define.

## Job Request

Job request sample
```
{
  "jobName": "sample",
  "trigger": "20210104-05",
  "priority": 10,
  "params": {
    "k1": "v3"
  },
  "steps": [...]
}
```

| Field Name | Description | Type | Mandatory | Default Value |
| ----- | ----- | ----- | ----- | ----- |
| jobName | the name of job to be triggered. | `string` | Yes | |
| trigger | the trigger id of the triggered job instance, should be unique for the same job define. | `string` | Yes | |
| priority | the priority of the triggered job, overwrite the value in job define. | `int` | No | |
| params | the parameters of job level, overwrite the value in job define. | `map<string, string>` | No | |
| steps | the step requests of job request | `list<StepRequest>` | No | |

## Step Request

Step request sample
```
{
  "stepName": "prev",
  "ignore": true,
  "affinityRule": "",
  "params": {
    "k1": "v4",
    "k2": "v5"
  },
  "shardConf": {
    "shard": 6
  }
}
```

| Field Name | Description | Type | Mandatory | Default Value |
| ----- | ----- | ----- | ----- | ----- |
| stepName | the name of step to overwrite params and config values, TasCreed server find the step by name. | `string` | Yes | |
| exeClass | the executor class name, overwrite the value in step define. | `string` | No | |
| ignore | if true, and the step define is `ignorable`, then this step will be ignored in this triggered job. | `boolean` | No | `false` |
| affinityRule | task pick affinity rule name, overwrite the value in step define. | `string` | No | |
| params | the parameters of step level, overwrite the value in job define. | `map<string, string>` | No | |
| shardConf | available for shard mode step, overwrite the shard config in step define. | `StepShardConf` | No | |
| packConf | available for pack mode step, overwrite the pack config in step define. | `StepPackConf` | No | |
