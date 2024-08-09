package com.ebay.magellan.tumbler.core.domain.graph;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GraphTest {

    GraphNode<String> buildGraphNode(String s) {
        return GraphNode.init(s, s);
    }

    Graph<String> buildGraph() {
        GraphNode<String> a = buildGraphNode("a");
        GraphNode<String> b = buildGraphNode("b");
        GraphNode<String> c = buildGraphNode("c");
        GraphNode<String> d = buildGraphNode("d");
        b.linkPrevNode(a);
        c.linkPrevNode(a);
        d.linkPrevNode(b);
        d.linkPrevNode(c);
        List<GraphNode> list = new ArrayList<>();
        list.add(a);
        list.add(b);
        list.add(c);
        list.add(d);
        return new Graph(list);
    }

    @Test
    public void findNodeByName() {
        Graph<String> graph = buildGraph();
        assertEquals("a", graph.findNodeByName("a").getData());
        assertEquals("b", graph.findNodeByName("b").getData());
        assertEquals("c", graph.findNodeByName("c").getData());
        assertEquals("d", graph.findNodeByName("d").getData());
    }

    @Test
    public void getNodesByPredicates() {
        Graph<String> graph = buildGraph();

        assertEquals(4, graph.getNodesByPredicates(null,
                null, true,
                null, true).size());
        assertEquals(1, graph.getNodesByPredicates(d -> "a".equals(d),
                null, true,
                null, true).size());
        assertEquals(2, graph.getNodesByPredicates(null,
                d -> "a".equals(d), false,
                null, true).size());
        assertEquals(1, graph.getNodesByPredicates(null,
                d -> "b".equals(d) || "c".equals(d), false,
                null, true).size());
        assertEquals(2, graph.getNodesByPredicates(null,
                null, true,
                d -> "d".equals(d), false).size());
        assertEquals(1, graph.getNodesByPredicates(null,
                null, true,
                d -> "b".equals(d) || "c".equals(d), false).size());

        assertEquals(0, graph.getNodesByPredicates(null,
                d -> false, true,
                d -> false, true).size());
        assertEquals(1, graph.getNodesByPredicates(null,
                d -> false, true,
                null, true).size());
        assertEquals(1, graph.getNodesByPredicates(null,
                null, true,
                d -> false, true).size());
    }
}
