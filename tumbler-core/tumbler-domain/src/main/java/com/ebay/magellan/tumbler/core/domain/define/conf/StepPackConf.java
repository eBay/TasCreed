package com.ebay.magellan.tumbler.core.domain.define.conf;

import com.ebay.magellan.tumbler.depend.common.util.DefaultValueUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class StepPackConf extends StepConf {
    @JsonProperty("infinite")
    private Boolean infinite;

    @JsonProperty("size")
    private Long size;
    @JsonProperty("start")
    private Long start;     // included
    @JsonProperty("end")
    private Long end;       // included, optional if infinite

    @JsonProperty("startPackId")
    private Long startPackId;

    // -----

    public static StepPackConf clone(StepPackConf sc) {
        if (sc == null) return null;
        StepPackConf nsc = new StepPackConf();
        nsc.setMaxTaskCount(sc.getMaxTaskCount());
        nsc.setInfinite(sc.getInfinite());
        nsc.setSize(sc.getSize());
        nsc.setStart(sc.getStart());
        nsc.setEnd(sc.getEnd());
        nsc.setStartPackId(sc.getStartPackId());
        return nsc;
    }

    public static StepPackConf merge(StepPackConf base, StepPackConf upper) {
        if (base == null && upper == null) return null;
        if (base == null) return clone(upper);
        if (upper == null) return clone(base);
        StepPackConf sc = new StepPackConf();
        sc.setMaxTaskCount(upper.getMaxTaskCount() != null ? upper.getMaxTaskCount() : base.getMaxTaskCount());
        sc.setInfinite(upper.getInfinite() != null ? upper.getInfinite() : base.getInfinite());
        sc.setSize(upper.getSize() != null ? upper.getSize() : base.getSize());
        sc.setStart(upper.getStart() != null ? upper.getStart() : base.getStart());
        sc.setEnd(upper.getEnd() != null ? upper.getEnd() : base.getEnd());
        sc.setStartPackId(upper.getStartPackId() != null ? upper.getStartPackId() : base.getStartPackId());
        return sc;
    }

    // -----

    public boolean isInfinite() {
        return DefaultValueUtil.booleanValue(infinite);
    }
}
