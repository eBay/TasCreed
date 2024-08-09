package com.ebay.magellan.tascreed.core.domain.util;

import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.schedule.Schedule;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonUtil {

    // -----

    // only parse from json string
    public static Job parseJob(String str) {
        if (StringUtils.isNotBlank(str)) {
            try {
                return Job.fromJson(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<Job> parseJobs(Collection<String> strs) {
        List<Job> jobs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(strs)) {
            for (String str : strs) {
                Job job = parseJob(str);
                if (job != null) {
                    jobs.add(job);
                }
            }
        }
        return jobs;
    }

    // -----

    // only parse from json string
    public static Task parseTask(String str) {
        if (StringUtils.isNotBlank(str)) {
            try {
                return Task.fromJson(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<Task> parseTasks(Collection<String> strs) {
        List<Task> tasks = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(strs)) {
            for (String str : strs) {
                Task task = parseTask(str);
                if (task != null) {
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    // -----

    // only parse from json string
    public static Schedule parseSchedule(String str) {
        if (StringUtils.isNotBlank(str)) {
            try {
                return Schedule.fromJson(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<Schedule> parseSchedules(Collection<String> strs) {
        List<Schedule> schedules = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(strs)) {
            for (String str : strs) {
                Schedule schedule = parseSchedule(str);
                if (schedule != null) {
                    schedules.add(schedule);
                }
            }
        }
        return schedules;
    }

    // -----
}
