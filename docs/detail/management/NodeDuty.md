# Node Duty

The TasCreed application can be deployed across different data centers, on multiple nodes. Each node can perform differently if: 

- during rollout of a new release or any unknown deployment issue, there could be several versions of manifests deployed at the same time;
- nodes in different data centers perform differently due to network locality.

Users might need to control the node responsibilities differently, based on some conditions, such as manifest version, host name, etc.

To support such fine-grained management, we've defined the node duties and the control rules, to guide the TasCreed application nodes work or not.

## Node duty

The responsibility of a TasCreed node is defined as a set of duties, which can be enabled or disabled separately.

``` mermaid
flowchart TD
    subgraph ALL
        subgraph SERVER
            JOB_SERVER
            STATE_SERVER
            SCHEDULE_SERVER
        end
        subgraph EXECUTOR
            TASK_EXECUTOR
            ROUTINE_EXECUTOR
        end
    end
```

The containment relationship of these duties are already depicted in the diagram. 

The child duties controls specific functionalities:

- `JOB_SERVER`: manages the APIs of job lifecycle; if disabled on a node, it can not serve for job APIs, and can not refresh job state in background
- `STATE_SERVER`: manages the other APIs; if disabled on a node, it can not serve for the functionalities of ban, delete adoption/task/job, etc.
- `SCHEDULE_SERVER`: manages the APIs of schedule lifecycle; if disabled on a node, it can not serve for schedule APIs
- `TASK_EXECUTOR`: manages the execution of tasks; if disabled on a node, it can not occupy/execute any task
- `ROUTINE_EXECUTOR`: manages the execution of routines; if disabled on a node, it can not occupy/execute any routine

For simplicity, duties can be controlled at a higher level.

- `SERVER`: includes `JOB_SERVER`, `STATE_SERVER`, `SCHEDULE_SERVER`
- `EXECUTOR`: includes `TASK_EXECUTOR` and `ROUTINE_EXECUTOR`
- `ALL`: includes `SERVER` and `EXECUTOR`

!!! tip "`GET` APIs still works"
    When a node is disabled for some specific server duties, only the writable APIs are disabled, it can still serve for the `GET` APIs.

## Node duty rule

A node duty rule defines the conditions to filter out the invalid nodes, as well as the duties to be disabled on them.

Example

``` json
{
  "minValidTcVersion": "0.3.2-RELEASE",
  "validHostNameRegex": "lvs-.*",
  "disableDutiesIfInvalid": ["SERVER", "TASK_EXECUTOR"]
}
```

The `disableDutiesIfInvalid` describes the duties to be disabled on invalid nodes. The other fields intend to find out the invalid node.

### Version conditions

- `minValidTcVersion`: the minimum valid TasCreed version; a node is invalid if the TasCreed version is smaller than it.
- `minValidAppVersion`: the minimum valid application version; a node is invalid if the application version is smaller than it.

???+ question "Why different version conditions?"
    Typically, the TasCreed is integrated as a library in the user's application.  
    Therefore, we provide two version conditions, to check the version of the integrated TasCreed library, or user's application itself.

!!! note "Requirement to get the application version"
    TasCreed infra reads the user application version by `org.springframework.boot.info.BuildProperties`.  
    Therefore, the pom file of user's application should contain the `spring-boot-maven-plugin` with the `build-info` goal. For example,
    ``` xml
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <executions>
            <execution>
                <id>build-info</id>
                <goals>
                    <goal>build-info</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
    ```
    Otherwise, the application version will be identified as `null`, if you've configured `minValidAppVersion` with a non-blank string, all your nodes will be identified as invalid.

### Host name conditions

- `validHostNameRegex`: the valid host name regex; a node is invalid if its host name can not match the regex.
- `invalidHostNameRegex`: the invalid host name regex; a node is invalid if its host name matches the regex.
