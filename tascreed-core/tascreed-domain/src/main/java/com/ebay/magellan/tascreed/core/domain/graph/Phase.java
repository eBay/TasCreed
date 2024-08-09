package com.ebay.magellan.tascreed.core.domain.graph;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class Phase<T> implements Comparable<Phase> {
    private int phase;

    private Map<String, T> nodes = new LinkedHashMap<>();

    public Phase(int phase) {
        this.phase = phase;
    }

    // compare by phase
    public int compareTo(Phase other) {
        return Integer.valueOf(getPhase()).compareTo(Integer.valueOf(other.getPhase()));
    }

    // -----

    public void addNode(String name, T node) {
        if (StringUtils.isBlank(name) || node == null) return;
        nodes.put(name, node);
    }

    public T findNodeByName(String name) {
        return nodes.get(name);
    }

}
