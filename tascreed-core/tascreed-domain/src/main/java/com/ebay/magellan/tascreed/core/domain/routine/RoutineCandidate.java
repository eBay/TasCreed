package com.ebay.magellan.tascreed.core.domain.routine;

import com.ebay.magellan.tascreed.core.domain.task.WeightLabel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoutineCandidate implements Comparable<RoutineCandidate> {
    private Routine routine;
    private String adoptionKey;
    private WeightLabel weight;

    private int emptyRate = TOTAL_RATE;
    private static final int TOTAL_RATE = 1000;

    public RoutineCandidate(Routine routine, String adoptionKey, WeightLabel weight) {
        this.routine = routine;
        this.adoptionKey = adoptionKey;
        this.weight = weight;
    }

    // -----

    public void setEmptyRate(int total, int active) {
        int calcEmptyRate = (total - active) * TOTAL_RATE / total;
        this.emptyRate = Math.min(TOTAL_RATE, Math.max(0, calcEmptyRate));
    }

    // -----

    // compare priority first, then affinity
    public int compareTo(RoutineCandidate other) {
        // 1. compare by weight
        int result = getWeight().compareTo(other.getWeight());
        if (result == 0) {
            // 2. compare by empty rate
            result = getEmptyRate() - other.getEmptyRate();
            if (result == 0) {
                // 3. compare by routine information
                Routine r1 = this.getRoutine();
                Routine r2 = other.getRoutine();

                // compare routine name, index
                int r = r1.getRoutineName().compareTo(r2.getRoutineName());
                if (r == 0) {
                    r = r1.getIndex() - r2.getIndex();
                }
                result = 0 - r;     // minor name will be prior
            }
        }
        return result;
    }
}
