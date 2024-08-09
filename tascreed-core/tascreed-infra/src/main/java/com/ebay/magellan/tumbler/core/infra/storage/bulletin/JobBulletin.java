package com.ebay.magellan.tumbler.core.infra.storage.bulletin;

import com.ebay.magellan.tumbler.core.domain.job.Job;
import com.ebay.magellan.tumbler.core.domain.task.Task;

import java.util.List;
import java.util.Map;

public interface JobBulletin extends BaseBulletin {

    /**
     * @param job          need to update
     * @param newTasks     need to put
     * @param oldDoneTasks need to delete
     * @param oldErrorTasks need to delete
     * @return submit success or not
     * @throws Exception if there's any exception
     */
    boolean submitJobAndTasks(Job job,
                              List<Task> newTasks,
                              List<Task> oldDoneTasks,
                              List<Task> oldErrorTasks) throws Exception;

    // -----

    String readJob(String name, String trigger);
    Map<String, String> readJobsByName(String jobName) throws Exception;

    Map<String, String> readAllJobs() throws Exception;

    Map<String, String> readDoneTasksOfJob(String jobName, String trigger) throws Exception;
    Map<String, String> readErrorTasksOfJob(String jobName, String trigger) throws Exception;

}
