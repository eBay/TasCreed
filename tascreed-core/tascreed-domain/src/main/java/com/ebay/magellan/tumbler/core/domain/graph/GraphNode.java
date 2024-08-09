package com.ebay.magellan.tumbler.core.domain.graph;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

@Getter
@Setter
@EqualsAndHashCode
public class GraphNode<T> {
    private String name;
    @EqualsAndHashCode.Exclude
    private T data;

    @EqualsAndHashCode.Exclude
    private Set<GraphNode> prevNodes = new LinkedHashSet<>();
    @EqualsAndHashCode.Exclude
    private Set<GraphNode> nextNodes = new LinkedHashSet<>();

    private GraphNode() {}

    public static <T> GraphNode<T> init(String name, T data) {
        GraphNode<T> node = new GraphNode<>();
        node.setName(name);
        node.setData(data);
        return node;
    }

    // -----

    public void linkPrevNode(GraphNode prev) {
        if (prev != null) {
            this.getPrevNodes().add(prev);
            prev.getNextNodes().add(this);
        }
    }

    public boolean prevNodesEmpty() {
        return CollectionUtils.isEmpty(prevNodes);
    }
    public boolean nextNodesEmpty() {
        return CollectionUtils.isEmpty(nextNodes);
    }

    // -----

    public boolean predicateSelf(Predicate<T> predicate) {
        return predicate != null ? predicate.test(getData()) : true;
    }
    public boolean predicatePrevNodes(Predicate<T> predicate, boolean defValueIfEmptyPrevNodes) {
        if (predicate == null) return true;
        if (prevNodesEmpty()) return defValueIfEmptyPrevNodes;
        boolean result = true;
        for (GraphNode<T> prevNode : prevNodes) {
            result = result && predicate.test(prevNode.getData());
        }
        return result;
    }
    public boolean predicateNextNodes(Predicate<T> predicate, boolean defValueIfEmptyNextNodes) {
        if (predicate == null) return true;
        if (nextNodesEmpty()) return defValueIfEmptyNextNodes;
        boolean result = true;
        for (GraphNode<T> nextNode : nextNodes) {
            result = result && predicate.test(nextNode.getData());
        }
        return result;
    }

}
