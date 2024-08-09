package com.ebay.magellan.tascreed.core.domain.task;

import com.ebay.magellan.tascreed.core.domain.state.TaskStateEnum;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TaskTest {

    @Test
    public void testJson() throws Exception {
        String s = "{\"jobName\":\"sample\",\"trigger\":\"20211013-10\",\"priority\":10,\"stepName\":\"aggr-1\",\"params\":{\"p1\":\"0\",\"count\":\"3\"},\"updatedParams\":{\"count\":\"4\"},\"traits\":[\"ARCHIVE\"],\"result\":{\"state\":\"SUCCESS\",\"reason\":\"\"},\"createTime\":\"2021-10-14T09:21:14.418Z\",\"modifyTime\":\"2021-10-14T09:21:20.830Z\",\"modifyThread\":\"LM-SHC-16507966/tascreed-task-worker-thread-1\"}";
        Task t = Task.fromJson(s);

        assertEquals("sample", t.getJobName());
        assertEquals("aggr-1", t.getStepName());
        assertEquals(TaskStateEnum.SUCCESS, t.getTaskState());

        System.out.println(t.getTaskFullName());
        System.out.println(t.toJson(TaskViews.TASK_TODO.class));
        System.out.println(t.toJson(TaskViews.TASK_DONE.class));
    }
}
