package com.ebay.magellan.tumbler.core.infra.routine.context;

import lombok.Getter;

@Getter
public class RoutineTriggerContext {
    // the trigger round of the routine executed by current executor thread
    private long triggerRound;
    // the trigger timestamp
    private long triggerTimestamp;

    public void init() {
        triggerRound = 0;
        triggerTimestamp = 0;
    }

    public void trigger() {
        triggerRound++;
        triggerTimestamp = System.currentTimeMillis();
    }

    // -----

    public String getStr() {
        return String.format("round %d", triggerRound);
    }
}
