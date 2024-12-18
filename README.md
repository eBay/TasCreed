# TasCreed

Welcome to TasCreed! Execute task with creed!

TasCreed is a light-weighted framework for reliable distributed task execution.

## Why TasCreed

When we execute a task, it is usually expected to be accomplished unwaveringly, without any maintenance effort. 

TasCreed is designed to provide such commitment.

- task execution can recover from environment issue, such as node crash
- submitted job will be executed thoroughly, eventually

Besides the basic goals, TasCreed also provides more convenience for users to execute tasks in a distributed way.

- execute tens of tasks in parallel, with simple configuration
- split a batch of data into partitions, and execute them in parallel
- create long run job to execute mini-batches continuously

These features are really useful in some special scenarios.

*Please be aware that, a task is executed at least once, so the side effect of the task should be idempotent. This is also a common requirement for the retryable batch jobs.*

## Quick Start

You can have a [quick try](docs/Quickstart.md) of the TasCreed framework with a sample application.

## Tutorial

You can refer to [more documents](docs/Overview.md) to learn the details about TasCreed.

## Features

- [Features](docs/misc/Features.md) introduces the features of historical TasCreed versions.
- [Planned Features](docs/misc/FeaturesPlanned.md) introduces the planned features of TasCreed in future versions.

## Configuration

- [Application Config](docs/config/ApplicationConfig.md) introduces how to config the TasCreed application.
- [Application Detailed Config](docs/config/AppDetailedConfig.md) introduces the detailed config of TasCreed application.
- [Job Define Config](docs/spec/JobDefineConfig.md) introduces how to config a job define file.

## Usage

- [Job Request](docs/detail/JobRequest.md) introduces how to submit job request to trigger a job instance.
- [Params](docs/spec/Params.md) introduces the params usage.
- [Advanced Usage](docs/detail/execution/AdvancedUsage.md) introduces some advanced features you can leverage in TasCreed job.
- [Update Param Config](docs/detail/execution/UpdateParamConfig.md) introduces how to update params and configs for the later tasks.
- [Ban Jobs](docs/detail/execution/BanJobs.md) introduces how to pause and resume a job in different levels.
- [Progression](docs/detail/execution/Progression.md) introduces the visualization of progression of a job.
- [States](docs/detail/execution/States.md) introduces the state of task, step and job, as well as the state machine.
- [Phase](docs/detail/definition/Phase.md) introduces the phase feature of step, as a weaker dependency relationship.
- [Archive](docs/detail/management/Archive.md) introduces the archive feature of done jobs, users can choose their own combinations of implementations, such as etcd, es.
- [Trait](docs/detail/definition/Trait.md) introduces the trait feature of TasCreed data objects, users can configure the optional characters of the job, step and task in different level.
- [Routine](docs/detail/definition/Routine.md) introduces a new kind of executor named routine, the long run routine job across the whole TasCreed cluster can be defined as a routine. It is an important complement of the task executor.
- [Annotation](docs/detail/definition/Annotation.md) introduces the TasCreed defined annotations, to help register the task executors and routine executors in code.
- [Node Duty](docs/detail/management/NodeDuty.md) introduces the duty rules of the TasCreed application nodes, to help users control which nodes can work as expected.
- [Schedule](docs/detail/definition/Schedule.md) introduces the schedule feature, users can schedule trigger time of job defines in advance, which means TasCreed can work as a time scheduler.

## Acknowledgements
Special thanks to [people](ACKNOWLEDGEMENTS.md) who give your support on this project.

## License Information
Copyright 2023-2024 eBay Inc.

Authors/Developers: Lionel Liu

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## Core Developers
- Lionel Liu

Please see [here](CONTRIBUTORS.md) for all contributors.

