package com.ebay.magellan.tumbler.core.domain.graph;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class GraphNodeTest {

    GraphNode<String> buildGraphNode(String s) {
        return GraphNode.init(s, s);
    }

    @Test
    public void linkPrevNode() {
        GraphNode a = buildGraphNode("a");
        GraphNode b = buildGraphNode("b");
        assertTrue(a.prevNodesEmpty());
        assertTrue(a.nextNodesEmpty());
        assertTrue(b.prevNodesEmpty());
        assertTrue(b.nextNodesEmpty());

        a.linkPrevNode(b);

        assertFalse(a.prevNodesEmpty());
        assertTrue(a.nextNodesEmpty());
        assertTrue(b.prevNodesEmpty());
        assertFalse(b.nextNodesEmpty());
        assertTrue(b.getNextNodes().contains(a));
        assertTrue(a.getPrevNodes().contains(b));
    }

    @Test
    public void testPredicate() {
        GraphNode<String> a = buildGraphNode("a");
        GraphNode<String> b = buildGraphNode("b");
        a.linkPrevNode(b);

        assertTrue(a.predicateSelf(d -> StringUtils.equals("a", d)));
        assertTrue(a.predicatePrevNodes(d -> StringUtils.equals("b", d), true));
        assertTrue(b.predicatePrevNodes(d -> false, true));
        assertTrue(b.predicateNextNodes(d -> StringUtils.equals("a", d), true));
        assertTrue(a.predicateNextNodes(d -> false, true));
    }
}
