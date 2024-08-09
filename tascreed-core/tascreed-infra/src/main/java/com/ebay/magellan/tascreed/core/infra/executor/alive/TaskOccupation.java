package com.ebay.magellan.tascreed.core.infra.executor.alive;

import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.TaskBulletin;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;

@AllArgsConstructor
public class TaskOccupation {
    private Task occupiedTask;
    private String workerThreadName;

    private TaskBulletin taskBulletin;

    // -----

    private static final int MAX_RETRY_TIMES = 5;

    public boolean taskStillOccupied() {
        for (int i = 0; i < MAX_RETRY_TIMES; i++) {
            try {
                return taskStillOccupiedImpl();
            } catch (Exception e) {
                // ignore
            }
        }
        return false;
    }

    // check if the task still occupied by current thread
    private boolean taskStillOccupiedImpl() throws TumblerException {
        if (taskBulletin == null || occupiedTask == null) return false;
        String val = taskBulletin.checkTaskAdoption(occupiedTask);
        return StringUtils.equals(val, workerThreadName);
    }
}
