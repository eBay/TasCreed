# Archive

**from 0.2.9-SNAPSHOT**

## Archive storage

The done jobs will be automatically archived to the archive storage, only if it can be auto-archived: the job state is `SUCCESS` or `FAILED`.

Archived jobs can be used for:
- job query
- job deduplication

Before version 0.2.9-SNAPSHOT, there is a default archive storage implementation, which is built on ElasticSearch.  
Now, users can configure and choose different implementations of archive storages, even there could be none or more than one choices.

The sample of configuration could be like this:
```
tumbler.storage.archive = ETCD, ES
```
The value is a list of the archive storage names. By default, it is set as `ES` only, the same as before.

## Archive storage types

### ES
done jobs are archived in ES, and the archived jobs can also be found from ES for deduplication or query.

the archived jobs are saved in the ES index `tumbler_job_*`, with the wildcard replaced by the tumbler namespace.

### ETCD
done jobs are archived in ETCD, and teh archived jobs can also be found from ETCD for deduplication or query.

the archived jobs are saved in the ETCD prefix `*/archive/job/`, with the wildcard replaced by the tumbler namespace.

the archived jobs in ETCD has a special feature called retention time. It will be kept in ETCD for 7 days by default, which can be also changed by users in configuration. After the retention time, the archived jobs will be removed from ETCD itself.

the retention feature will keep the ETCD usage a constant space, with the price of historical jobs dropped forever. It could be useful in some special scenarios.

### NONE
Literally as the name, no archive, do nothing.
