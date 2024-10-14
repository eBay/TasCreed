# Ban & Resume

In TasCreed, we provide the ban function to disable/enable the execution of task or routine, this feature is called ban & resume.

## Ban jobs

We can ban a job, to disable all the tasks of this job; or we can ban all the jobs, to disable any task execution.  
The ban function is determined by two dimensions, scope and action.

First, users need to decide to ban at which scope:

- job: all tasks of the specific job instance will be impacted
- job define: bigger scope than job, all tasks of the job instances of the specific job define will be impacted
- global: bigger scope than job define, all tasks are impacted

After determining the scope, we can choose the action level, defined for different scopes:

| Ban level     | Global | Job define | Job |
|---------------|--------|------------|-----|
| `TASK_PICK`   | Y      | Y          | Y   |
| `TASK_CREATE` | Y      | Y          | Y   |
| `JOB_SUBMIT`  | Y      | Y          | -   |

- `TASK_PICK`: can not pick task to execute, but can still create new tasks
- `TASK_CREATE`: cover `TASK_PICK` level, and can not create new tasks
- `JOB_SUBMIT`: cover `TASK_CREATE` level, and can not submit new job instance

*If a task has been picked and executing, it can not be stopped.*

## Ban routines

Similarly, we can ban the execution of routines.  
The ban function is also determined by the two dimensions.

First, users need to decide to ban at which scope:

- routine: the specific routine instance will be impacted
- routine define: all instances of a specific routine define will be impacted

After determining the scope, we can choose the action level, defined for different scopes:

| Ban level        | Routine define | Routine |
|------------------|----------------|---------|
| `ROUTINE_EXEC`   | Y              | Y       |
| `ROUTINE_OCCUPY` | Y              | Y       |

- `ROUTINE_EXEC`: can not execute routine
- `ROUTINE_OCCUPY`: cover `ROUTINE_EXEC` level, and can not occupy routine; if a routine is already occupied, it will be dropped

*Routine is executed round and round, thus the current running round can not be stopped, but the next round can.*
