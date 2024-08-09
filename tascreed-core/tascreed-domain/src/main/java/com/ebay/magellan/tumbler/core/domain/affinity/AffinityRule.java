package com.ebay.magellan.tumbler.core.domain.affinity;

import com.ebay.magellan.tumbler.core.domain.task.Task;

public interface AffinityRule {

    String getName();

    int getWeight();

    boolean isAffinity(Task task);

}
