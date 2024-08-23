# Trait

**from 0.2.6-SNAPSHOT**

Trait describes a character of a TasCreed data object, each object can have arbitrary number of traits.

## Trait Type
Each trait should be categorized to one type, and each type of trait can be configured on multiple kinds of data objects.

| Trait type | Valid data objects |
| ----- | ----- |
| `JOB_DEFINE` | `JOB_DEFINE` |
| `STEP_DEFINE` | `STEP_DEFINE` |
| `JOB` | `JOB`, `JOB_DEFINE` |
| `STEP` | `STEP`, `STEP_DEFINE` |
| `TASK` | `TASK`, `STEP`, `STEP_DEFINE` |

For example, a trait with type `STEP` can be configured on a step or a step define. `CAN_FAIL` trait is a `STEP` type trait, so it can be affect both step and step define.  

In the step define, it can be configured like this:
```
{
  "stepName": "calc-2",
  "dependency": {
    "steps": ["prep"]
  },
  "traits": ["canFail"]
}
```

When the step is created, `CAN_FAIL` trait is inheritted:
```
{
  "stepName": "calc-2",
  "traits": ["canFail"]
}
```

But when the task is created, `CAN_FAIL` trait will not be seen:
```
{
  "stepName": "calc-2"
}
```

Because the task worker doesn't care if the task can fail or not, only when we want to figure out the step result state, the task failure affects.

## Trait
There are 4 traits defined. A trait can be configured by name or aliases.

### CAN_IGNORE
- type: `STEP_DEFINE`
- aliases: `canIgnore`, `can-ignore`, `ignorable`
- usage: the step can be ignored; only if this step is explicitly ignored by the job request, the step will be ignored.

### DELETED
- type: `JOB`
- aliases: `deleted`
- usage: the job is deleted; this trait can not be configured by user

### CAN_FAIL
- type: `STEP`
- aliases: `canFail`, `can-fail`
- usage: the step can be failed; if the step final state is `FAILED`, it will be set as `ACCEPTABLE_FAILED`, with the result seen as a `SUCCESS`, and the following steps will not be affected.

### ARCHIVE
- type: `TASK`
- aliases: `archive`
- usage: the task will be archived when it finishes; by default the finished tasks will be directly dropped, but some important tasks can be configured to be archived by this trait.
