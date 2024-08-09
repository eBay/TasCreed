package com.ebay.magellan.tascreed.core.domain.util;

import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.state.JobStateEnum;
import com.ebay.magellan.tascreed.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JsonUtilTest {

    private String jobStr = "{\"progression\":\"50.00%\",\"jobName\":\"sample-dag\",\"trigger\":\"20211013-4\",\"priority\":10,\"updatedParams\":{\"count\":\"3\"},\"steps\":[{\"progression\":\"100.00%\",\"stepName\":\"prep\",\"state\":\"SUCCESS\",\"taskStates\":{}},{\"progression\":\"100.00%\",\"stepName\":\"calc-1\",\"state\":\"SUCCESS\",\"taskStates\":{}},{\"progression\":\"0.00%\",\"stepName\":\"calc-2\",\"state\":\"FAILED\",\"taskStates\":{\"-1\":\"FAILED\"}},{\"progression\":\"100.00%\",\"stepName\":\"calc-3\",\"state\":\"SUCCESS\",\"taskStates\":{}},{\"progression\":\"0.00%\",\"stepName\":\"aggr-1\",\"state\":\"SKIP_BY_FAILED\"},{\"progression\":\"0.00%\",\"stepName\":\"aggr-2\",\"state\":\"SKIP_BY_ERROR\"}],\"state\":\"FAILED\",\"traits\":[\"DELETED\"],\"createTime\":\"2021-10-14T09:22:26.997Z\",\"modifyTime\":\"2021-10-14T09:23:30.680Z\"}";
    private String taskStr = "{\"jobName\":\"sample\",\"trigger\":\"20211013-10\",\"priority\":10,\"stepName\":\"aggr-1\",\"params\":{\"p1\":\"0\",\"count\":\"3\"},\"updatedParams\":{\"count\":\"4\"},\"traits\":[\"ARCHIVE\"],\"result\":{\"state\":\"SUCCESS\",\"reason\":\"\"},\"createTime\":\"2021-10-14T09:21:14.418Z\",\"modifyTime\":\"2021-10-14T09:21:20.830Z\",\"modifyThread\":\"LM-SHC-16507966/tascreed-task-worker-thread-1\"}";

    @Test
    public void parseJob() {
        Job job = JsonUtil.parseJob(jobStr);
        assertNotNull(job);
        assertEquals("sample-dag", job.getJobName());
        assertEquals("20211013-4", job.getTrigger());
        assertEquals(Integer.valueOf(10), job.getPriority());
        assertEquals("50.00%", job.getProgression().getValue());
        assertEquals(JobStateEnum.FAILED, job.getState());
    }

    @Test
    public void parseJobs() {
        List<String> strs = new ArrayList<>();
        strs.add(jobStr);
        strs.add(jobStr);
        strs.add(jobStr);
        List<Job> jobs = JsonUtil.parseJobs(strs);
        assertEquals(3, jobs.size());
    }

    @Test
    public void parseTask() {
        Task task = JsonUtil.parseTask(taskStr);
        assertNotNull(task);
        assertEquals("sample", task.getJobName());
        assertEquals("aggr-1", task.getStepName());
        assertEquals(TaskStateEnum.SUCCESS, task.getTaskState());
        assertEquals("LM-SHC-16507966/tascreed-task-worker-thread-1", task.getMidState().getModifyThread());
    }

    @Test
    public void parseTasks() {
        List<String> strs = new ArrayList<>();
        strs.add(taskStr);
        strs.add(taskStr);
        strs.add(taskStr);
        List<Task> tasks = JsonUtil.parseTasks(strs);
        assertEquals(3, tasks.size());
    }
}
