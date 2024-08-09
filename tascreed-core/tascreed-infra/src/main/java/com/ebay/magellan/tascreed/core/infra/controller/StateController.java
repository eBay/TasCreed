package com.ebay.magellan.tascreed.core.infra.controller;

import com.ebay.magellan.tascreed.core.domain.duty.NodeDutyRules;
import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.infra.jobserver.StateServer;
import com.ebay.magellan.tascreed.depend.common.collection.KeyValuePair;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/state")
public class StateController {

    @Autowired
    private StateServer stateServer;

    // -----

    @GetMapping("/aliveJobs")
    public List<Job> aliveJobs(@RequestParam(name="jobName",required=false) String jobName,
                               @RequestParam(name="trigger",required=false) String trigger) throws Exception {
        List<Job> jobs = stateServer.findAliveJobs(Optional.ofNullable(jobName), Optional.ofNullable(trigger));
        return jobs;
    }

    @GetMapping("/todoTasks")
    public List<Task> todoTasks(@RequestParam(name="jobName",required=false) String jobName,
                                @RequestParam(name="trigger",required=false) String trigger,
                                @RequestParam(name="stepName",required=false) String stepName) throws Exception {
        List<Task> tasks = stateServer.findTodoTasks(Optional.ofNullable(jobName),
                Optional.ofNullable(trigger), Optional.ofNullable(stepName));
        return tasks;
    }

    @GetMapping("/doneTasks")
    public List<Task> doneTasks(@RequestParam(name="jobName",required=false) String jobName,
                                @RequestParam(name="trigger",required=false) String trigger,
                                @RequestParam(name="stepName",required=false) String stepName) throws Exception {
        List<Task> tasks = stateServer.findDoneTasks(Optional.ofNullable(jobName),
                Optional.ofNullable(trigger), Optional.ofNullable(stepName));
        return tasks;
    }

    @GetMapping("/errorTasks")
    public List<Task> errorTasks(@RequestParam(name="jobName",required=false) String jobName,
                                 @RequestParam(name="trigger",required=false) String trigger,
                                 @RequestParam(name="stepName",required=false) String stepName) throws Exception {
        List<Task> tasks = stateServer.findErrorTasks(Optional.ofNullable(jobName),
                Optional.ofNullable(trigger), Optional.ofNullable(stepName));
        return tasks;
    }

    // -----

    @GetMapping("/adoptions")
    public List<KeyValuePair> taskAdoptions(@RequestParam(name="filter",required=false) String filter) throws Exception {
        List<KeyValuePair> pairs = stateServer.findTaskAdoptions(Optional.ofNullable(filter));
        return pairs;
    }

    @GetMapping("/adoption/{jobName}/{trigger}/{taskName}")
    public KeyValuePair<String, String> taskAdoption(@NotBlank @PathVariable("jobName") String jobName,
                                                     @NotBlank @PathVariable("trigger") String trigger,
                                                     @NotBlank @PathVariable("taskName") String taskName) {
        KeyValuePair<String, String> pair = stateServer.findTaskAdoption(jobName, trigger, taskName);
        return pair;
    }

    @DeleteMapping("/adoption/{jobName}/{trigger}/{taskName}")
    public KeyValuePair<String, String> deleteTaskAdoption(@NotBlank @PathVariable("jobName") String jobName,
                                                           @NotBlank @PathVariable("trigger") String trigger,
                                                           @NotBlank @PathVariable("taskName") String taskName) throws Exception {
        KeyValuePair<String, String> pair = stateServer.deleteTaskAdoption(jobName, trigger, taskName);
        return pair;
    }

    // -----

    @GetMapping("/routine-adoptions")
    public List<KeyValuePair> routineAdoptions(@RequestParam(name="filter",required=false) String filter) throws Exception {
        List<KeyValuePair> pairs = stateServer.findRoutineAdoptions(Optional.ofNullable(filter));
        return pairs;
    }

    @GetMapping("/routine-adoption/{routineFullName}")
    public KeyValuePair<String, String> routineAdoption(@NotBlank @PathVariable("routineFullName") String routineFullName) throws Exception {
        KeyValuePair<String, String> pair = stateServer.findRoutineAdoption(routineFullName);
        return pair;
    }

    @DeleteMapping("/routine-adoption/{routineFullName}")
    public KeyValuePair<String, String> deleteRoutineAdoption(@NotBlank @PathVariable("routineFullName") String routineFullName) throws Exception {
        KeyValuePair<String, String> pair = stateServer.deleteRoutineAdoption(routineFullName);
        return pair;
    }

    @DeleteMapping("/todoTask/{jobName}/{trigger}/{taskName}")
    public Task deleteTodoTask(@NotBlank @PathVariable("jobName") String jobName,
                               @NotBlank @PathVariable("trigger") String trigger,
                               @NotBlank @PathVariable("taskName") String taskName) throws Exception {
        Task task = stateServer.deleteTodoTask(jobName, trigger, taskName);
        return task;
    }

    @DeleteMapping("/aliveJob/{jobName}/{trigger}")
    public Job deleteAliveJob(@NotBlank @PathVariable("jobName") String jobName,
                              @NotBlank @PathVariable("trigger") String trigger,
                              @RequestParam(name="archive",required=false) Boolean archive) throws Exception {
        Job job = stateServer.deleteAliveJob(jobName, trigger, archive);
        return job;
    }

    // -----

    @PutMapping("/ban/global")
    public Boolean banGlobal(@RequestParam(name="level",required=false) String level) throws Exception {
        Boolean ret = stateServer.banGlobal(level);
        return ret;
    }

    @PutMapping("/resume/global")
    public Boolean resumeGlobal() throws Exception {
        Boolean ret = stateServer.resumeGlobal();
        return ret;
    }

    @PutMapping("/ban/jobDefine/{jobDefineName}")
    public Boolean banJobDefine(@NotBlank @PathVariable("jobDefineName") String jobDefineName,
                                @RequestParam(name="level",required=false) String level) throws Exception {
        Boolean ret = stateServer.banJobDefine(jobDefineName, level);
        return ret;
    }

    @PutMapping("/resume/jobDefine/{jobDefineName}")
    public Boolean resumeJobDefine(@NotBlank @PathVariable("jobDefineName") String jobDefineName) throws Exception {
        Boolean ret = stateServer.resumeJobDefine(jobDefineName);
        return ret;
    }

    @PutMapping("/ban/job/{jobName}/{trigger}")
    public Boolean banJob(@NotBlank @PathVariable("jobName") String jobName,
                          @NotBlank @PathVariable("trigger") String trigger,
                          @RequestParam(name="level",required=false) String level) throws Exception {
        Boolean ret = stateServer.banJob(jobName, trigger, level);
        return ret;
    }

    @PutMapping("/resume/job/{jobName}/{trigger}")
    public Boolean resumeJob(@NotBlank @PathVariable("jobName") String jobName,
                             @NotBlank @PathVariable("trigger") String trigger) throws Exception {
        Boolean ret = stateServer.resumeJob(jobName, trigger);
        return ret;
    }

    // -----

    @PutMapping("/ban/routineDefine/{routineName}")
    public Boolean banRoutineDefine(@NotBlank @PathVariable("routineName") String routineName,
                                    @RequestParam(name="level",required=false) String level) throws Exception {
        Boolean ret = stateServer.banRoutineDefine(routineName, level);
        return ret;
    }

    @PutMapping("/resume/routineDefine/{routineName}")
    public Boolean resumeRoutineDefine(@NotBlank @PathVariable("routineName") String routineName) throws Exception {
        Boolean ret = stateServer.resumeRoutineDefine(routineName);
        return ret;
    }

    @PutMapping("/ban/routine/{routineFullName}")
    public Boolean banRoutine(@NotBlank @PathVariable("routineFullName") String routineFullName,
                              @RequestParam(name="level",required=false) String level) throws Exception {
        Boolean ret = stateServer.banRoutine(routineFullName, level);
        return ret;
    }

    @PutMapping("/resume/routine/{routineFullName}")
    public Boolean resumeRoutine(@NotBlank @PathVariable("routineFullName") String routineFullName) throws Exception {
        Boolean ret = stateServer.resumeRoutine(routineFullName);
        return ret;
    }

    // -----

    @GetMapping("/archivedTask/{jobName}/{trigger}/{taskName}")
    public Task queryArchivedTask(@NotBlank @PathVariable("jobName") String jobName,
                                  @NotBlank @PathVariable("trigger") String trigger,
                                  @NotBlank @PathVariable("taskName") String taskName) {
        Task task = stateServer.findArchivedTask(jobName, trigger, taskName);
        return task;
    }

    // -----

    @GetMapping("/duty/rules/global")
    public NodeDutyRules readDutyRules(@RequestParam(name="forceRefresh",required=false) Boolean forceRefresh) throws Exception {
        NodeDutyRules ret = stateServer.readDutyRules(forceRefresh);
        return ret;
    }

    @PutMapping("/duty/rules/global")
    public NodeDutyRules submitDutyRules(@Valid @RequestBody NodeDutyRules nodeDutyRules) throws Exception {
        NodeDutyRules ret = stateServer.submitDutyRules(nodeDutyRules);
        return ret;
    }

    @DeleteMapping("/duty/rules/global")
    public Boolean deleteDutyRules() throws Exception {
        Boolean ret = stateServer.deleteDutyRules();
        return ret;
    }
}
