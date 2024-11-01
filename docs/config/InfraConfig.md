# Infra Config

By default, TasCreed infra config is stored in `tascreed.yaml`, which can be partially overwritten by application properties file.

This document introduces the properties.

## Basic

| Param Name                              | Description                                                          | Type     | Overwrite | Default Value       |
|-----------------------------------------|----------------------------------------------------------------------|----------|-----------|---------------------|
| `tascreed.namespace`                    | namespace of your TasCreed application, should be unique             | `string` | MUST      | `/tascreed/default` |
| `tascreed.define.dirs`                  | job define file directories, split by `,`, `*.json` files are loaded | `string` | OPTIONAL  | `jobDefine`         |
| `tascreed.define.graph.validate.enable` | if enabled, will validate cyclic dependency of defined steps         | `bool`   | OPTIONAL  | `true`              |

## Task worker

| Param Name                                   | Description                                                   | Type   | Overwrite  | Default Value |
|----------------------------------------------|---------------------------------------------------------------|--------|------------|---------------|
| `tascreed.watcher.task.switch.on.default`    | if disabled, will not pick any task                           | `bool` | OPTIONAL   | `true`        |
| `tascreed.watcher.task.interval.seconds`     | the interval of task watcher thread loop, to pick tasks       | `int`  | OPTIONAL   | `30`          |
| `tascreed.worker.max.count.overall.default`  | the default number of task worker threads across all nodes    | `int`  | RECOMMEND  | `20`          |
| `tascreed.worker.max.count.per.host.default` | the default number of task worker threads on each node        | `int`  | RECOMMEND  | `5`           |
| `tascreed.worker.affinity.enable`            | if enabled, will consider affinity rules when assigning tasks | `bool` | OPTIONAL   | `false`       |

## Routine worker

| Param Name                                    | Description                                                   | Type   | Overwrite  | Default Value |
|-----------------------------------------------|---------------------------------------------------------------|--------|------------|---------------|
| `tascreed.watcher.routine.switch.on.default`  | if disabled, will not pick any routine                        | `bool` | OPTIONAL   | `true`        |
| `tascreed.watcher.routine.interval.seconds`   | the interval of routine watcher thread loop, to pick routines | `int`  | OPTIONAL   | `30`          |
| `tascreed.routine.max.count.overall.default`  | the default number of routine worker threads across all nodes | `int`  | RECOMMEND  | `20`          |
| `tascreed.routine.max.count.per.host.default` | the default number of routine worker threads on each node     | `int`  | RECOMMEND  | `5`           |

## Features

| Param Name                     | Description                                   | Type   | Overwrite | Default Value |
|--------------------------------|-----------------------------------------------|--------|-----------|---------------|
| `tascreed.ban.enable`          | enable the feature to pause the job execution | `bool` | OPTIONAL  | `true`        |
| `tascreed.duty.enable`         | enable the feature of node duty rules         | `bool` | OPTIONAL  | `true`        |
| `tascreed.archive.task.enable` | enable the feature to archive finished tasks  | `bool` | OPTIONAL  | `false`       |

## Environment

| Param Name                       | Description                                                   | Type     | Overwrite | Default Value |
|----------------------------------|---------------------------------------------------------------|----------|-----------|---------------|
| `tascreed.storage.bulletin`      | bulletin storage, could be `ETCD`                             | `enum`   | OPTIONAL  | `ETCD`        |
| `tascreed.storage.archive`       | archive storages, split by `,`, could be `ES`, `ETCD`, `NONE` | `enum`   | OPTIONAL  | `ES`          |
| `tascreed.logger`                | logger implementation                                         | `string` | OPTIONAL  | `std`         |
| `tascreed.etcd`                  | etcd implementation                                           | `string` | OPTIONAL  | `mem`         |
| `tascreed.es`                    | es implementation                                             | `string` | OPTIONAL  | `disk`        |
| `tascreed.metrics.server.enable` | enable prometheus metrics server                              | `bool`   | OPTIONAL  | `true`        |
| `tascreed.metrics.server.port`   | prometheus metrics server port                                | `int`    | OPTIONAL  | `9091`        |

!!! note "Environment properties"
    The default implementation of storages are simply in memory or disk, which is convenient for local test or debug.   
    Users can have their own implementations, and configure to switch the environment.