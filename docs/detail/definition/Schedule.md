# Schedule

Jobs are triggered by job requests from users or other systems. What if we want to trigger a job at a specific time, or periodically?

Scheduling is such a feature to trigger jobs as predefined time points or periods, automatically.

## Workflow

``` mermaid
flowchart LR
	SD["Schedule Define"] -->|generate when<br> condition meets| TR["Trigger"]
	TR -->|submit| JR["Job Request"]
	JR -->|build| JB["Job"]
```

When schedule triggers, a job request is generated and submitted to the job system.

## Example

Let's define a schedule like this:

``` json
{
  "scheduleName": "schedule-test",
  "jobRequest": {
    "jobName": "sample",
    "trigger": "test",
    "params": {
      "p1": "${var1}",
      "p3": "date-${var3}"
    },
    "steps": [
      {
        "name": "prep",
        "ignore": false,
        "params": {
          "p2": "${var2}"
        }
      }, {
        "name": "calc",
        "ignore": false,
        "params": {
          "p2": "${var2}"
        }
      }, {
        "name": "aggr-1",
        "traits": {
          "disable": ["ARCHIVE"]
        }
      }
    ]
  },
  "conf": {
    "type": "period",
    "intervalMs": 600000
  },
  "variables": {
    "var1": {
      "type": "const",
      "value": "str1"
    },
    "var2": {
      "type": "count",
      "next": 2
    },
    "var3": {
      "type": "time",
      "pattern": "yyyyMMdd",
      "zone": "UTC"
    }
  }
}
```

## Job request

The `jobRequest` field defines the job request template, which is used to generate a job request each time triggered.

!!! annotate note "Distinguish triggers"
	To distinguish triggers, the job requests can differ in some fields, by replacing some fields with the trigger time.

	- `trigger` field in job request will be suffixed by the trigger time (UTC) in format of `-yyyyMMddHHmmss` (1)
  	- the `${xxx}` format string in the param values can be replaced by variables, both job params and step params (2)

1. For example, the schedule is triggered at `2023-02-10T11:00:00.000Z`, the `trigger` field of the job request is `test-20230210110000`
2. For example, `${var2}` can be replaced by the value of `var2`, like `5`, `${var3}` can be replaced by trigger time in format of `20230210`

## Schedule config

The `conf` field defines the schedule config, indicating the time-based schedule information.

- `point`: list of time points to trigger
- `period`: periodically trigger, within an optional time range
- `cron`: cron expression described trigger time, within an optional time range

Examples

``` json
// point type, trigger at 3 time points
{
  "type": "point",
  "points": ["2023-02-10T11:00:00.000Z", "2023-02-11T11:00:00.000Z", "2023-02-12T11:00:00.000Z"]
}

// period type, from a start date, every 10 minutes
{
  "type": "period",
  "intervalMs": 600000,
  "startDate": "2023-02-10T11:00:00.000Z"
}

// cron type, from a start date, every 10 minutes
{
  "type": "cron",
  "cron": "0 0/10 * * * ?",
  "startDate": "2023-02-10T11:00:00.000Z"
}
```

## Variable

The `variables` field defines the variables calculated for each trigger, used to replace param values in job request.

- `const`: constant string for direct replacement
- `count`: counter number, increase after replacement
- `time`: time string of trigger time for replacement

Examples

``` json
// const type, value is constant
{
  "type": "const",
  "value": "my-string"
}

// count type, start from 2
{
  "type": "count",
  "next": 2
}

// time type, trigger time in string format
{
  "type": "time",
  "pattern": "yyyyMMdd",
  "zone": "UTC"
}
```
