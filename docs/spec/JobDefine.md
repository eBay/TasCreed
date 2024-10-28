# Job Define

Usually we start from a job define, to describe what we want to do.

## Where to define jobs

A job define is stored in a file, which is 

- placed in folders configured by `tascreed.define.dirs`
- suffixed with `.json`
- visited through `classpath:/<dir>/<file>.json`

TasCreed application loads all the job define files when starting up, after parsing and validating, valid job defines are registered into memory.

## How to define a job

Each job define file describes a job template, to define how it works.  

Example

``` json
{
  "jobName": "sample",
  "version": 1,
  "priority": 10,
  "uniqueAliveInstance": true,
  "params": {
    "k1": "v1",
    "k2": "v2"
  },
  "steps": [
    {
      "stepName": "prep"
    }, {
      "stepName": "calc",
      "stepType": "SHARD",
      "shardConf": {
        "shard": 4
      },
      "dependentStep": "prep"
    }
  ]
}
```

### Job define

Generally, a job can be simply defined by two parts:

- `jobName`: unique name of the job define, TasCreed finds a job by it
- `steps`: list of step defines, to describe the steps and their dependencies

Other optional fields are used to impact the job scheduling and execution:

- `version`: version of the job define, not in use now
- `priority`: priority of the job define, inherited by job instances and tasks; the larger number the higher priority
- `uniqueAliveInstance`: if true, only one alive job instance can exist at any time
- `params`: [parameters](Params.md) at job level, inherited by all job instances
- `traits`: traits of the job define, inherited by job instance
- `duration`: expected execution time of a job, inherited by job instance

### Step define

A step can also be simply defined by some part:

- `stepName`: unique name of the step define within the job define, TasCreed finds a step by it
- `stepType`: indicates the step work mode
    + `SIMPLE`: default step type, only one task can be created
    + `SHARD`: several tasks can be created in sharding mode
    + `PACK`: several tasks can be created in pack mode
- `dependentStep`: dependent step name of the step, meaning the step can start only after the dependent step done; deprecated, use `dependency` instead
- `dependency`: describe the dependency information of the step
    + `doneSteps`: name list of dependent steps, only when all these steps done can this step start
    + `phase`: belonged phase of this step

*`dependentStep` can only define one dependent step, while `dependency` can define multiple dependent steps.*

Other optional fields are used to impact the step scheduling and execution:

- `exeClass`: task executor class name of the step, to overwrite the registered executor
- `affinityRule`: affinity rule of the step, to guide task selection of each node
- `effort`: effort of the step, used for progress percentage calculation of the whole job
- `traits`: traits of the step, inherited by tasks
- `ignorable`: if true, the step can be ignored if the step is set `ignore` in job request; deprecated, use `traits` instead
- `duration`: expected execution time of a step
- `params`: [parameters](Params.md) at step level, inherited by all tasks of the step

#### Simple mode step

<div class="grid" markdown>

``` json title="sample of simple mode step" hl_lines="3"
{
  "stepName": "prep",
  "stepType": "SIMPLE",
  "dependentStep": "first",
  "params": {
    "k1": "v11", 
    "k2": "v22"
  }
}
```

``` mermaid
block-beta
  T["prep<br>(simple task)"]
```

</div>

*If `stepType` not set, it is `SIMPLE` mode by default.*

#### Shard mode step

<div class="grid" markdown>

``` json title="sample of shard mode step" hl_lines="3-6"
{
  "stepName": "calc",
  "stepType": "SHARD",
  "shardConf": {
    "shard": 5
  }
}
```

``` mermaid
block-beta
  columns 2
  T0["calc.shard-0<br>(total: 5, index: 0)"]
  T1["calc.shard-1<br>(total: 5, index: 1)"]
  T2["calc.shard-2<br>(total: 5, index: 2)"]
  T3["calc.shard-3<br>(total: 5, index: 3)"]
  T4["calc.shard-4<br>(total: 5, index: 4)"]
```

</div>

The shard mode configuration is defined in `shardConf` field.

- `shard`: the total sharding number, larger than `0`, recommended to be less than `100`
- `startShardId`: the start shard id of created shard tasks, by default is `0`
- `maxTaskCount`: the max number of alive tasks of this step at the same time, by default is `50`

#### Pack mode step

<div class="grid" markdown>

``` json title="sample of pack mode step" hl_lines="3-8"
{
  "stepName": "calc",
  "stepType": "PACK",
  "packConf": {
    "size": 100,
    "start": 0,
    "end": 925
  },
  "dependentStep": "prep"
}
```

``` mermaid
block-beta
  columns 2
  T0["calc.pack-0<br>(id: 0, start: 0, end: 99)"]
  T1["calc.pack-1<br>(id: 1, start: 100, end: 199)"]
  T2["calc.pack-2<br>(id: 2, start: 200, end: 299)"]
  T3["calc.pack-3<br>(id: 3, start: 300, end: 399)"]
  T4["calc.pack-4<br>(id: 4, start: 400, end: 499)"]
  T5["calc.pack-5<br>(id: 5, start: 500, end: 599)"]
  T6["calc.pack-6<br>(id: 6, start: 600, end: 699)"]
  T7["calc.pack-7<br>(id: 7, start: 700, end: 799)"]
  T8["calc.pack-8<br>(id: 8, start: 800, end: 899)"]
  T9["calc.pack-9<br>(id: 9, start: 900, end: 925)"]
```

</div>

The pack mode configuration is defined in `packConf` field.

- `size`: the size of each pack, larger than `0`
- `start`: the start point of the whole range, included, should not be negative
- `end`: the end point of the whole range, included, not needed if the step is infinite
- `infinite`: if true, the whole range has no end point, the step will create pack tasks infinitely
- `startPackId`: the start pack id of created pack tasks, by default is `0`
- `maxTaskCount`: the max number of alive tasks of this step at the same time, by default is `50`

As a special usage, an infinite pack mode step can create tasks infinitely. A sample usage is to consume an endless data stream.

<div class="grid" markdown>

``` json title="sample of infinite pack mode step" hl_lines="3-8"
{
  "stepName": "consume",
  "stepType": "PACK",
  "packConf": {
    "size": 100,
    "start": 0,
    "infinite": true
  },
  "dependentStep": "prep"
}
```

``` mermaid
block-beta
  columns 2
  T0["consume.pack-0<br>(id: 0, start: 0, end: 99)"]
  T1["consume.pack-1<br>(id: 1, start: 100, end: 199)"]
  T2["consume.pack-2<br>(id: 2, start: 200, end: 299)"]
  T3["consume.pack-3<br>(id: 3, start: 300, end: 399)"]
  T4["consume.pack-4<br>(id: 4, start: 400, end: 499)"]
  T5["consume.pack-5<br>(id: 5, start: 500, end: 599)"]
  ...
```

</div>

