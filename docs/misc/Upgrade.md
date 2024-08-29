# TasCreed Upgrade Guide

## Extra version history

### from 0.2.3-RELEASE to 0.2.3.1-RELEASE
1. upgrade etcd version to 1.2.9-RELEASE (etcd migration)
2. upgrade raptor-io version to 0.12.3-RELEASE (SWU-Q2)
3. 0.2.3.2-RELEASE is the same as 0.2.3.1-RELEASE

### from 0.2.3.1-RELEASE to 0.2.3.1-UP-RELEASE
1. change sample app name in raptor_app.xml (no impact)

### from 0.2.3.1-UP-RELEASE to 0.2.3.3-RELEASE
1. upgrade raptor-io version to 0.13.3-RELEASE (SWU-Q4)
2. upgrade es version to 1.2.11-RELEASE (ELK 7 upgrade and migration)
3. upgrade etcd version to 1.2.11-RELEASE (staging etcd migration to tess)

### from 0.2.4-RELEASE to 0.2.4-UP-RELEASE
1. change sample app name in raptor_app.xml (no impact)
2. upgrade raptor-io version to 0.12.3-RELEASE (SWU-Q2)
3. already included etcd upgrade in normal commit

## Most used versions
- 0.2.3.1-RELEASE
	+ fasorchestrator
	+ fasrecongen
	+ Thunder
	+ cashmanager
	+ Pangolin
	+ reports
	+ Skyscraper
	+ Sailfish
- 0.2.3-RELEASE
	+ pangolin-cleaner
	+ fasnvbat

## How to use new versions

The extra versions are for environment compatibility, not for main features, so there should be no code change required to use the extra versions with the same minor version. For example, 0.2.3.1-RELEASE has the same features as 0.2.3-RELEASE.

Let's start from version 0.2.3, and list all the new features that requires code change if the users want to use the new versions of TasCreed.

### from 0.2.3 to 0.2.4
1. checkpoint task executor: this feature enables a new task executor type which can temporarily save checkpoint in task in etcd, then the next task executor of the same task can continue to process data from the checkpoint.
	- **code change required**: all the task executor extends `TaskExecutor` need to change the parent class to `NormalTaskExecutor`
	- if user wants to use checkpoint executor, it should extends `CheckpointTaskExecutor`
2. refactor ban function, only covered ban level can be banned: this feature changes the ban logic and behaviour.
	- there should be no usage of ban feature, so no change needed
3. embed job/state controller into infra: internally build the api controllers in infra level, so application can simply support the TasCreed apis.

### from 0.2.4 to 0.2.5
- no mandatory code change required for compatibility

### from 0.2.5 to 0.2.6
1. trait to support ignorable, archive, delete, canFail features: this feature implements the `ignore step`, `archive task`, `delete task`, `step can fail` features in trait mode. Actually all these features are not supported in 0.2.3 except the `ignore step`, but the implementation is backward compatible, so no code change required.
2. multiple dependent steps to support DAG, verify DAG of job define: this feature supports multiple dependent steps instead of only one dependent step. The implementation is backward compatible, so no code change required.
3. redefine job/step/task states and state transfer logic: this feature redefines the job/step/task states and logic, it defines a full state machine, users can refer to [this document](../feature/States.md). The users of previous versions only generate the `SUCCESS` task state as result, so it is backward compatible. 
	- From this version on, users can generate the other task states like `FAILED` and `ERROR`, and the behaviour of these abnormal states are safely defined, users need to carefully read the document before use the states.

### from 0.2.6 to 0.2.7
1. enrich job and step validation: this feature validates the job and step defines and requests in a more strict way, like the naming convention, etc. For the normal cases, there should be no change, only if the job define doesn't follow the normal way, for example, the job name with strange charactor. If the job define can not be successfully parsed in dev or staging env, users need to change it.

### from 0.2.7 to 0.2.8
1. accelerate job refresh frequency
2. enhancement of steps
	- enrich step states
	- step max error time can be configured
	- dependent step states can be passed to the following steps
	- DAG can be separated by different phases

### from 0.2.8 to 0.2.9
1. abstract archive storages, ES is only one of the choice, users can choose other archive storages, or multiple ones.
2. enhance monitor metrics of task retry and execution time exceed.
3. task occupation can be checked by user in code.

### from 0.2.9 to 0.3.0
1. a TasCreed dedicated ES cluster is applied, the TasCreed applications will access to it by default. Also, you can still choose to configure for your specific ES cluster. 
2. step max task count and max pick times can be updated even it is still running.

**Please be aware that, from this version on, the TasCreed jobs will be archived to the dedicated ES cluster, instead of the previous shared ES cluster; so all the archived historical jobs can not be found in the new ES cluster.**

You are free to choose the new specific ES cluster or the previous shared one.
- If you choose the default new ES, the dependency and esams key access should be applied. (To apply the key `/xfasdatadumper/tascreed/es/auth` in Fidelius production environment)
- If you choose the previous ES, you just need to explicitly configure the endpoint and related parameters of the old ES in your application properties file to overwrite the ES related configuration in TasCreed infra.

### from 0.3.0 to 0.3.1
1. [routine](../feature/Routine.md) introduced, for the long run jobs across the whole cluster.
2. done range state of pack/shard step defined, users can get the progress of the step, even generate watermark for the pack step.
3. worker count per host can be updated in etcd on the fly, no need to restart.
4. [annotation](../feature/Annotation.md) to simplify the executor register code effort
5. **code change required:** In the `run` method of user's `Application` class, the `TcRunner` start up way changes, from 1 step to 2 steps.

Sample code
```java
    @Override
    public void run(ApplicationArguments args) {
        // TasCreed registers all the executors by annotations
        tcRunner.init();

        // users can manually register or overwrite the executors before TasCreed runner start
        registerTaskExecutors();

        // TasCreed runner start
        tcRunner.start();
    }
```

In this way, users can overwrite the executors registered by code (happens in `init` method), which is more flexible.

### from 0.3.1 to 0.3.2
1. routine adoption can be checked via state controller
2. infinite retry when read global config from etcd
3. [node duty rules](../feature/NodeDuty.md) for node validity control

### from 0.3.2 to 0.3.3
1. enable min valid app version control feature
2. version comparison use maven ComparableVersion for better compatibility

Details can be found in this [document](../feature/NodeDuty.md).

### from 0.3.3 to 0.3.4
1. invalid node regex in node duty rule
2. sample watermark routine
3. change infinite read global config from etcd to 5 times retry, it can throw exception if any error encountered
4. get routine trigger information in executor
5. biz error or exception alert from infra

### from 0.3.4 to 0.3.5
1. make cal logger optional, default logger can be configured by property
2. abstract es util and etcd util interfaces, default implementation depends on ebay environment
3. build time schedule module
4. node duty of schedule server

### from 0.3.5 to 0.4.0 (latest version)
1. refactor etcd util
2. TasCreed api spec
3. refactor TasCreed dependency lib, ebay related dependency can be replaced by external users
4. ebay related configuration wrapped in one file
5. refactor to extract ebay related dependency from infra package

**code change required**: 
eBay users need to change the configuration file name of es and etcd in the application class. For example:
```java
public class TasCreedSampleApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(TasCreedSampleApplication.class)
                .properties("spring.config.name:fasrt-logger,fasrt-commons,etcd,tascreed-ebay,tascreed,application")
                .build()
                .run(args);
    }
}
```
The configuration files `tascreed-es`, `tascreed-etcd` are wrapped into one file `tascreed-ebay`.

**pom file change required**:
eBay users need to add another dependency of eBay related external storage implementation, including es, etcd, cal, etc. For example:
```pom
<dependency>
    <groupId>com.ebay.magellan</groupId>
    <artifactId>tascreed-ext-ebay</artifactId>
    <version>${tascreed.version}</version>
</dependency>
```

Also, from this version on, TasCreed sample app can run without real deployment of etcd or es, there is a default in-memory implementation of etcd, and in-memory implementation of es, developers can debug or run the sample app at local. You can refer to [environment configuration](../config/EnvConfig.md).
