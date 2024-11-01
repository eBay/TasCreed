# Planned Features

## 0.4.1-SNAPSHOT
1. refine schedule, should be guaranteed
2. query history jobs of a schedule
3. abstract shard and pack step by window
4. refine es config key, avoid collision

## Backlog
1. dependency of other jobs [will not do]
2. job storage CRUD API
3. namespace unique constraint
4. multi namespace for one app [will not do]
5. resource management in sharing group, to better assign task to nodes; similar to the flink task slot sharing
6. monitor of TasCreed
7. follow shard/pack step config, when a sub task finishes, the same shard/pack sub task of the following step can start, as pipeline feature
8. metrics of more alive job insight
## Ideas
1. time wheel, prepare for the timer features [done]
2. schedule routine/job by cron expr [done]
3. schedule by resource management
4. exceptions/errors to downgrade node score, to avoid picking up tasks
5. refactor code to remove the hard dependency of raptor infra [done]
6. abstract shard and pack step by window [plan, think it]
	- with some properties like concurrency, ordered, etc.
	- window can also be recursive
	- simple step can also be abstracted by window, so a property can be recursivable
7. one worker node can pick multi tasks at one time [plan]
8. Markov chain super step like workflow, Pregel graph computing model [maybe not a proper idea]

