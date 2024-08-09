package com.ebay.magellan.tascreed.core.domain.define;

public enum StepTypeEnum {
    SIMPLE(1),
    SHARD(2),
    PACK(3),
    ;

    // the smaller the order number, the higher the priority
    // SIMPLE > SHARD > PACK
    private int order;

    private StepTypeEnum(int order) {
        this.order = order;
    }

    public static int getOrder(StepTypeEnum st) {
        if (st == null) {
            st = SIMPLE;
        }
        return st.order;
    }
}
