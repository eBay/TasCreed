# Schedule

With the feature of schedule, job defines can be triggered by Tumbler itself, as user scheduled, e.g. trigger at a specific time, every two hours, daily, weekly, etc.

## Time schedule

**from 0.3.5-SNAPSHOT**

The job define can be triggered when the scheduled time comes. Here introduces a new concept `Schedule`, users can submit a schedule of a job request, with the config of time schedule.

Across the whole Tumbler cluster, a routine thread runs to submit job requests when the related schedule condition meets.

An example of schedule could be like this:
```json
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

- scheduleName: the unique name of schedule, it should not conflict
- jobRequest: the job request pattern to be scheduled, please refer to the [Job Request](JobRequest.md) doc for detailed introduction; however, some fields will be replaced when triggered as instance of job request:
	+ trigger: the instance trigger will be suffixed by trigger time in format of `-yyyyMMddHHmmss`; e.g. the schedule job request trigger is `test`, a triggered instance can be `test-20230210110000`
	+ params: the `${xxx}` format string in the param value can be replaced by variables when triggered, including the job params and step params; e.g. if a variable `var1` is calculated as `123`, then the param value `abc${var1}` can be replaced to `abc123`
- conf: config of time schedule
- variables: map of string to variable object, key is the variable name, value describes the variable

### Schedule config types
- point: list of time points to trigger
- period: periodically trigger, within an optional time range
- cron: cron expression described trigger time, within an optional time range

examples
```json
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

### Variable types
- const: constant string for direct replacement
- count: counter number, increase after replacement
- time: time string of trigger time for replacement

examples
```json
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
