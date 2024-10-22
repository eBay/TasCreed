# Archive

## Archive jobs

When a job is done with a determined result, `SUCCESS` or `FAILED`, it can be archived to the archive storage.

Archived jobs can be used for:

- job query, to find the historical jobs
- job deduplication, to avoid dual running of the same job

## Archive tasks

By default, a task is simply removed when it is done.

But for the tasks with `archive` trait (1), it can be archived to the archive storage when it is done.
{ .annotate }

1. `archive` trait of a task is inherited from its belonged step, which can be defined in the step define, or overwritten in job request

## Archive storage types

Users can configure to choose different implementations of archive storages.

An example of configuration could be like this:

``` properties
tascreed.storage.archive = ETCD, ES
```

The value is a list of the archive storage names. By default, it is set as `ES` only.

### ES (ElasticSearch)

Archived jobs are saved in index `tascreed_job_<namespace>`, and archived tasks are saved in index `tascreed_task_<namespace>`.

### ETCD

Archived jobs are saved in keys with prefix `<namespace>/archive/job/`, and archive tasks is not supported in ETCD.

In ETCD archive storage, `tascreed.storage.archive.etcd.retention.hours` can configure the retention time of the archived jobs, 7 days by default.

### NONE

Literally, no archive.
