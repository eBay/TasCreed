package com.ebay.magellan.tumbler.core.domain.define;

import com.ebay.magellan.tumbler.core.domain.graph.PhaseList;
import com.ebay.magellan.tumbler.core.domain.trait.Trait;
import com.ebay.magellan.tumbler.core.domain.trait.Traits;
import com.ebay.magellan.tumbler.depend.common.util.DefaultValueUtil;
import com.ebay.magellan.tumbler.depend.common.util.JsonParseUtil;
import com.ebay.magellan.tumbler.core.domain.graph.Graph;
import com.ebay.magellan.tumbler.core.domain.graph.GraphNode;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.*;

@Getter
@Setter
@ToString
public class JobDefine implements Define {
    @JsonProperty("jobName")
    @JsonAlias({"name"})
    private String jobName;
    @JsonProperty("version")
    private long version;
    @JsonProperty("priority")
    private Integer priority;
    // only one job instance can be submitted or not
    @JsonProperty("uniqueAliveInstance")
    private Boolean uniqueAliveInstance;

    @JsonProperty("params")
    private Map<String, String> params;
    @JsonProperty("steps")
    private List<StepDefine> steps = new ArrayList<>();

    @JsonProperty("traits")
    private List<String> traitStrList;
    @JsonIgnore
    private Traits traits = new Traits(Trait.TraitType.JOB_DEFINE);

    // expect execute duration of the job, in milliseconds
    @JsonProperty("duration")
    private Long duration;

    @JsonIgnore
    private Map<String, StepDefine> stepsMap;
    @JsonIgnore
    private Graph<StepDefine> stepDefineGraph;
    @JsonIgnore
    private PhaseList<StepDefine> stepDefinePhaseList;

    // -----

    @Override
    public String name() {
        return jobName;
    }

    @JsonIgnore
    public boolean isUniqueAliveInstance() {
        return DefaultValueUtil.booleanValue(uniqueAliveInstance);
    }

    // -----

    public StepDefine findStepDefineByName(String name) {
        if (MapUtils.isNotEmpty(stepsMap)) {
            return stepsMap.get(name);
        } else if (CollectionUtils.isNotEmpty(steps)) {
            for (StepDefine step : steps) {
                if (step != null && StringUtils.equals(name, step.name())) {
                    return step;
                }
            }
        }
        return null;
    }

    // -----

    public void setTraitStrList(List<String> traitStrList) {
        this.traitStrList = traitStrList;
        traits.genTraitSet(traitStrList);
    }

    public List<String> getTraitStrList() {
        this.traitStrList = traits.genTraitStrList();
        return traitStrList;
    }

    // -----

    // post build of job define, to build step map and graph
    public void postBuild() {
        buildStepMap();
        buildGraph();
        buildPhaseList();
    }

    // -----

    private void buildStepMap() {
        stepsMap = new LinkedHashMap<>();
        for (StepDefine step : steps) {
            if (step != null) {
                stepsMap.put(step.getStepName(), step);
            }
        }
    }

    // -----

    private void buildGraph() {
        stepDefineGraph = new Graph<>(buildGraphNodes());
        linkNodes();
    }

    List<GraphNode<StepDefine>> buildGraphNodes() {
        List<GraphNode<StepDefine>> nodes = new ArrayList<>();
        for (StepDefine step : getSteps()) {
            if (step != null) {
                nodes.add(GraphNode.init(step.name(), step));
            }
        }
        return nodes;
    }

    void linkNodes() {
        if (stepDefineGraph == null) return;
        for (StepDefine step : getSteps()) {
            GraphNode<StepDefine> node = stepDefineGraph.findNodeByName(step.name());
            for (String dependentStepName : step.dependencyDoneStepNames()) {
                GraphNode<StepDefine> prevNode = stepDefineGraph.findNodeByName(dependentStepName);
                node.linkPrevNode(prevNode);
            }
        }
    }

    // -----

    private void buildPhaseList() {
        stepDefinePhaseList = new PhaseList<>();
        for (StepDefine step : getSteps()) {
            if (step != null) {
                stepDefinePhaseList.addNode(step.phaseValue(), step.getStepName(), step);
            }
        }
    }

    // -------------

    /**
     * json serialize and deserialize
     */
    private static ObjectReader reader = JsonParseUtil.getReader(JobDefine.class);
    private static ObjectWriter writer = JsonParseUtil.getWriter(JobDefine.class);

    public static JobDefine fromJson(String json) throws IOException {
        JobDefine pin = null;
        if (StringUtils.isNotBlank(json)) {
            pin = reader.readValue(json);
        }
        return pin;
    }
    public String toJson() throws JsonProcessingException {
        return writer.writeValueAsString(this);
    }
}
