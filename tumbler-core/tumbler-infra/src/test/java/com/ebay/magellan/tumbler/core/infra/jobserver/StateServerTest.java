package com.ebay.magellan.tumbler.core.infra.jobserver;

import com.ebay.magellan.tumbler.core.domain.builder.JobBuilder;
import com.ebay.magellan.tumbler.core.domain.builder.TaskBuilder;
import com.ebay.magellan.tumbler.core.domain.task.TaskViews;
import com.ebay.magellan.tumbler.core.domain.job.Job;
import com.ebay.magellan.tumbler.core.domain.task.Task;
import com.ebay.magellan.tumbler.core.infra.constant.TumblerKeys;
import com.ebay.magellan.tumbler.core.infra.storage.bulletin.*;
import com.ebay.magellan.tumbler.core.infra.help.TestRepo;
import com.ebay.magellan.tumbler.depend.common.collection.KeyValuePair;
import com.ebay.magellan.tumbler.depend.common.logger.TumblerLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class StateServerTest {

    @InjectMocks
    private StateServer stateServer = new StateServer();

    @Mock
    private TumblerLogger logger;

    @Mock
    private TumblerKeys tumblerKeys;

    @Mock
    private JobBulletin jobBulletin;
    @Mock
    private TaskBulletin taskBulletin;
    @Mock
    private RoutineBulletin routineBulletin;

    private JobBuilder jobBuilder = new JobBuilder();
    private TaskBuilder taskBuilder = new TaskBuilder();

    @Before
    public void init() throws Exception {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest);
        String jobStr = job.toJson();

        Map<String, String> jobMap = new HashMap<>();
        jobMap.put("k1", jobStr);
        doReturn(jobMap).when(jobBulletin).readAllJobs();

        Map<String, String> todoTasks = new HashMap<>();
        Map<String, String> doneTasks = new HashMap<>();
        List<Task> tasks = taskBuilder.buildNewTasks(job);
        for (Task task : tasks) {
            todoTasks.put(task.getTaskName(), task.toJson(TaskViews.TASK_TODO.class));
            doneTasks.put(task.getTaskName(), task.toJson(TaskViews.TASK_DONE.class));
        }

        doReturn(todoTasks).when(taskBulletin).readAllTodoTasks();
        doReturn(doneTasks).when(taskBulletin).readAllDoneTasks();

        Map<String, String> adoptions = new HashMap<>();
        adoptions.put("/test/job1/trigger1/step1", "node1/thread1");
        adoptions.put("/test/job1/trigger1/step2", "node1/thread2");
        adoptions.put("/test/job2/trigger1/step1", "node1/thread3");
        adoptions.put("/test/job2/trigger2/step2", "node1/thread5");
        doReturn(adoptions).when(taskBulletin).readAllTaskAdoptions();

        doReturn("key").when(tumblerKeys).getTaskAdoptionKey(anyString(), anyString(), anyString());
        doReturn("value").when(taskBulletin).getSingleValue(anyString());
    }

    @Test
    public void findAliveJobs() throws Exception {
        List<Job> jobs1 = stateServer.findAliveJobs(Optional.empty(), Optional.empty());
        assertEquals(1, jobs1.size());

        List<Job> jobs2 = stateServer.findAliveJobs(Optional.of("test"), Optional.empty());
        assertEquals(0, jobs2.size());

        List<Job> jobs3 = stateServer.findAliveJobs(Optional.of("sample"), Optional.empty());
        assertEquals(1, jobs3.size());

        List<Job> jobs4 = stateServer.findAliveJobs(Optional.empty(), Optional.of("20191020"));
        assertEquals(1, jobs4.size());
    }

    @Test
    public void findTodoTasks() throws Exception {
        List<Task> tasks1 = stateServer.findTodoTasks(Optional.empty(), Optional.empty(), Optional.empty());
        assertEquals(4, tasks1.size());

        List<Task> tasks2 = stateServer.findTodoTasks(Optional.of("test"), Optional.empty(), Optional.empty());
        assertEquals(0, tasks2.size());

        List<Task> tasks3 = stateServer.findTodoTasks(Optional.of("sample"), Optional.empty(), Optional.empty());
        assertEquals(4, tasks3.size());

        List<Task> tasks4 = stateServer.findTodoTasks(Optional.empty(), Optional.of("20191020"), Optional.empty());
        assertEquals(4, tasks4.size());

        List<Task> tasks5 = stateServer.findTodoTasks(Optional.empty(), Optional.of("20191020"), Optional.of("aggr-1"));
        assertEquals(0, tasks5.size());

        List<Task> tasks6 = stateServer.findTodoTasks(Optional.empty(), Optional.of("20191020"), Optional.of("prep"));
        assertEquals(4, tasks6.size());
    }

    @Test
    public void findDoneTasks() throws Exception {
        List<Task> tasks1 = stateServer.findDoneTasks(Optional.empty(), Optional.empty(), Optional.empty());
        assertEquals(4, tasks1.size());

        List<Task> tasks2 = stateServer.findDoneTasks(Optional.of("test"), Optional.empty(), Optional.empty());
        assertEquals(0, tasks2.size());

        List<Task> tasks3 = stateServer.findDoneTasks(Optional.of("sample"), Optional.empty(), Optional.empty());
        assertEquals(4, tasks3.size());

        List<Task> tasks4 = stateServer.findDoneTasks(Optional.empty(), Optional.of("20191020"), Optional.empty());
        assertEquals(4, tasks4.size());

        List<Task> tasks5 = stateServer.findDoneTasks(Optional.empty(), Optional.of("20191020"), Optional.of("aggr-1"));
        assertEquals(0, tasks5.size());

        List<Task> tasks6 = stateServer.findDoneTasks(Optional.empty(), Optional.of("20191020"), Optional.of("prep"));
        assertEquals(4, tasks6.size());
    }

    @Test
    public void findTaskAdoptions() throws Exception {
        List<KeyValuePair> pairs1 = stateServer.findTaskAdoptions(Optional.empty());
        assertEquals(4, pairs1.size());

        List<KeyValuePair> pairs2 = stateServer.findTaskAdoptions(Optional.of("job1"));
        assertEquals(2, pairs2.size());

        List<KeyValuePair> pairs3 = stateServer.findTaskAdoptions(Optional.of("job2/trigger1"));
        assertEquals(1, pairs3.size());
    }

    @Test
    public void findTaskAdoption() {
        KeyValuePair<String, String> pair = stateServer.findTaskAdoption("sample", "trigger", "task");
        assertNotNull(pair);
        assertEquals("key", pair.getKey());
        assertEquals("value", pair.getValue());
    }
}
