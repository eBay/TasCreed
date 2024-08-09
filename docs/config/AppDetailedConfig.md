# Configuration of Tumbler application

**from 0.3.5-SNAPSHOT**

Applications using Tumbler library can overwrite the properties, to customize the system behaviour instead of the default.

The default properties of Tumbler library can be found in [this file](../../tumbler-core/tumbler-infra/src/main/resources/tumbler.yaml), it provides the default values of these necessary properties, users can overwrite any of them in the application properties file.

This document introduces all the properties, with the overwrite recommendation levels: 
- `MUST`: should overwrite
- `RECOMMEND`: recommend to overwrite
- `OPTIONAL`: can overwrite on demand
- `NO`: should not overwrite

## Common properties

| Property | Description | Overwrite | Data type | Default value |
| ----- | ----- | ----- | ----- | ----- |
| `tumbler.version` | the version of tumbler library | `NO` | string | |
| `tumbler.namespace` | the prefix of all the tumbler keys in etcd, it should be unique for different applications | `MUST` | string | `/tumbler/default` |
| `tumbler.define.dirs` | the directories of job defines, relative path, split by `,`; job define files are read via the path `classpath:/{dirPath}/*.json` | `OPTIONAL` | string | `jobDefine` |
| `tumbler.define.graph.validate.enable` | enable step graph validation of job defines, to make sure no step is unaccessed, the check failed job defines will be failed to read | `OPTIONAL` | bool | `true` |

## Bulletin key & prefix

All the bulletin key and prefix properties are not recommended to overwrite, here just introduces them one by one.

| Property | Description | Overwrite | Data type | Default value |
| ----- | ----- | ----- | ----- | ----- |
| `tumbler.bulletin.routine.adoption.lock` | the lock key for routine adoption | `NO` | string | `%s/lock/routine-adoption` |
| `tumbler.bulletin.routine.adoption.prefix` | the prefix of routine adoption keys | `NO` | string | `%s/routine-adoption/` |
| `tumbler.bulletin.routine.update.lock.prefix` | the lock key prefix for updating routine checkpoint | `NO` | string | `%s/lock/routine-checkpoint/` |
| `tumbler.bulletin.checkpoint.prefix` | the prefix of routine checkpoint keys | `NO` | string | `%s/routine-checkpoint/` |
| `tumbler.bulletin.job.update.lock.prefix` | the lock key prefix for updating job info | `NO` | string | `%s/lock/job-info/` |
| `tumbler.bulletin.job.info.prefix` | the prefix of job info keys | `NO` | string | `%s/job-info/` |
| `tumbler.bulletin.task.update.lock.prefix` | the lock key prefix for updating task info | `NO` | string | `%s/lock/task-info/` |
| `tumbler.bulletin.task.info.todo.prefix` | the prefix of todo task info keys | `NO` | string | `%s/task-info/todo/` |
| `tumbler.bulletin.task.info.done.prefix` | the prefix of done task info keys | `NO` | string | `%s/task-info/done/` |
| `tumbler.bulletin.task.info.error.prefix` | the prefix of error task info keys | `NO` | string | `%s/task-info/error/` |
| `tumbler.bulletin.task.adoption.lock` | the lock key for task adoption | `NO` | string | `%s/lock/task-adoption` |
| `tumbler.bulletin.task.adoption.prefix` | the prefix of task adoption keys | `NO` | string | `%s/task-adoption/` |
| `tumbler.bulletin.schedule.update.lock.prefix` | the lock key prefix for updating schedule info | `NO` | string | `%s/lock/schedule-info/` |
| `tumbler.bulletin.schedule.info.prefix` | the prefix of schedule info keys | `NO` | string | `%s/schedule-info/` |

## Executor related properties

| Property | Description | Overwrite | Data type | Default value |
| ----- | ----- | ----- | ----- | ----- |
| `tumbler.watcher.routine.switch.on.key` | the key of routine watcher switch | `NO` | string | `%s/watch/routine/switch_on` |
| `tumbler.watcher.routine.switch.on.default` | the default value if routine watcher switch key not set | `OPTIONAL` | bool | `true` |
| `tumbler.watcher.routine.interval.seconds` | the interval of routine watcher to occupy routine, in seconds | `OPTIONAL` | int | `30` |
| `tumbler.watcher.task.switch.on.key` | the key of task watcher switch | `NO` | string | `%s/watch/task/switch_on` |
| `tumbler.watcher.task.switch.on.default` | the default value if task watcher switch key not set | `OPTIONAL` | bool | `true` |
| `tumbler.watcher.task.interval.seconds` | the interval of task watcher to occupy task, in seconds | `OPTIONAL` | int | `30` |
| `tumbler.worker.max.count.overall.key` | the key of max overall worker count | `NO` | string | `%s/max_worker_count_overall` |
| `tumbler.worker.max.count.overall.default` | the default value if max overall worker count key not set | `OPTIONAL` | string | `20` |
| `tumbler.worker.max.count.per.host.key` | the key of max per host worker count | `NO` | string | `%s/max_worker_count_per_host` |
| `tumbler.worker.max.count.per.host.default` | the default value if max per host worker count key not set | `OPTIONAL` | string | `5` |
| `tumbler.worker.affinity.enable` | enable affinity when worker occupy tasks | `OPTIONAL` | bool | `false` |
| `tumbler.routine.max.count.overall.key` | the key of max overall routine count | `NO` | string | `%s/max_routine_count_overall` |
| `tumbler.routine.max.count.overall.default` | the default value if max overall routine count key not set | `OPTIONAL` | string | `20` |
| `tumbler.routine.max.count.per.host.key` | the key of max per host routine count | `NO` | string | `%s/max_routine_count_per_host` |
| `tumbler.routine.max.count.per.host.default` | the default value if max per host routine count key not set | `OPTIONAL` | string | `5` |

## Other advanced properties

Including the properties of advanced features, which are not recommended to overwrite, here gives the introduction.

| Property | Description | Overwrite | Data type | Default value |
| ----- | ----- | ----- | ----- | ----- |
| `tumbler.ban.enable` | enable the ban feature; if false, no job or routine can be banned | `OPTIONAL` | bool | `true` |
| `tumbler.ban.global.key` | the key of ban action on global level | `NO` | string | `%s/ban/global` |
| `tumbler.ban.job.define.prefix` | the key prefix of ban action for a specific job define | `NO` | string | `%s/ban/job-define/` |
| `tumbler.ban.job.prefix` | the key prefix of ban action for a specific job | `NO` | string | `%s/ban/job/` |
| `tumbler.ban.routine.define.prefix` | the key prefix of ban action for a specific routine define | `NO` | string | `%s/ban/routine-define/` |
| `tumbler.ban.routine.prefix` | the key prefix of ban action for a specific routine | `NO` | string | `%s/ban/routine/` |
| `tumbler.duty.enable` | enable the duty feature; if false, no duty can be disabled | `OPTIONAL` | bool | `true` |
| `tumbler.duty.rules.global.key` | the key of duty rules, only on global level | `NO` | string | `%s/duty/rules/global` |
| `tumbler.archive.task.enable` | enable archive task feature; if true, done tasks with `archive` trait will be archived to archive storage, instead of simply deleted in bulletin | `NO` | bool | `false` |
| `tumbler.storage.archive` | archive storage options, split by `,`, could be `ES`, `ETCD`, `NONE` | `NO` | enum | `ES` |
| `tumbler.storage.bulletin` | bulletin storage option, could be `ETCD` | `NO` | enum | `ETCD` |
| `tumbler.logger.default` | default logger, could be `CAL`, `STD` | `NO` | enum | `CAL` |
