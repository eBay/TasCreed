package com.ebay.magellan.tascreed.core.domain.affinity;

import com.ebay.magellan.tascreed.core.domain.task.Task;

public interface AffinityRule {

    String getName();

    int getWeight();

    boolean isAffinity(Task task);

}
