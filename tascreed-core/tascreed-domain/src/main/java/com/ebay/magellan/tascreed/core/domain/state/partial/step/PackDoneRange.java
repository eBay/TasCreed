package com.ebay.magellan.tascreed.core.domain.state.partial.step;

import com.ebay.magellan.tascreed.core.domain.util.RangeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.*;
import lombok.Getter;
import lombok.Setter;

/**
 * pack step done range is described by a range set of done pack offsets
 */
@Getter
@Setter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PackDoneRange {
    @JsonProperty(value = "offsets")
    private String offsetRangesStr;

    @JsonIgnore
    private RangeSet<Long> offsetRanges = TreeRangeSet.create();

    // -----

    public String getOffsetRangesStr() {
        offsetRangesStr = RangeUtil.rangeSetToString(offsetRanges);
        return offsetRangesStr;
    }

    public void setOffsetRangesStr(String offsetRangesStr) {
        this.offsetRangesStr = offsetRangesStr;
        this.offsetRanges = RangeUtil.rangeSetFromString(offsetRangesStr);
    }

    // -----

    public void addDoneOffsetRange(long start, long end) {
        RangeUtil.addRange(offsetRanges, Range.closed(start, end));
    }

}
