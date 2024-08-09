package com.ebay.magellan.tumbler.core.domain.trait;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum Trait {
    /**
     * job define trait
     */

    /**
     * step define trait
     */
    // step can be ignored
    CAN_IGNORE(TraitType.STEP_DEFINE, new String[]{"canIgnore", "can-ignore", "ignorable"}),

    /**
     * job trait
     */
    // job is deleted
    DELETED(TraitType.JOB, new String[]{"deleted"}, false),

    /**
     * step trait
     */
    // step failed state will not impact job state
    CAN_FAIL(TraitType.STEP, new String[]{"canFail", "can-fail"}),

    /**
     * task trait
     */
    // tasks will be archived
    ARCHIVE(TraitType.TASK, new String[]{"archive"}),

    ;

    private TraitType type;
    private String[] aliases;
    private boolean configurable;

    private static final Map<String, Trait> traitMap = new HashMap<>();

    static {
        buildTraitMap();
    }

    Trait(TraitType type, String[] aliases, boolean configurable) {
        this.type = type;
        this.aliases = aliases;
        this.configurable = configurable;
    }
    Trait(TraitType type, String[] aliases) {
        this.type = type;
        this.aliases = aliases;
        this.configurable = true;
    }

    public static Trait findByName(String name) {
        return traitMap.get(name);
    }

    public TraitType getType() {
        return this.type;
    }
    public boolean isConfigurable() {
        return this.configurable;
    }

    // -----

    private static void putTraitMap(String k, Trait v) {
        if (StringUtils.isNotBlank(k) && v != null) {
            traitMap.put(k, v);
        }
    }
    private static void buildTraitMap() {
        traitMap.clear();
        for (Trait trait : Trait.values()) {
            putTraitMap(trait.name(), trait);
            if (trait.aliases != null) {
                for (String alias : trait.aliases) {
                    putTraitMap(alias, trait);
                }
            }
        }
    }

    // -----

    public enum TraitType {
        JOB_DEFINE(),
        STEP_DEFINE(),
        JOB(JOB_DEFINE),
        STEP(STEP_DEFINE),
        TASK(STEP, STEP_DEFINE),
        ;

        private TraitType[] coverTypes;

        TraitType(TraitType... coverTypes) {
            this.coverTypes = coverTypes;
        }

        // covers means a trait can exist in the kind of data structure
        public boolean covers(TraitType traitType) {
            if (traitType == null) return false;
            if (this == traitType) return true;
            if (coverTypes != null) {
                for (TraitType c : coverTypes) {
                    if (c == traitType) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
