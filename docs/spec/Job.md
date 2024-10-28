# Job

Job is created from a job define, triggered by a job request.

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
      "stepName": "prep"
    }, {
      "stepName": "calc",
      "stepType": "SHARD",
      "shardConf": {
        "shard": 10
      }
    }
  ]
}
```

## Job

Job is identified by

- `jobName`: unique name of job define
- `trigger`: trigger ID of job request, unique for the same job define

Other fields are inherited from job define, or updated by job request.

## Step

Step is identified by

- `stepName`: unique name of step within the job

Other fields are inherited from step define, or updated by step request.

???+ question "Why there is no dependency information in steps?"
    The dependency information is defined in job define, should not be overwritten in job request.  
    TasCreed reads the dependency information from the job define, so it is optional to record in each job instance.  
    
    However, there could be an exception that, if the dependency is changed, the previously unfinished job will use the changed dependency, which is unexpected.   
    This should be fixed in future versions. 

## Task

Example

``` json
{
  "jobName": "sample",
  "trigger": "20210104-05",
  "stepName": "calc",
  "priority": 10,
  "params": {
    "k1": "v3"
  },
  "stepType": "SHARD",
  "shardConf": {
    "total": 10,
    "index": 3
  },
  "dependentStepStates": {
    "prep": "SUCCESS"
  }
}
```

Task is created from a step, identified by

- `jobName`
- `trigger`
- `stepName`
- other fields determined by step modes
    + `SIMPLE` mode, `stepName` is enough to identify
    + `SHARD` mode, shard `index` is used to identify
    + `PACK` mode, pack `id` is used to identify

Other fields are inherited from step, or updated by step results.
