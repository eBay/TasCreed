package com.ebay.magellan.tumbler.core.domain.util;

import com.ebay.magellan.tumbler.core.domain.job.Job;
import com.ebay.magellan.tumbler.core.domain.task.Task;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FilterUtil {

    public static List<Job> filterJobs(List<Job> jobs,
                                       Optional<String> jobNameOpt,
                                       Optional<String> triggerOpt) {
        List<Job> list = new ArrayList<>();
        for (Job job : jobs) {
            if (job == null) continue;
            if (jobNameOpt.isPresent() && !StringUtils.equals(jobNameOpt.get(), job.getJobName())) continue;
            if (triggerOpt.isPresent() && !StringUtils.equals(triggerOpt.get(), job.getTrigger())) continue;
            list.add(job);
        }
        return list;
    }

    public static List<Task> filterTasks(List<Task> tasks,
                                         Optional<String> jobNameOpt,
                                         Optional<String> triggerOpt,
                                         Optional<String> stepNameOpt) {
        List<Task> list = new ArrayList<>();
        for (Task task : tasks) {
            if (task == null) continue;
            if (jobNameOpt.isPresent() && !StringUtils.equals(jobNameOpt.get(), task.getJobName())) continue;
            if (triggerOpt.isPresent() && !StringUtils.equals(triggerOpt.get(), task.getTrigger())) continue;
            if (stepNameOpt.isPresent() && !StringUtils.equals(stepNameOpt.get(), task.getStepName())) continue;
            list.add(task);
        }
        return list;
    }
}
