package com.ebay.magellan.tumbler.core.domain.graph;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Predicate;

@Getter
@Setter
public class Graph<T> {
    private Map<String, GraphNode<T>> allNodes = new LinkedHashMap<>();

    public Graph(List<GraphNode<T>> nodes) {
        if (CollectionUtils.isNotEmpty(nodes)) {
            for (GraphNode<T> node : nodes) {
                if (node != null) {
                    allNodes.put(node.getName(), node);
                }
            }
        }
    }

    // -----

    public GraphNode<T> findNodeByName(String name) {
        return allNodes.get(name);
    }

    // -----

    /**
     * get list of graph nodes by predicate conditions, to filter the nodes match all the conditions
     * @param curNodePredicate predicate current node
     * @param prevNodePredicate predicate prev node
     * @param defValueIfEmptyPrevNodes if no prev nodes, result is default value
     * @param nextNodePredicate predicate next node
     * @param defValueIfEmptyNextNodes if no next nodes, result is default value
     * @return list of graph nodes pass all the predicates
     */
    public List<GraphNode<T>> getNodesByPredicates(Predicate<T> curNodePredicate,
                                                   Predicate<T> prevNodePredicate,
                                                   boolean defValueIfEmptyPrevNodes,
                                                   Predicate<T> nextNodePredicate,
                                                   boolean defValueIfEmptyNextNodes) {
        List<GraphNode<T>> targetNodes = new ArrayList<>();
        for (GraphNode<T> node : allNodes.values()) {
            if (node.predicateSelf(curNodePredicate)
                    && node.predicatePrevNodes(prevNodePredicate, defValueIfEmptyPrevNodes)
                    && node.predicateNextNodes(nextNodePredicate, defValueIfEmptyNextNodes)) {
                targetNodes.add(node);
            }
        }
        return targetNodes;
    }

}
