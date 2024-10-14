# Update Param and Config

## Updated Param

As we know, a job has params, which can be used to pass parameters to the task execution.  
Actually, after execution, a task can also update the job params, to pass to the subsequently created tasks.

This feature can also be used to pass a small sized intermediate result to the subsequent tasks.

???+ question "What if the update param has the same key as the job/step param?"
    The update param has the highest overwrite level, so the subsequent created tasks will see the overwritten value.

Sample usage, update param in the end of the `executeImpl` method in task executor:
```
task.addUpdatedParam(key, value);
```

*Only the subsequently created tasks can see the updated param, e.g. tasks of the next steps.*

## Updated Config

Similarly, we can also update the config of subsequent steps, to modify like the pack range or shard number dynamically.  
For example, we can count the data volume in the first step, and then set the shard number of the next steps according to the total count. 

*Only the subsequently created steps config can be updated, e.g. following steps depending on current one.*

Sample usage, update step config in the end of the `executeImpl` method in task executor:
```
task.addUpdatedConfig(stepName, stepConf);
```

