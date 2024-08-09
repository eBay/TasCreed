package com.ebay.magellan.tumbler.core.domain.validate;

import com.ebay.magellan.tumbler.core.domain.define.JobDefine;
import com.ebay.magellan.tumbler.core.domain.define.StepDefine;
import com.ebay.magellan.tumbler.core.domain.graph.Graph;
import com.ebay.magellan.tumbler.core.domain.graph.GraphNode;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class StepGraphValidator implements Validator<JobDefine> {

    private static final String DataName = "StepGraph";

    public ValidateResult validate(JobDefine jobDefine) {
        ValidateResult result = ValidateResult.init(DataName);
        if (jobDefine == null) return result;

        Graph<StepDefine> stepGraph = jobDefine.getStepDefineGraph();
        result.setTitle(String.format("%s (%s)", DataName, jobDefine.getJobName()));
        if (stepGraph == null) {
            result.addMsg("step graph is null");
        } else {
            List<String> stuckNodes = someNodesWouldStuck(stepGraph);
            if (CollectionUtils.isNotEmpty(stuckNodes)) {
                String stuckNodesStr = StringUtils.join(stuckNodes, ", ");
                result.addMsg(String.format(
                        "step graph has cycle, these steps would stuck: %s", stuckNodesStr));
            }
        }
        return result;
    }

    // -----

    private List<String> someNodesWouldStuck(Graph graph) {
        if (graph == null) return null;

        Map<String, NodeWithData> allNodesMap = buildAllNodesMap(graph.getAllNodes());
        while (true) {
            boolean newNodeVisited = false;
            for (NodeWithData nd : allNodesMap.values()) {
                if (!nd.visited && nd.inCount <= 0) {
                    // visit
                    nd.visited = true;
                    Set<GraphNode> nextNodes = nd.node.getNextNodes();
                    for (GraphNode nextNode : nextNodes) {
                        if (nextNode == null) continue;
                        NodeWithData nextNd = allNodesMap.get(nextNode.getName());
                        if (nextNd != null) {
                            nextNd.inCount--;
                        }
                    }
                    newNodeVisited = true;
                }
            }
            if (!newNodeVisited) break;
        }

        // check if some nodes not visited
        List<String> stuckNodes = new ArrayList<>();
        for (Map.Entry<String, NodeWithData> entry : allNodesMap.entrySet()) {
            String k = entry.getKey();
            NodeWithData nd = entry.getValue();
            if (!nd.visited) {
                stuckNodes.add(k);
            }
        }
        return stuckNodes;
    }
    private Map<String, NodeWithData> buildAllNodesMap(Map<String, GraphNode> nodeMap) {
        Map<String, NodeWithData> map = new LinkedHashMap<>();
        for (GraphNode n : nodeMap.values()) {
            if (n != null) {
                NodeWithData nd = new NodeWithData(n);
                map.put(n.getName(), nd);
            }
        }
        return map;
    }

    @EqualsAndHashCode
    private class NodeWithData {
        public GraphNode node;
        public int inCount;
        public boolean visited;

        public NodeWithData(GraphNode node) {
            this.node = node;
            this.inCount = node.getPrevNodes().size();
            this.visited = false;
        }
    }

    // -----
}
