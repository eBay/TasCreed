package com.ebay.magellan.tumbler.core.domain.util;

import com.ebay.magellan.tumbler.core.domain.job.Job;
import com.ebay.magellan.tumbler.core.domain.task.Task;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class FilterUtilTest {

    Job buildJob(String jobName, String trigger) {
        Job job = new Job();
        job.setJobName(jobName);
        job.setTrigger(trigger);
        return job;
    }

    Task buildTask(String jobName, String trigger, String stepName) {
        Task task = new Task();
        task.setJobName(jobName);
        task.setTrigger(trigger);
        task.setStepName(stepName);
        return task;
    }

    @Test
    public void filterJobs1() {
        List<Job> jobs = new ArrayList<>();
        jobs.add(buildJob("j1", "t1"));
        jobs.add(buildJob("j2", "t2"));
        jobs.add(buildJob("j3", "t3"));

        List<Job> filteredJobs = FilterUtil.filterJobs(jobs, Optional.empty(), Optional.empty());
        assertEquals(3, filteredJobs.size());
    }

    @Test
    public void filterJobs2() {
        List<Job> jobs = new ArrayList<>();
        jobs.add(buildJob("j1", "t1"));
        jobs.add(buildJob("j2", "t2"));
        jobs.add(buildJob("j3", "t3"));

        List<Job> filteredJobs = FilterUtil.filterJobs(jobs, Optional.of("j1"), Optional.empty());
        assertEquals(1, filteredJobs.size());
        assertEquals("j1", filteredJobs.get(0).getJobName());
        assertEquals("t1", filteredJobs.get(0).getTrigger());
    }

    @Test
    public void filterJobs3() {
        List<Job> jobs = new ArrayList<>();
        jobs.add(buildJob("j1", "t1"));
        jobs.add(buildJob("j2", "t2"));
        jobs.add(buildJob("j3", "t3"));

        List<Job> filteredJobs = FilterUtil.filterJobs(jobs, Optional.of("j1"), Optional.of("t1"));
        assertEquals(1, filteredJobs.size());
        assertEquals("j1", filteredJobs.get(0).getJobName());
        assertEquals("t1", filteredJobs.get(0).getTrigger());
    }

    @Test
    public void filterJobs4() {
        List<Job> jobs = new ArrayList<>();
        jobs.add(buildJob("j1", "t1"));
        jobs.add(buildJob("j2", "t2"));
        jobs.add(buildJob("j3", "t3"));

        assertEquals(0, FilterUtil.filterJobs(jobs, Optional.of("j1"), Optional.of("t3")).size());
        assertEquals(0, FilterUtil.filterJobs(jobs, Optional.of("j4"), Optional.empty()).size());
    }

    @Test
    public void filterTasks1() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(buildTask("j1", "t1", "s1"));
        tasks.add(buildTask("j1", "t1", "s2"));
        tasks.add(buildTask("j1", "t1", "s3"));
        tasks.add(buildTask("j2", "t2", "s1"));
        tasks.add(buildTask("j2", "t2", "s2"));
        tasks.add(buildTask("j2", "t2", "s3"));

        assertEquals(0, FilterUtil.filterTasks(tasks, Optional.of("j0"), Optional.of("t1"), Optional.of("s1")).size());
        assertEquals(1, FilterUtil.filterTasks(tasks, Optional.of("j1"), Optional.of("t1"), Optional.of("s1")).size());
        assertEquals(3, FilterUtil.filterTasks(tasks, Optional.of("j1"), Optional.of("t1"), Optional.empty()).size());
        assertEquals(1, FilterUtil.filterTasks(tasks, Optional.of("j2"), Optional.empty(), Optional.of("s1")).size());
        assertEquals(6, FilterUtil.filterTasks(tasks, Optional.empty(), Optional.empty(), Optional.empty()).size());
    }
}
