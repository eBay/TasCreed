# States

**from: 0.2.6-SNAPSHOT**

**updated: 0.2.8-RELEASE**
- rename `START_FAILED` to `SKIP_BY_FAILED`
- introduces `SKIP_BY_ERROR` state
- job `ERROR` state is updated after all steps done, instead of fast fail

## Definition of state

Each state includes two kinds of information, to describe the state of the job/step/task.

### Done enum

Indicate the job/step/task is finished or not.
- `UNDONE`: job/step/task not finished
- `DONE`: job/step/task finished

### Result enum

Indicate the job/step/task has a certain result or not, and the type of certain result.
- `UNKNOWN`: not a certain result
- `SUCCESS`: a certain result which means an expected success
- `FAILED`: a certain result which means an expected failed
- `ERROR`: a certain result which means an exception

The difference between `FAILED` and `ERROR` results:
- `FAILED` indicates an expected failure of execution, which will only fail the current and following steps, but the other non-dependent steps can keep working.
- `ERROR` indicates an non-expected exception during execution, which will mark the current step and the whole job as `ERROR` state, then all the remaining steps of the job will not be executed, except the tasks already created.

## Task state

![task_state](../pic/task_state.png)

| State | Done enum | Result enum | Description |
| ----- | ----- | ----- | ----- |
| `UNDONE` | `UNDONE` | `UNKNOWN` | the default state of a new created task |
| `SUCCESS` | `DONE` | `SUCCESS` | the task is successfully finished |
| `FAILED` | `DONE` | `FAILED` | the task is finished with a failed result |
| `ERROR` | `DONE` | `ERROR` | the task is finished by a non-expected exception |

Please be noted that TasCreed will **NOT** mark the task from undone state to the done states, the task state change should be explicitly implemented in the execution method of `TaskExecutor` by users. So it's your choice to use any kind of the task states.

## Step state

![step_state](../pic/step_state.png)

| State | Done enum | Result enum | Description |
| ----- | ----- | ----- | ----- |
| `DORMANT` | `UNDONE` | `UNKNOWN` | the default state of a step |
| `START` | `UNDONE` | `UNKNOWN` | the step starts to create tasks, and not finished the task creation |
| `READY` | `UNDONE` | `UNKNOWN` | the tasks of the step are all created |
| `SUCCESS` | `DONE` | `SUCCESS` | all the tasks of this step are success, so the step is success |
| `FAILED` | `DONE` | `FAILED` | some task of this step is failed, and this step can not fail, so the step is failed |
| `ACCEPTABLE_FAILED` | `DONE` | `SUCCESS` | the step can fail, some task of this step is failed, or any dependent step has a `FAILED` result, so the step is acceptable failed, not successfully finished but with a `SUCCESS` result |
| `ERROR` | `DONE` | `ERROR` | some task of this step is error, so the step is error |
| `IGNORED` | `DONE` | `SUCCESS` | the step can be ignored, and the job request asks to ignore this step, then it is ignored, with a `SUCCESS` result |
| `SKIP_BY_FAILED` | `DONE` | `FAILED` | if any dependent step has a `FAILED` result, and this step can not fail, then the step is skipped with a `FAILED` result |
| `SKIP_BY_ERROR` | `DONE` | `ERROR` | if any dependent step has a `ERROR` result, and this step can not fail, then the step is skipped with a `ERROR` result; `ERROR` is prior than `FAILED` |

The `START` and `READY` states are similar, with the only difference of task creation finished or not. Actually the `START` state step can be changed to `FAILED`, `ACCEPTABLE_FAILED`, `ERROR` like `READY` state, but only the `READY` state step can be changed to `SUCCESS` state.

Something about the `FAILED` result:
- if any task of the current step is failed, then the step state is determined by its `CAN_FAIL` trait, which indicates the step can fail or not.
	+ if the step can fail, then it changes to `ACCEPTABLE_FAILED` state, with a `SUCCESS` result, which means the task failure is acceptable; 
	+ if the step can not fail, then it changes to `FAILED` state, with a `FAILED` result, which means the step fails due to task failure.
- if any dependent step has a `FAILED` result, it will impact all its follow-up steps, the follow-up steps will be skipped with a `FAILED` result by default, except the follow-up step can fail.
	+ if the step can fail, then it changes to `ACCEPTABLE_FAILED`, skipped with a `SUCCESS` result, which means it is impacted by the `FAILED` result of its dependent step, but the failure of this step is acceptable.
	+ if the step can not fail, then it changes to `SKIP_BY_FAILED`, skipped with a `FAILED` result, which means it is impacted by the `FAILED` result of its dependent step, and this step inherits the `FAILED` result.
	+ if the step can not fail, and another dependent step has an `ERROR` result, then this step will change to `SKIP_BY_ERROR`, skipped with an `ERROR` result, which means it is impacted by the `ERROR` result of its dependent step, because `ERROR` is prior than `FAILED`.

## Job state

![job_state](../pic/job_state.png)

| State | Done enum | Result enum | Description |
| ----- | ----- | ----- | ----- |
| `UNDONE` | `UNDONE` | `UNKNOWN` | the default state of a job |
| `SUCCESS` | `UNDONE` | `SUCCESS` | all the steps of the job have a `SUCCESS` result |
| `FAILED` | `UNDONE` | `FAILED` | any step of the job has a `FAILED` result |
| `ERROR` | `UNDONE` | `ERROR` | any step of the job has a `ERROR` result |
| `STUCK` | `UNDONE` | `ERROR` | the job is stuck if some step can never by started, due to the circular dependency of the step DAG |

Please be noted that the `ERROR` state is determined whenever a step is `ERROR`, but the `FAILED` state is determined when all the steps are done, and there's `FAILED` step.

Since the circular dependency of DAG can be checked when reading the job define, so there should not be any job fall into the `STUCK` state, it is just defined to cover all the possibilities.

About the job archiveness, only the `SUCCESS` jobs will be archived to the permanent storage, the jobs with the other states will be left in the etcd.