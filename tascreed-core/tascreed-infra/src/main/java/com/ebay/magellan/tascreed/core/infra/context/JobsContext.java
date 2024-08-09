package com.ebay.magellan.tascreed.core.infra.context;

import com.ebay.magellan.tascreed.core.domain.job.Job;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class JobsContext {
    private Map<String, Job> allJobsMap;
    private List<Job> allJobs = new ArrayList<>();

    private JobsContext(Map<String, Job> allJobsMap) {
        this.allJobsMap = (allJobsMap != null ? allJobsMap : new HashMap<>());
        buildAllJobs();
    }

    public static JobsContext init(Map<String, Job> allJobsMap) {
        return new JobsContext(allJobsMap);
    }

    // -----

    private void buildAllJobs() {
        allJobs.clear();
        List<String> keys = allJobsMap.keySet().stream().sorted().collect(Collectors.toList());
        for (String key : keys) {
            Job job = allJobsMap.get(key);
            if (job != null) {
                allJobs.add(job);
            }
        }
    }
}
