package com.ebay.magellan.tascreed.core.infra.controller;

import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.request.JobRequest;
import com.ebay.magellan.tascreed.core.infra.jobserver.JobServer;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/job")
public class JobController  {

    @Autowired
    private JobServer jobServer;

    // -----

    @PostMapping("")
    public Job submitJob(@Valid @RequestBody JobRequest jr) throws TumblerException {
        Job job = jobServer.submitJobRequest(jr);
        return job;
    }

    @GetMapping("/{jobName}/{trigger}")
    public Job queryJob(@NotBlank @PathVariable("jobName") String jobName,
                        @NotBlank @PathVariable("trigger") String trigger) {
        Job job = jobServer.findJobByJobNameAndTrigger(jobName, trigger);
        return job;
    }

    // -----

    @PostMapping("/update")
    public Job updateAliveJob(@Valid @RequestBody JobRequest jr) throws TumblerException {
        Job job = jobServer.updateAliveJob(jr);
        return job;
    }

    // -----

    @PostMapping("/retry")
    public Job retryAliveErrorJob(@Valid @RequestBody JobRequest jr) throws TumblerException {
        Job job = jobServer.retryAliveErrorJob(jr);
        return job;
    }

}
