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

!!! tip "Tips"
    Please be aware that, a task is executed at least once, so the side effect of the task should be idempotent. This is also a common requirement for the retryable batch jobs.

## Quick Start

You can have a [quick try](Quickstart.md) of the TasCreed framework with a sample application.

## Tutorial

You can refer to [more documents](Tutorial.md) to learn the details about TasCreed.
