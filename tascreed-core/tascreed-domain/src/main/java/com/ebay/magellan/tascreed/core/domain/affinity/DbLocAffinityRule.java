package com.ebay.magellan.tascreed.core.domain.affinity;

import com.ebay.magellan.tascreed.depend.common.util.HostUtil;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import org.apache.commons.lang.StringUtils;

public class DbLocAffinityRule implements AffinityRule {
    private static final int RULE_WEIGHT = 100;

    private String location;
    public DbLocAffinityRule(String loc) {
        location = loc;
    }

    public String getName() {
        return location;
    }

    public int getWeight() {
        return RULE_WEIGHT;
    }

    public boolean isAffinity(Task task) {
        if (task == null) return false;
        String dcName = HostUtil.getDcName();
        if (StringUtils.equalsIgnoreCase(location, dcName)) {
            return true;
        } else {
            return false;
        }
    }

}
