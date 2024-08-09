package com.ebay.magellan.tascreed.core.domain.define.conf;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class StepShardConf extends StepConf {
    @JsonProperty("shard")
    @JsonAlias({"sharding"})
    private Integer shard;

    @JsonProperty("startShardId")
    private Integer startShardId;

    // -----

    public static StepShardConf clone(StepShardConf sc) {
        if (sc == null) return null;
        StepShardConf nsc = new StepShardConf();
        nsc.setMaxTaskCount(sc.getMaxTaskCount());
        nsc.setShard(sc.getShard());
        nsc.setStartShardId(sc.getStartShardId());
        return nsc;
    }

    public static StepShardConf merge(StepShardConf base, StepShardConf upper) {
        if (base == null && upper == null) return null;
        if (base == null) return clone(upper);
        if (upper == null) return clone(base);
        StepShardConf sc = new StepShardConf();
        sc.setMaxTaskCount(upper.getMaxTaskCount() != null ? upper.getMaxTaskCount() : base.getMaxTaskCount());
        sc.setShard(upper.getShard() != null ? upper.getShard() : base.getShard());
        sc.setStartShardId(upper.getStartShardId() != null ? upper.getStartShardId() : base.getStartShardId());
        return sc;
    }
}
