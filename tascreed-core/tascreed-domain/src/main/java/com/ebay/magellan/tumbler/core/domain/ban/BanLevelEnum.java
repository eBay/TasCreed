package com.ebay.magellan.tumbler.core.domain.ban;

import org.apache.commons.lang.StringUtils;

public enum BanLevelEnum {
    NONE("NONE", BanTargetEnum.ANY, 0),                        // nothing will be banned

    /**
     * ban target job
     * TASK_PICK, TASK_CREATE, JOB_SUBMIT
     */
    TASK_PICK("TASK_PICK", BanTargetEnum.JOB, 1),   // for banned job, no task will be picked
    TASK_CREATE("TASK_CREATE", BanTargetEnum.JOB, 2),   // cover TASK_PICK, for banned job, no new task will be created
    JOB_SUBMIT("JOB_SUBMIT", BanTargetEnum.JOB, 3),    // cover TASK_CREATE, for banned job, new job instance will be submitted

    /**
     * ban target routine
     * ROUTINE_EXEC, ROUTINE_OCCUPY
     */
    ROUTINE_EXEC("ROUTINE_EXEC", BanTargetEnum.ROUTINE, 1),     // for banned routine, it will not execute for any round
    ROUTINE_OCCUPY("ROUTINE_OCCUPY", BanTargetEnum.ROUTINE, 2), // cover ROUTINE_EXEC, for banned routine, no routine will be occupied
    ;

    private String name;
    private BanTargetEnum target;
    private int level;

    BanLevelEnum(String name, BanTargetEnum target, int level) {
        this.name = name;
        this.target = target;
        this.level = level;
    }

    private static BanLevelEnum DEFAULT = NONE;

    public static BanLevelEnum buildByName(String name) {
        if (StringUtils.isBlank(name)) return DEFAULT;
        for (BanLevelEnum e : BanLevelEnum.values()) {
            if (StringUtils.equalsIgnoreCase(e.name, name)) {
                return e;
            }
        }
        return DEFAULT;
    }

    public String getName() {
        return name;
    }

    public BanTargetEnum getTarget() {
        return target;
    }

    public static boolean bannable(BanLevelEnum bl) {
        return bl != null && bl != NONE;
    }

    // -----

    public boolean covers(BanLevelEnum bl) {
        if (bl == null) return true;
        if (target.compatible(bl.target)) {
            return this.level >= bl.level;
        } else {
            return false;
        }
    }

    // -----
}
