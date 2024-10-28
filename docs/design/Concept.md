# Concept

``` mermaid
erDiagram
    JobDefine {
        string jobName
        StepDefine[] steps
    }
    
    StepDefine {
        string stepName
        string[] dependentSteps
    }
    
    JobRequest {
        string jobName
        string trigger
    }
    
    Job {
        string jobName
        string trigger
        StepDefine[] steps
    }
    
    Step {
        string stepName
    }
    
    Task {
        string jobName
        string trigger
        string stepName
    }

    JobDefine ||--o{ StepDefine : has
    Job ||--o{ Step : has
    
    JobRequest ||--|| Job : triggers
    JobDefine ||--o{ Job : instantiates
    StepDefine ||--o{ Step : instantiates
    
    Step ||--|{ Task : generates
```

- `JobDefine`: definition of job, containing `StepDefine`s
- `StepDefine`: definition of step, with dependency of other steps
- `JobRequest`: request to trigger a `Job`
- `Job`: instance of a `JobDefine`, triggered by a `JobRequest`
- `Step`: instance of a `StepDefine`, contained by a `Job`
- `Task`: execution unit of a `Step`

## Job Define

`JobDefine` is pre-defined as job template, identified by `jobName`. e.g.

``` json
{
  "jobName": "balance-report",
  "steps": [...]
}
```

## Step Define

`StepDefine` is pre-defined as step template, identified by `stepName` within its belonged `JobDefine`. e.g.

``` json
{
  "stepName": "merge_sharding_files_and_upload",
  "sharding": 12,
  "dependentStep": "generate_files_and_upload_by_sharding"
}
```

## Job Request

`JobRequest` is submitted by user, to create a new job instance. e.g.

``` json
{
  "jobName": "balance-report",
  "trigger": "20191031",
  "steps": [
    "stepName": "balance-report-calculate",
    "params": {
      "param1": "value1",
      "param2": "value2"
    }
  ]
}
```

## Job

`Job` is created based on the `JobDefine`, triggered by `JobRequest`, identified by `jobName` and `trigger`. e.g.

``` json
{
  "jobName": "balance-report",
  "trigger": "20191113", 
  "steps": [...],
  "state": "SUCCESS"
}
```

## Step

`Step` is created based on the `StepDefine`, identified by `stepName` within its belonged `Job`. e.g.

``` json
{
  "name": "balance-report-calculate",
  "state": "SUCCESS",
  "taskStates": {
    "3": "SUCCESS",
    "4": "SUCCESS",
    "2": "SUCCESS",
    "1": "SUCCESS"
  },
  "params": {
    "REQUEST": "..."
  }
}
```

## Task

`Task` is created based on the `Step`, globally identified by `jobName`, `trigger`, `stepName` and its partitioned information if any. e.g.

``` json
{
  "jobName": "balance-report",
  "trigger": "20200121200001",
  "stepName": "balance-report-calculate",
  "sharding": {
    "total": 12,
    "index": 9
  },
  "params": {
    "REQUEST": "{\"triggerId\":\"20200121200001\",\"reportDate\":\"2020-01-21\"}",
    "host": "9"
  },
  "createTime": "2020-01-23T04:14:44.900Z"
}
```
