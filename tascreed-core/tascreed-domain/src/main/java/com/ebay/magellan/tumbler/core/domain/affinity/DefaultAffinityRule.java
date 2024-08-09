package com.ebay.magellan.tumbler.core.domain.affinity;

import com.ebay.magellan.tumbler.core.domain.task.Task;

public class DefaultAffinityRule implements AffinityRule {

    private static final String RULE_NAME = "default";
    private static final int RULE_WEIGHT = 1;

    public String getName() {
        return RULE_NAME;
    }

    public int getWeight() {
        return RULE_WEIGHT;
    }

    public boolean isAffinity(Task task) {
        return true;
    }

}
