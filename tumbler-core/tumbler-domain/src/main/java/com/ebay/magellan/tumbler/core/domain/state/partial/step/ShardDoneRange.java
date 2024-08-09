package com.ebay.magellan.tumbler.core.domain.state.partial.step;

import com.ebay.magellan.tumbler.core.domain.util.RangeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import lombok.Getter;
import lombok.Setter;

/**
 * shard step done range is described by a range set of done shard ids
 */
@Getter
@Setter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ShardDoneRange {
    @JsonProperty(value = "indexes")
    private String indexRangesStr;

    @JsonIgnore
    private RangeSet<Long> indexRanges = TreeRangeSet.create();

    // -----

    public String getIndexRangesStr() {
        indexRangesStr = RangeUtil.rangeSetToString(indexRanges);
        return indexRangesStr;
    }

    public void setIndexRangesStr(String indexRangesStr) {
        this.indexRangesStr = indexRangesStr;
        this.indexRanges = RangeUtil.rangeSetFromString(indexRangesStr);
    }

    // -----

    public void addDoneIndex(int index) {
        RangeUtil.addRange(indexRanges, Range.closed((long)index, (long)index));
    }
}
