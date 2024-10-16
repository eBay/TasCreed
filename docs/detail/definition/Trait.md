# Trait

Traits describe the properties of TasCreed data objects, such as jobs, steps, etc. An object can have arbitrary number of traits.

## Trait Type

Each trait is categorized to a type, which can be configured on several kinds of data objects.

| Trait type    | Valid data objects            |
|---------------|-------------------------------|
| `JOB_DEFINE`  | `JOB_DEFINE`                  |
| `STEP_DEFINE` | `STEP_DEFINE`                 |
| `JOB`         | `JOB`, `JOB_DEFINE`           |
| `STEP`        | `STEP`, `STEP_DEFINE`         |
| `TASK`        | `TASK`, `STEP`, `STEP_DEFINE` |

For example, `CAN_FAIL` trait is `STEP` type, it can be used to a step define or step.  

In step define, it can be configured like this:
``` json
{
  "stepName": "calc-2",
  "dependency": {
    "steps": ["prep"]
  },
  "traits": ["canFail"]
}
```

When the step is created, `CAN_FAIL` trait is inherited:
``` json
{
  "stepName": "calc-2",
  "traits": ["canFail"]
}
```

But when the task is created, `CAN_FAIL` trait will not be seen, because it doesn't impact task:
``` json
{
  "stepName": "calc-2"
}
```

??? question "Why `CAN_FAIL` trait is not seen in task?"
    Because the task executor doesn't care if the task can fail or not, only when we want to figure out the step result, the task failure affects.

## Trait

By now, there are 4 traits defined. The trait can be used by name or aliases, case-sensitive.

### CAN_IGNORE

- name: `CAN_IGNORE`
- aliases: `canIgnore`, `can-ignore`, `ignorable`
- type: `STEP_DEFINE`
- usage: the step can be ignored; only if this step is explicitly ignored by the job request, the step will be ignored.

### DELETED

- name: `DELETED`
- aliases: `deleted`
- type: `JOB`
- usage: the job is deleted; this trait can not be configured by user

### CAN_FAIL

- name: `CAN_FAIL`
- aliases: `canFail`, `can-fail`
- type: `STEP`
- usage: the step can be failed; if the step final result is `FAILED`, it will be set as `ACCEPTABLE_FAILED`, with the result seen as a `SUCCESS`.

### ARCHIVE

- name: `ARCHIVE`
- aliases: `archive`
- type: `TASK`
- usage: the task will be archived when it finishes; by default the finished tasks will be simply dropped, but some important tasks, which need to be tracked in the future, can be configured to be archived when finished.
