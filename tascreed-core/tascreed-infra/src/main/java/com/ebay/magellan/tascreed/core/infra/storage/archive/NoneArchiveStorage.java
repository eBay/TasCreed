package com.ebay.magellan.tascreed.core.infra.storage.archive;

import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.job.JobInstKey;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.domain.task.TaskInstKey;
import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoneArchiveStorage implements ArchiveStorage {

    private static final String THIS_CLASS_NAME = NoneArchiveStorage.class.getSimpleName();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    // -----

    public String name() {
        return ArchiveStorageType.NONE.getName();
    }

    // -----

    public Job findJob(JobInstKey key) {
        logger.info(THIS_CLASS_NAME, String.format("not found job %s on none archive storage", key));
        return null;
    }

    public void archiveJob(String key, String value, String jobName, String trigger, String state) throws Exception {
        logger.info(THIS_CLASS_NAME, "will not archive job to none archive storage");
    }

    // -----

    public Task findTask(TaskInstKey key) {
        logger.info(THIS_CLASS_NAME, String.format("not found task %s on none archive storage", key));
        return null;
    }

    public boolean archiveTasks(List<Task> tasks) {
        logger.info(THIS_CLASS_NAME, "will not archive tasks to none archive storage");
        return true;
    }

}
