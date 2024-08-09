package com.ebay.magellan.tumbler.core.infra.storage.archive;

import com.ebay.magellan.tumbler.core.domain.job.Job;
import com.ebay.magellan.tumbler.core.domain.job.JobInstKey;
import com.ebay.magellan.tumbler.core.domain.task.Task;
import com.ebay.magellan.tumbler.core.domain.task.TaskInstKey;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ArchiveStorageList implements ArchiveStorage {

    private List<ArchiveStorage> storageList = new ArrayList<>();

    public void addArchiveStorage(ArchiveStorage storage) {
        if (storage == null) return;
        if (findArchiveStorage(storage.name()) == null) {
            storageList.add(storage);
        }
    }

    public ArchiveStorage findArchiveStorage(String name) {
        for (ArchiveStorage storage : storageList) {
            if (storage != null && StringUtils.equals(name, storage.name())) {
                return storage;
            }
        }
        return null;
    }

    // -----

    public String name() {
        return "ALL";
    }

    public Job findJob(JobInstKey key) {
        for (ArchiveStorage storage : storageList) {
            if (storage != null) {
                Job job = storage.findJob(key);
                if (job != null) {
                    return job;
                }
            }
        }
        return null;
    }

    public void archiveJob(String key, String value, String jobName, String trigger, String state) throws Exception {
        for (ArchiveStorage storage : storageList) {
            if (storage != null) {
                try {
                    storage.archiveJob(key, value, jobName, trigger, state);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // -----

    public Task findTask(TaskInstKey key) {
        for (ArchiveStorage storage : storageList) {
            if (storage != null) {
                Task task = storage.findTask(key);
                if (task != null) {
                    return task;
                }
            }
        }
        return null;
    }

    public boolean archiveTasks(List<Task> tasks) {
        boolean success = true;
        for (ArchiveStorage storage : storageList) {
            if (storage != null) {
                try {
                    success = success && storage.archiveTasks(tasks);
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
            }
        }
        return success;
    }

}
