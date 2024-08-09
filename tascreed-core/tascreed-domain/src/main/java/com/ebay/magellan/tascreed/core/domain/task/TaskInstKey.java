package com.ebay.magellan.tascreed.core.domain.task;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * task key, to identify a unique task
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class TaskInstKey {
    private String jobName;
    private String trigger;
    private String taskName;

    @Override
    public String toString() {
        return String.format("%s.%s.%s", jobName, trigger, taskName);
    }
}
