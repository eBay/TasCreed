package com.ebay.magellan.tascreed.core.infra.jobserver.notify;

import com.ebay.magellan.tascreed.core.domain.builder.JobBuilder;
import com.ebay.magellan.tascreed.core.domain.builder.TaskBuilder;
import com.ebay.magellan.tascreed.core.domain.task.TaskViews;
import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.job.JobInstKey;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.infra.constant.TumblerConstants;
import com.ebay.magellan.tascreed.core.infra.help.TestRepo;
import com.ebay.magellan.tascreed.core.infra.jobserver.JobServer;
import com.ebay.magellan.tascreed.core.infra.repo.JobDefineRepo;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.JobBulletin;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.TaskBulletin;
import com.ebay.magellan.tascreed.depend.common.collection.GeneralDataListMap;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class JobNotifyExecThreadTest {

    @InjectMocks
    private JobNotifyExecThread jobNotifyExecThread = new JobNotifyExecThread();

    @Mock
    private TumblerConstants tumblerConstants;
    @Mock
    private JobDefineRepo jobDefineRepo;
    @Mock
    private JobServer jobServer;
    @Mock
    private JobBulletin jobBulletin;
    @Mock
    private TaskBulletin taskBulletin;
    @Mock
    private TcLogger logger;

    private JobBuilder jobBuilder = new JobBuilder();
    private TaskBuilder taskBuilder = new TaskBuilder();

    @Before
    public void init() throws Exception {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest);

        Map<String, String> map = new HashMap<>();
        List<Task> tasks = taskBuilder.buildNewTasks(job);
        for (Task task : tasks) {
            map.put(task.getTaskName(), task.toJson(TaskViews.TASK_DONE.class));
        }

        doReturn(map).when(taskBulletin).readAllDoneTasks();
    }

    @Test
    public void fetchAllDoneTasks() throws Exception {
        List<Task> tasks = jobNotifyExecThread.fetchAllDoneTasks();
        assertEquals(4, tasks.size());
    }

    @Test
    public void aggregateTasks() throws Exception {
        List<Task> tasks = jobNotifyExecThread.fetchAllDoneTasks();
        GeneralDataListMap<JobInstKey, Task> map = jobNotifyExecThread.aggregateTasks(tasks);
        assertEquals(1, map.keySet().size());
    }
}
