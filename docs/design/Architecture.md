# Architecture

``` mermaid
flowchart LR
    subgraph node[App Node]
        server[Job Server]
        worker[Task Worker]
    end
    
    user(User)
    bulletin[Bulletin]
    archive[Archive]

    user -->|"submit job request"| server
    
    server -->|"create job"| bulletin
    server -->|"proceed job"| bulletin
    server -->|"archive job"| archive
    
    worker -->|"occupy task"| bulletin
    worker -->|"execute task"| worker
    worker -->|"update task"| bulletin
```

## Component

### Job Server
Job server manages the lifecycle of jobs and tasks. 

| Action      | When                     | How                                                                  |
|-------------|--------------------------|----------------------------------------------------------------------|
| create job  | user submits job request | find job define, create a new job instance                           |
| proceed job | periodically             | fetch job and done tasks, update job status, create subsequent tasks |
| archive job | periodically             | fetch done jobs and tasks, archive them                              |

### Task Worker
Task worker executes tasks, and updates task status.

| Action       | When         | How                                                           |
|--------------|--------------|---------------------------------------------------------------|
| occupy task  | periodically | fetch todo tasks, occupy a task; keep heartbeat of occupation |
| execute task | task adopted | execute task internally                                       |
| update task  | task done    | update task status                                            |

???+ question "Why not assign task to worker?"
    In some other task execution framework, a leader role (similar to "job server") will assign a task to specific worker.  
    By managing the aliveness of all workers, the leader can assign tasks flexibly, such as considering resource capacity, network affinity. However, the leader role also introduces more complexity of design and implementation.

    To simplify the system, TasCreed adopts a leaderless design, each node has the same functionality, works independently.
    Each worker occupies tasks by itself, which makes it easy to implement, deploy, and scale.

### Storage
There are two kinds of storages, bulletin and archive.

- Bulletin stores the status of alive jobs and tasks, as well as the task occupations.
- Archive stores the records of done jobs and tasks.

???+ question "What kind of storage can work as bulletin?"
    Most storages can easily store the status of alive jobs and tasks, but for task occupation, the solutions would be different.

    - In traditional databses, task occupation can be stored in a dedicated table, then orphan record would exist if worker crashes.
    - A timeout mechanism helps, the worker keeps refresh the aliveness of the occupation, then orphan record can be cleaned or easily recognized if worker crashes.  

    Therefore, the bulletin can be implemented by lots of storages. We've chosen etcd by default, for its native feature of lease.

???+ question "What kind of storage can work as archive?"
    Most storages can support the archive requirement, thus there could be lots of options.

## Workflow

### Job Trigger
``` mermaid
flowchart LR
    user(User) -->|"1. submit job request"| server[Job Server]
    server -->|"2. create new job"| server
    server -->|"3. write new job"| bulletin[Bulletin]
```

1. User submit a job request
2. Job server validate the request, find the job define, and create a new job
3. Job server write the new job to bulletin
    - If the job is a duplicate one, it will be rejected
    - A job is non-duplicate only if it can not be found in both bulletin and archive

### Task Execution
``` mermaid
flowchart LR
    worker[Task Worker] -.->|"1. fetch todo tasks and occupations"| bulletin[Bulletin]
    worker -->|"2. occupy a task and keep heartbeat"| bulletin
    worker -->|"3. execute task"| worker
    worker -->|"4. update task status"| bulletin
```

1. Worker fetch todo tasks and current occupations from bulletin
2. Worker pick a valid todo task, occupy it in bulletin, periodically heartbeat to keep the occupation
3. Worker execute the task
4. After execution, worker update the task status to done, and remove the occupation

### Job Update
``` mermaid
flowchart LR
    server[Job Server] -.->|"1. fetch alive jobs and done tasks"| bulletin[Bulletin]
    server -->|"2. update job status, create new todo tasks"| bulletin
```

1. Server fetch alive jobs and done tasks from bulletin
2. Server update the job status by done tasks, and create more executable tasks

## Thread model
In each TasCreed node, there are some threads to handle the job and task lifecycle.

| Thread       | Description                                                        |
|--------------|--------------------------------------------------------------------|
| job server   | when receive a job request, create new job                         |
| job watcher  | periodically update job status, create new tasks                   |
| task watcher | periodically occupy task, build worker thread                      |
| task worker  | execute task, with a companion heartbeat thread to keep occupation |

!!! note "Note"
    The thread model just illustrates the design principle, not the exact implementation. For example, the job watcher is implemented as a routine worker across the whole cluster.

