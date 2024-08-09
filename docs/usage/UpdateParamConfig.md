# Update Param and Config

## Updated Params

**from: 0.2.0-SNAPSHOT**

Task executor sometimes need to update param value or pass a small size middle result to the later created tasks, then we can add update params when in the current task, these updated params can be recorded in the task output, and collected by job server, write to the job update params, so when create new tasks, the job updated params can be set as task params, with the highest overwrite level.

Sample usage
```
task.addUpdatedParam(key, value);
```

## Updated Config

**from: 0.2.3-RELEASE**

Sometimes, we can not decide the later step's config, like pack range or shard number. In such cases, we can calculate the variable part of later step's config, put into this task's updated config map, then it will be recorded in the task output, and collected by job server, overwrite to the specific step config, so when create the later step, config has been changed.

Please note that, step config change can only affect the later steps.

Sample usage
```
task.addUpdatedConfig(stepName, stepConf);
```

