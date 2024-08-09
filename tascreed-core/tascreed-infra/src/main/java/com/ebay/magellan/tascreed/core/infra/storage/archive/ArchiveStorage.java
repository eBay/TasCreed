package com.ebay.magellan.tascreed.core.infra.storage.archive;

import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.job.JobInstKey;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.domain.task.TaskInstKey;

import java.util.List;

public interface ArchiveStorage {

    String name();

    // -----

    Job findJob(JobInstKey key);

    void archiveJob(String key, String value, String jobName, String trigger, String state) throws Exception;

    // -----

    Task findTask(TaskInstKey key);

    boolean archiveTasks(List<Task> tasks);

}
