package com.ebay.magellan.tascreed.core.domain.duty;

import org.apache.commons.lang.StringUtils;

import java.util.*;

public enum NodeDutyEnum {
    // none
    NONE("NONE", false),
    // server
    JOB_SERVER("JOB_SERVER", true),
    STATE_SERVER("STATE_SERVER", true),
    SCHEDULE_SERVER("SCHEDULE_SERVER", true),
    SERVER("SERVER", false, JOB_SERVER, STATE_SERVER, SCHEDULE_SERVER),
    // executor
    TASK_EXECUTOR("TASK_EXECUTOR", true),
    ROUTINE_EXECUTOR("ROUTINE_EXECUTOR", true),
    EXECUTOR("EXECUTOR", false, TASK_EXECUTOR, ROUTINE_EXECUTOR),
    // all
    ALL("ALL", false, SERVER, EXECUTOR),
    ;

    private String name;
    private boolean realDuty;
    private NodeDutyEnum[] coveredDuties;

    NodeDutyEnum(String name, boolean realDuty, NodeDutyEnum... coveredDuties) {
        this.name = name;
        this.realDuty = realDuty;
        this.coveredDuties = coveredDuties;
    }

    // -----

    public static NodeDutyEnum buildByName(String name) {
        if (StringUtils.isBlank(name)) return NONE;
        for (NodeDutyEnum e : NodeDutyEnum.values()) {
            if (StringUtils.equalsIgnoreCase(e.name, name)) {
                return e;
            }
        }
        return NONE;
    }

    public String getName() {
        return name;
    }

    public boolean isRealDuty() {
        return realDuty;
    }

    public Set<NodeDutyEnum> getAllCoveredRealDuties() {
        Set<NodeDutyEnum> duties = new HashSet<>();
        if (this.realDuty) {
            duties.add(this);
        }
        if (coveredDuties != null && coveredDuties.length > 0) {
            for (NodeDutyEnum d : coveredDuties) {
                duties.addAll(d.getAllCoveredRealDuties());
            }
        }
        return duties;
    }

}
