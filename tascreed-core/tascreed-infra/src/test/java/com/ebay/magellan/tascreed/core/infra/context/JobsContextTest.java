package com.ebay.magellan.tascreed.core.infra.context;

import com.ebay.magellan.tascreed.core.domain.job.Job;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JobsContextTest {

    Job buildJob(String name) {
        Job job = new Job();
        job.setJobName(name);
        return job;
    }

    Map<String, Job> buildJobs() {
        Map<String, Job> map = new HashMap<>();
        map.put("j1", buildJob("j1"));
        map.put("j4", buildJob("j4"));
        map.put("j3", buildJob("j3"));
        map.put("j2", buildJob("j2"));
        return map;
    }

    @Test
    public void testJobsContext() {
        Map<String, Job> map = buildJobs();
        JobsContext jc = JobsContext.init(map);

        assertEquals(map, jc.getAllJobsMap());
        assertEquals(4, jc.getAllJobs().size());
        assertEquals("j1", jc.getAllJobs().get(0).getJobName());
        assertEquals("j2", jc.getAllJobs().get(1).getJobName());
        assertEquals("j3", jc.getAllJobs().get(2).getJobName());
        assertEquals("j4", jc.getAllJobs().get(3).getJobName());
    }
}
