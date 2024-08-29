# Application Configuration

The TasCreed application parameters are defaultly configured in `tascreed.yaml`, they can be also overwritten in application properties file.

## Application Parameters

| Param Name | Description | Type | Overwrite Recommendation | Default Value |
| ----- | ----- | ----- | ----- | ----- |
| `tascreed.namespace` | the namespace of the whole TasCreed application, we should keep namespace unique for each application. | `string` | MUST | `/tascreed/default` |
| `tascreed.define.dirs` | the directories of job define files, split by `,`, all the files in the folder can be read as job defines, support json format content only. | `string` | OPTIONAL | `jobDefine` |
| `tascreed.watcher.job.interval.seconds` | the TasCreed job watcher thread loop interval seconds, to update job state, clear done tasks, create new tasks. | `int` | OPTIONAL | `60` |
| `tascreed.watcher.task.interval.seconds` | the TasCreed task watcher thread loop interval seconds, to create new task executor thread for task pick and execution. | `int` | OPTIONAL | `30` |
| `tascreed.worker.max.count.overall.default` | the default max worker thread count of the Tumble application, can be overwritten by etcd key `{namespace}/max_worker_overall`. | `int` | RECOMMEND | `20` |
| `tascreed.worker.max.count.per.host.default` | the default max worker thread count of each node, can be overwritten by etcd key `{namespace}/max_worker_count_per_host`. | `int` | RECOMMEND | `5` |
| `tascreed.worker.affinity.enable` | enable affinity rule of TasCreed worker thread to pick tasks. | `boolean` | OPTIONAL | `false` |
| `tascreed.ban.level` | the TasCreed job ban level, the value could be: `NONE` (nothing can be banned), `TASK_PICK` (banned job's task can not be picked), `TASK_CREATE` (cover `TASK_PICK`, and banned job can not create new task), `JOB_SUBMIT` (cover `TASK_CREATE`, and banned job can not be submitted) | `enum` | OPTIONAL | `JOB_SUBMIT` |

Overwrite Recommendation:
- **MUST**: must overwrite
- **RECOMMEND**: not must, but recommend to overwrite for specific usage
- **OPTIONAL**: not necessary in most cases, only for special usage
- **MUST_NOT**: some parameters are strongly not recommended to overwrite, so these parameters are not listed here