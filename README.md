# TasCreed

Welcome to TasCreed!  
TasCreed is a framework library of job executor, to guarantee the jobs executed thoroughly.

## Why TasCreed
If we want to execute a batch job, we would expect:
- this job must be executed
- if the job execution failed by environment issue, it can retry itself
- if the executing node crashes, another node can take the job

This is the basic aim of TasCreed, and it can provide the at least once job execution guarantee, even node crash encountered.

Furthermore, TasCreed can provide you some more convenience:
- execute tens of tasks in parallel, with simple configuration
- split a batch of data into partitions, and execute them parallelly
- create long run job to execute mini-batches continuously

These features are really useful in some special scenarios, TasCreed can help on this in a simple way.

Please be aware that, TasCreed task is executed at least once, so the side effect of your task should be idempotent, this is also a common requirement for the retryable batch jobs.

## Overview

[Overview](docs/Overview.md) introduces the basic concepts of TasCreed, you can take a cup of coffee to have a glance.

## Features

- [Features](docs/Features.md) introduces the features of historical TasCreed versions.
- [Planned Features](docs/FeaturesPlanned.md) introduces the planned features of TasCreed in future versions.

## Configuration

- [Application Config](docs/config/ApplicationConfig.md) introduces how to config the TasCreed application.
- [Application Detailed Config](docs/config/AppDetailedConfig.md) introduces the detailed config of TasCreed application.
- [Job Define Config](docs/config/JobDefineConfig.md) introduces how to config a job define file.

## Usage

- [Job Request](docs/usage/JobRequest.md) introduces how to submit job request to trigger a job instance.
- [Params](docs/usage/Params.md) introduces the params usage.
- [Advanced Usage](docs/usage/AdvancedUsage.md) introduces some advanced features you can leverage in TasCreed job.
- [Update Param Config](docs/usage/UpdateParamConfig.md) introduces how to update params and configs for the later tasks.
- [Ban Jobs](docs/usage/BanJobs.md) introduces how to pause and resume a job in different levels.
- [Progression](docs/usage/Progression.md) introduces the visualization of progression of a job.
- [States](docs/usage/States.md) introduces the state of task, step and job, as well as the state machine.
- [Phase](docs/usage/Phase.md) introduces the phase feature of step, as a weaker dependency relationship.
- [Archive](docs/usage/Archive.md) introduces the archive feature of done jobs, users can choose their own combinations of implementations, such as etcd, es.
- [Trait](docs/usage/Trait.md) introduces the trait feature of TasCreed data objects, users can configure the optional characters of the job, step and task in different level.
- [Routine](docs/usage/Routine.md) introduces a new kind of executor named routine, the long run routine job across the whole TasCreed cluster can be defined as a routine. It is an important complement of the task executor.
- [Annotation](docs/usage/Annotation.md) introduces the TasCreed defined annotations, to help register the task executors and routine executors in code.
- [Node Duty](docs/usage/NodeDuty.md) introduces the duty rules of the TasCreed application nodes, to help users control which nodes can work as expected.
- [Schedule](docs/usage/Schedule.md) introduces the schedule feature, users can schedule trigger time of job defines in advance, which means TasCreed can work as a time scheduler.

# Acknowledgements
Special thanks to [people](ACKNOWLEDGEMENTS.md) who give your support on this project.

# License Information
Copyright 2023-2024 eBay Inc.

Authors/Developers: Lionel Liu

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

# Core Developers
- Lionel Liu

Please see [here](CONTRIBUTORS.md) for all contributors.

