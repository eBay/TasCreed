# TasCreed Features

## 0.1.0-SNAPSHOT
1. define job and step by name
2. job step dependency
3. task sharding
4. RESTful trigger job instance
5. job execution archive to es

## 0.2.0-SNAPSHOT
1. affinity rule of step
2. job param as global param
3. updated param as global param
4. job last modify time support
5. support job step in pack mode
6. fix bug of simple time format

## 0.2.1-SNAPSHOT
1. register step name with job name
2. configurable step execute class
3. read job define files from multi define dirs
4. limited number of tasks can be built for each job at once

## 0.2.2-SNAPSHOT
1. upgrade raptor.io to 0.11.2
2. list job, task, task adoption
3. delete job, task, task adoption
4. close threads gracefully

## 0.2.3-RELEASE
1. support ban job submit, task create, task pick in different level
2. prioritize task pick by job priority
3. able to ignore ignorable step in trigger job request
4. long run job step in pack mode support
5. delete alive job with optional archive parameter
6. support unique alive instance config in job define
7. success task can update dormant step config

## 0.2.4-SNAPSHOT
1. affinity rule can be overwritten by request
2. checkpoint task executor
3. job/step/task progression percentage expose, with step effort config
4. job/task update lock in each entity level
5. refactor ban function, only covered ban level can be banned
6. embed job/state controller into infra

## 0.2.5-SNAPSHOT
1. cache etcd config values, refresh after time out, to reduce etcd hit frequency
2. ban job/task in different level by request
3. fix bug of all ignored steps in a job instance stuck there
4. record the modify thread of task; done tasks could be archived to ES, need to enable this feature in configuration and step define
5. job/step request with task create after time; task with task pick after time. With this feature, TasCreed can support delayed task creation and back-off task retry across threads

## 0.2.6-SNAPSHOT
1. trait to support ignorable, archive, delete, canFail features
2. multiple dependent steps to support DAG, verify DAG of job define
3. redefine job/step/task states and state transfer logic

## 0.2.7-RELEASE
1. unit test of TasCreed domain and infra
2. enrich job and step validation
3. fix watcher thread sleep bug if any exception
4. metrics to expose TasCreed working state
5. task pick priority consider step empty rate as well
6. update alive job to change step pack size
7. auto archive failed job as success job, fail is acceptable result
8. api of manual retry all error tasks of a job
9. upgrade es to ELK 7, upgrade etcd lib to switch cluster in staging

## 0.2.8-RELEASE
1. job server trigger state update and task creation in queue, to accelerate the task creation
2. differentiate monitor and normal heartbeat config
3. step state change
	- rename state start_failed to skip_by_failed
	- add state skip_by_error
	- error state will not fast fail the job
	- error state can be set by infra: non-retry or fatal exception exceeds max error retry times (by default will retry infinitely)
4. step config max error times
5. dependent step states are passed to next step tasks
6. step phase field in dependency, a step can start only when all prev phase steps finished

## 0.2.9-RELEASE
1. make es as an optional archive storage
2. etcd archive storage purges old jobs for retention
3. metric of task retry times
4. task/job execution time exceed metric
5. users can do task occupation check by demand

## 0.3.0-RELEASE
1. TasCreed dedicated es cluster
2. update alive job step max task count and max pick times

## 0.3.1-RELEASE
1. define done range state of pack/shard step, users can know the detailed progress and generate watermark for pack step
2. worker count per host can be changed on the fly
3. annotation of task executor registry
4. define routine, make it extensible
5. refactor monitor thread as routine
6. refactor job watcher thread as routine
7. routine params configurable

## 0.3.2-RELEASE
1. controller of routine adoption
2. TasCreed lib doesn't depend on raptor parent pom file
3. infinite retry when read global config from etcd
4. node duty rules for node validity control

## 0.3.3-RELEASE
1. enable min valid app version control feature
2. version comparison use maven ComparableVersion for better compatibility

## 0.3.4-RELEASE
1. invalid node regex in node duty rule
2. sample watermark routine
3. change infinite read global config from etcd to 5 times retry, it can throw exception if any error encountered
4. get routine trigger information in executor
5. execution exception counter is exposed in metric

## 0.3.5-RELEASE
1. make cal logger optional, default logger can be configured by property
2. abstract es util and etcd util interfaces, default implementation depends on ebay environment
3. build time schedule module
4. node duty of schedule server

## 0.4.0-RELEASE
1. refactor EtcdBulletinStorage to distinguish different data types
2. TasCreed api spec
3. refactor TasCreed dependency lib, ebay related dependency can be replaced by external users
4. ebay related configuration wrapped in one file
5. refactor to extract ebay related dependency from infra package
