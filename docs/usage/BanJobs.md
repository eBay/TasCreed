# Job Control

## Ban Job Submit, Task Create, Task Pick

**from: 0.2.3-RELEASE**

In the first version to support ban function, we can ban the jobs in these 3 levels: 
- Job Submit: job can not be submitted if the job or job define is banned, also covers `Task Create` & `Task Pick` level
- Task Create: new taks can not be created if the job or job define is banned, also covers `Task Pick` level
- Task Pick: task can not be picked if the job or job define is banned

The ban level is defined in the application configuration, by default is Job Submit level, which means all the 3 functions can be banned.

Users can ban the job or job define, to disable the job submission, task creation and task pick functions of the specific job or job define. Furthermore, users can ban all the job and job defines via ban `allJobs` api.

**from: 0.2.3.3-SNAPSHOT**

The ban config in etcd will be read and cached in memory, and refreshed after 1 minute, this works for `Task Create` and `Task Pick`; But for `Job Submit`, it always refresh the cached data from etcd, so it can be affected immediately after the ban request.

**from: 0.2.5-SNAPSHOT**

The enhancement of ban function includes:
- rename ban `allJobs` api to ban `global` api
- only the covered level can be banned, covered can be explained in two ways:
    + covered by default ban level (as before)
    + covered by ban element type, as the following relation table
- remove `default ban level` configuration, instead, add a `ban enable` configuration
- all the ban request should give the ban level
- ban request succeed with response `true`, fails with response `false` or other exceptions
    + the failure reason could be: `ban enable` not enabled, ban level in request is not valid
    + the exception reason could be: job define or job is not exist

| Action can be banned | Global | Job Define | Job |
| ----- | ----- | ----- | ----- |
| Job Sumbit | Y | Y | - |
| Task Create | Y | Y | Y |
| Task Pick | Y | Y | Y |

Explanations:
- If `Global` banned: the actions covered by its ban level will be affected. All the jobDefines and jobs are banned.
- If `Job Define` banned: the actions covered by its ban level will be affected. Only the specific jobDefines are banned.
- If `Job` banned: the actions covered by its ban level will be affected. Only the specific jobs are banned.

## Ban Routine Execute, Routine Occupy

**from: 0.3.1-RELEASE**

The routines can be banned and resumed on different levels as well:
- `ROUTINE_EXEC`: the routine job can not be executed, simply skip the execution of each round
- `ROUTINE_OCCUPY`: covers `ROUTINE_EXEC`, the routine job can not be occupied; if the routine is already occupied, it will be dropped

| Action can be banned | Routine Define | Routine |
| ----- | ----- | ----- |
| Routine Occupy | Y | Y |
| Routine Execute | Y | Y |

Explanations:
- If `Routine Define` banned: the actions covered by its ban level will be affected, only the specific routineDefines are banned.
- If `Routine` banned: the actions covered by its ban level will be affected, only the specific routines are banned.
