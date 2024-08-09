package com.ebay.magellan.tascreed.core.domain.ban;

public enum BanTargetEnum {
    JOB(0x01),
    ROUTINE(0x10),
    ANY(0x11)
    ;

    private int code;

    BanTargetEnum(int code) {
        this.code = code;
    }

    public boolean compatible(BanTargetEnum other) {
        if (other == null) return false;
        return (this.code & other.code) != 0;
    }

    public boolean isJob() {
        return compatible(JOB);
    }

    public boolean isRoutine() {
        return compatible(ROUTINE);
    }
}
