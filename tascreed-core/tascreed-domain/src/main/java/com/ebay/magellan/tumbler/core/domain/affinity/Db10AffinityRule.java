package com.ebay.magellan.tumbler.core.domain.affinity;

import com.ebay.magellan.tumbler.depend.common.util.HostUtil;
import com.ebay.magellan.tumbler.core.domain.task.Task;
import org.apache.commons.lang.StringUtils;

public class Db10AffinityRule implements AffinityRule {

    private static final String RULE_NAME = "db10";
    private static final int RULE_WEIGHT = 100;

    private static final int DB_HOST_NUM = 12;

    public String getName() {
        return RULE_NAME;
    }

    public int getWeight() {
        return RULE_WEIGHT;
    }

    /**
     * Primary DB (Secondary DB will be opposite)
     * 00-01	SLC
     * 04-05	SLC
     * 08-09	SLC
     *
     * 02-03	LVS
     * 06-07	LVS
     * 10-11	LVS
     */
    public boolean isAffinity(Task task) {
        if (task == null) return false;
        if (!task.isShard()) return true;

        int hostId = task.getTaskAllConf().getShardConf().getIndex() % DB_HOST_NUM;
        String dcName = HostUtil.getDcName();
        if (StringUtils.equalsIgnoreCase(HostUtil.DC_SLC, dcName)) {
            return hostId == 0 || hostId == 1 || hostId == 4 || hostId == 5 || hostId == 8 || hostId == 9;
        } else if (StringUtils.equalsIgnoreCase(HostUtil.DC_LVS, dcName)) {
            return hostId == 2 || hostId == 3 || hostId == 6 || hostId == 7 || hostId == 10 || hostId == 11;
        } else {
            return false;
        }
    }

}
