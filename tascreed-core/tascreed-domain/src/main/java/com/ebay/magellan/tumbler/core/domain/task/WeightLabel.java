package com.ebay.magellan.tumbler.core.domain.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WeightLabel implements Comparable<WeightLabel> {
    private int priorityWeight;
    private int affinityWeight;

    // compare priority first, then affinity
    public int compareTo(WeightLabel other) {
        if (this.getPriorityWeight() == other.getPriorityWeight()) {
            return this.getAffinityWeight() - other.getAffinityWeight();
        }
        return this.getPriorityWeight() - other.getPriorityWeight();
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", priorityWeight, affinityWeight);
    }

}
