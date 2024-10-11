# States

TasCreed tracks the state of a job, step, and task, here introduces the states of each object.

## State definition

Each type of state is determined by two kinds of information, the completion and result.

- Completion, could be `UNDONE` or `DONE`, indicates the object is finished or not.
- Result, could be `UNKNOWN`, `SUCCESS`, `FAILED`, or `ERROR`, indicates the object has a certain result or not.

!!! note "Difference between `FAILED` and `ERROR` results"
	- `FAILED` indicates an expected failure of execution, which will only fail the current and following steps, but the other non-dependent steps can keep working.
	- `ERROR` indicates a non-expected exception during execution, which will mark the current step and the whole job as `ERROR` state, then all the remaining steps of the job will not be executed, except the tasks already created.

	*Usually, `FAILED` means retryable, while `ERROR` means non-retryable.*

## Task state

``` mermaid
flowchart LR
	UNDONE -->|task finish with<br> success result| SUCCESS
	UNDONE -->|task finish with<br> failed result| FAILED
	UNDONE -->|task finish by<br> an exception| ERROR
```

| State | Completion | Result | Description |
| ----- | ----- | ----- | ----- |
| `UNDONE` | `UNDONE` | `UNKNOWN` | the default state of a new created task |
| `SUCCESS` | `DONE` | `SUCCESS` | the task is successfully finished |
| `FAILED` | `DONE` | `FAILED` | the task is finished with a failed result |
| `ERROR` | `DONE` | `ERROR` | the task is finished by a non-expected exception |

!!! note "User changes the task state"
	TasCreed will **NOT** automatically set the task state when it finishes, users need to explicitly set the task state when implementing the execution method of `TaskExecutor`. 
	So users can decide which kind of task state to use.

## Step state

``` mermaid
flowchart LR
	DORMANT -->|start to create tasks| START
	DORMANT -->|step ignored by request| IGNORED
	DORMANT -->|any dependent step<br> has error result| SKIP_BY_ERROR
	DORMANT -->|any dependent step<br> has failed result,<br> and this step<br> can not fail| SKIP_BY_FAILED
	DORMANT -->|any dependent step<br> has failed result,<br> and this step<br> can fail| ACCEPTABLE_FAILED
	
	START -->|all tasks created| READY
	
	READY -->|any task is failed,<br> and this step<br> can fail| ACCEPTABLE_FAILED
	READY -->|any task is failed,<br> and this step<br> can not fail| FAILED
	READY -->|any task is error| ERROR
	READY -->|all tasks are success| SUCCESS
```

| State | Completion | Result | Description |
| ----- |------------| ----- | ----- |
| `DORMANT` | `UNDONE`   | `UNKNOWN` | the default state of a step |
| `START` | `UNDONE`   | `UNKNOWN` | the step starts to create tasks, and not finished the task creation |
| `READY` | `UNDONE`   | `UNKNOWN` | the tasks of the step are all created |
| `SUCCESS` | `DONE`     | `SUCCESS` | all the tasks of this step are success, so the step is success |
| `FAILED` | `DONE`     | `FAILED` | some task of this step is failed, and this step can not fail, so the step is failed |
| `ACCEPTABLE_FAILED` | `DONE`     | `SUCCESS` | the step can fail, some task of this step is failed, or any dependent step has a `FAILED` result, so the step is acceptable failed, not successfully finished but with a `SUCCESS` result |
| `ERROR` | `DONE`     | `ERROR` | some task of this step is error, so the step is error |
| `IGNORED` | `DONE`     | `SUCCESS` | the step can be ignored, and the job request asks to ignore this step, then it is ignored, with a `SUCCESS` result |
| `SKIP_BY_FAILED` | `DONE`     | `FAILED` | if any dependent step has a `FAILED` result, and this step can not fail, then the step is skipped with a `FAILED` result |
| `SKIP_BY_ERROR` | `DONE`     | `ERROR` | if any dependent step has a `ERROR` result, and this step can not fail, then the step is skipped with a `ERROR` result; `ERROR` is prior than `FAILED` |

!!! note "Difference between `START` and `READY` states"
	The `START` and `READY` states are similar, with the only difference of task creation finished or not.  
	Actually the `START` state step can be changed to `FAILED`, `ACCEPTABLE_FAILED`, `ERROR` like `READY` state, but only the `READY` state step can be changed to `SUCCESS` state.

???+ question "How does completion & result impact the next steps?"
	- Completion
		+ `UNDONE`: this step is not finished, next steps should be waiting
		+ `DONE`: this step is finished, next steps can be triggered
	- Result
		+ `SUCCESS`: next steps can start as expected
		+ `FAILED`: next steps can not start
		+ `ERROR`: next steps can not start

???+ question "What does it mean that a step can fail or not?"
	A step can fail means its failure is acceptable, it should not block the execution of its following steps.  
	Users can decide the step can fail or not by configuring it with `CAN_FAIL` trait.  

	- When the step failure condition is met (any task of the step is failed),
		+ if the step can fail, then it changes to `ACCEPTABLE_FAILED` state, with a `SUCCESS` result, which means the failure is acceptable;
		+ otherwise, it changes to `FAILED` state, with a `FAILED` result, which means the step fails, and the following steps will be impacted.
	- When the dependent step has a `FAILED` result,
		+ if the step can fail, then it changes to `ACCEPTABLE_FAILED` state as well;
		+ otherwise, it changes to `SKIP_BY_FAILED` state, with a `FAILED` result, meaning it is skipped due to the `FAILED` result of the dependent step.

	*The `ERROR` result doesn't behave the same, there's not a `CAN_ERROR` trait. An `ERROR` result will lead to the `SKIP_BY_ERROR` state of the next steps.*

## Job state

``` mermaid
flowchart LR
	UNDONE -->|all steps with<br> success result| SUCCESS
	UNDONE -->|any step with<br> failed result| FAILED
	UNDONE -->|any step with<br> error result| ERROR
	UNDONE -->|any step can<br> never start| STUCK
```

| State | Done enum | Result enum | Description |
| ----- | ----- | ----- | ----- |
| `UNDONE` | `UNDONE` | `UNKNOWN` | the default state of a job |
| `SUCCESS` | `UNDONE` | `SUCCESS` | all the steps of the job have a `SUCCESS` result |
| `FAILED` | `UNDONE` | `FAILED` | any step of the job has a `FAILED` result |
| `ERROR` | `UNDONE` | `ERROR` | any step of the job has a `ERROR` result |
| `STUCK` | `UNDONE` | `ERROR` | the job is stuck if some step can never by started, due to the circular dependency of the step DAG |

???+ question "What leads to a `STUCK` state?"
	If any step can never be triggered, the whole job will be stuck.  
	This is caused by circular dependency, which can be recognized and alert when reading the job define, so there should not be any job fall into the `STUCK` state, it is just defined to cover all the possibilities.

*Only `SUCCESS` jobs will be archived to the permanent storage, the jobs with other states will be left in bulletin, which can be manually retried afterwards.*
