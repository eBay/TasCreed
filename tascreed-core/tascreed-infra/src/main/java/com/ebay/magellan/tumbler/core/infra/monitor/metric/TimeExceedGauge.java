package com.ebay.magellan.tumbler.core.infra.monitor.metric;

import com.ebay.magellan.tumbler.core.domain.job.Job;
import com.ebay.magellan.tumbler.core.domain.task.Task;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Getter
public class TimeExceedGauge {

    private int numTimeExceedJobs;
    private int numTimeExceedTasks;

    // -----

    public void updateByAliveJobs(List<Job> jobs) {
        this.numTimeExceedJobs = 0;
        long now = System.currentTimeMillis();
        if (CollectionUtils.isNotEmpty(jobs)) {
            for (Job job : jobs) {
                if (job == null) continue;
                if (job.getMidState().getDuration() == null) continue;
                if (job.getMidState().getCreateTime() == null) continue;
                long duration = job.getMidState().getDuration();
                long st = job.getMidState().getCreateTime().getTime();
                long exeTime = now - st;
                if (exeTime > duration) {
                    numTimeExceedJobs++;
                }
            }
        }
    }

    public void updateByAliveTasks(List<Task> tasks) {
        this.numTimeExceedTasks = 0;
        long now = System.currentTimeMillis();
        if (CollectionUtils.isNotEmpty(tasks)) {
            for (Task task : tasks) {
                if (task == null) continue;
                if (task.getMidState().getDuration() == null) continue;
                if (task.getMidState().getCreateTime() == null) continue;
                long duration = task.getMidState().getDuration();
                long st = task.getMidState().getCreateTime().getTime();
                long exeTime = now - st;
                if (exeTime > duration) {
                    numTimeExceedTasks++;
                }
            }
        }
    }

    // -----
}
