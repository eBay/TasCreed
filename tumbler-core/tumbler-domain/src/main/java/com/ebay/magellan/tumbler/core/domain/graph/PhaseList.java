package com.ebay.magellan.tumbler.core.domain.graph;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PhaseList<T> {
    private List<Phase<T>> phases = new LinkedList<>();

    public void addNode(int phase, String name, T node) {
        Phase<T> ph = findPhase(phase);
        if (ph == null) {
            ph = new Phase<T>(phase);
            insertPhase(ph);
        }
        ph.addNode(name, node);
    }

    public Phase<T> findPhase(int phase) {
        for (Phase p : phases) {
            if (p.getPhase() == phase) {
                return p;
            }
        }
        return null;
    }

    private void insertPhase(Phase<T> phase) {
        if (phase == null) return;
        phases.add(phase);
        sortPhases();
    }

    private void sortPhases() {
        Collections.sort(phases);
    }

    // -----

    public Phase<T> findPrevPhase(int phase) {
        Phase r = null;
        for (Phase p : phases) {
            if (p.getPhase() == phase) {
                return r;
            }
            r = p;
        }
        return null;
    }
}
