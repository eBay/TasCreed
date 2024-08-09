package com.ebay.magellan.tumbler.core.domain.occupy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OccupyInfo {
    private long occupyLeaseId;
    private String occupyKey;
    private String occupyValue;

    private boolean alive = true;
    private boolean finished = false;

    public OccupyInfo(long occupyLeaseId, String occupyKey, String occupyValue) {
        this.occupyLeaseId = occupyLeaseId;
        this.occupyKey = occupyKey;
        this.occupyValue = occupyValue;
    }
}
