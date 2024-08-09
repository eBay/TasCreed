package com.ebay.magellan.tascreed.core.domain.job;

import com.ebay.magellan.tascreed.core.domain.define.JobDefine;
import com.ebay.magellan.tascreed.core.domain.graph.Graph;
import com.ebay.magellan.tascreed.core.domain.graph.GraphNode;
import com.ebay.magellan.tascreed.core.domain.graph.PhaseList;
import com.ebay.magellan.tascreed.core.domain.job.help.TaskCreationConditioner;
import com.ebay.magellan.tascreed.core.domain.job.mid.JobMidState;
import com.ebay.magellan.tascreed.core.domain.state.JobStateEnum;
import com.ebay.magellan.tascreed.core.domain.state.StepStateEnum;
import com.ebay.magellan.tascreed.core.domain.state.partial.Progression;
import com.ebay.magellan.tascreed.core.domain.trait.Trait;
import com.ebay.magellan.tascreed.core.domain.trait.Traits;
import com.ebay.magellan.tascreed.depend.common.util.DefaultValueUtil;
import com.ebay.magellan.tascreed.depend.common.util.JsonParseUtil;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Job {
    @JsonProperty("jobName")
    @JsonAlias({"name"})
    private String jobName;
    @JsonProperty("trigger")
    private String trigger;
    @JsonProperty("priority")
    private Integer priority;

    @JsonProperty("params")
    private Map<String, String> params;     // job init params
    @JsonProperty("updatedParams")
    private Map<String, String> updatedParams;      // job global params

    @JsonProperty("steps")
    private List<JobStep> steps;

    @JsonUnwrapped
    private Progression progression;

    @JsonProperty("state")
    private JobStateEnum state;

    @JsonProperty("traits")
    private List<String> traitStrList;
    @JsonIgnore
    private Traits traits = new Traits(Trait.TraitType.JOB);

    // wrap all the mid state in an object
    @JsonUnwrapped
    private JobMidState midState = new JobMidState();

    @JsonIgnore
    private JobDefine jobDefine;

    @JsonIgnore
    private Graph<JobStep> stepGraph;
    @JsonIgnore
    private Map<String, JobStep> stepsMap;
    @JsonIgnore
    private PhaseList<JobStep> stepPhaseList;

    @JsonIgnore
    private String fromValue;

    public Job(JobDefine jobDefine) {
        this.jobDefine = jobDefine;
    }

    public void addParam(String k, String v) {
        if (k == null) return;
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(k, v);
    }

    public void addUpdatedParam(String k, String v) {
        if (k == null) return;
        if (updatedParams == null) {
            updatedParams = new HashMap<>();
        }
        updatedParams.put(k, v);
    }

    public JobStateEnum getState() {
        return DefaultValueUtil.defValue(state, JobStateEnum.UNDONE);
    }

    public JobStep findStepByName(String name) {
        if (MapUtils.isNotEmpty(stepsMap)) {
            return stepsMap.get(name);
        } else if (CollectionUtils.isNotEmpty(steps)) {
            for (JobStep step : steps) {
                if (step != null && StringUtils.equals(name, step.getStepName())) {
                    return step;
                }
            }
        }
        return null;
    }

    // -----

    public void setTraitStrList(List<String> traitStrList) {
        this.traitStrList = traitStrList;
        traits.genTraitSetDirectly(traitStrList);
    }

    public List<String> getTraitStrList() {
        this.traitStrList = traits.genTraitStrList();
        return traitStrList;
    }

    // -----

    // post build of job, to build step map and graph
    public void postBuild() {
        if (steps == null) {
            steps = new ArrayList<>();
        }
        buildStepMap();
        buildGraph();
        buildPhaseList();
    }

    // -----

    private void buildStepMap() {
        if (stepsMap == null) {
            stepsMap = new LinkedHashMap<>();
        }
        stepsMap.clear();
        for (JobStep step : steps) {
            if (step != null) {
                stepsMap.put(step.getStepName(), step);
            }
        }
    }

    // -----

    private void buildGraph() {
        stepGraph = new Graph<>(buildGraphNodes());
        linkNodes();
    }

    List<GraphNode<JobStep>> buildGraphNodes() {
        List<GraphNode<JobStep>> nodes = new ArrayList<>();
        for (JobStep step : getSteps()) {
            if (step != null) {
                nodes.add(GraphNode.init(step.getStepName(), step));
            }
        }
        return nodes;
    }

    void linkNodes() {
        if (stepGraph == null) return;
        for (JobStep step : getSteps()) {
            GraphNode<JobStep> node = stepGraph.findNodeByName(step.getStepName());
            if (step.getStepDefine() == null) continue;
            for (String prevStepName : step.getStepDefine().dependencyDoneStepNames()) {
                GraphNode<JobStep> prevNode = stepGraph.findNodeByName(prevStepName);
                node.linkPrevNode(prevNode);
            }
        }
    }

    // -----

    private void buildPhaseList() {
        stepPhaseList = new PhaseList<>();
        for (JobStep step : getSteps()) {
            if (step == null || step.getStepDefine() == null) continue;
            stepPhaseList.addNode(step.getStepDefine().phaseValue(), step.getStepName(), step);
        }
    }

    // -----

    boolean updateJobState(JobStateEnum newState) {
        if (newState == null || newState.equals(getState())) return false;
        setState(newState);
        return true;
    }

    public boolean updateJobStateByStepStates() {
        JobStateEnum oldJobState = getState();
        JobStateEnum newJobState = oldJobState;
        if (CollectionUtils.isEmpty(steps)) {
            newJobState = JobStateEnum.SUCCESS;
        } else if (oldJobState.isUndone()) {
            List<StepStateEnum> stepStates = new ArrayList<>();
            for (JobStep step : steps) {
                if (step == null) continue;
                stepStates.add(step.getState());
            }
            newJobState = JobStateEnum.getJobState(oldJobState, stepStates);

            // still undone, then check if stuck
            if (newJobState.isUndone()) {

                // check if job is stuck: no step node can create task, but still some step need to create task
                List<GraphNode<JobStep>> stepNodesCanCreateTasks = TaskCreationConditioner.getStepNodesCanCreateTasks(this);
                List<GraphNode<JobStep>> stepNodesInReadyState = TaskCreationConditioner.getStepNodesInReadyState(this);
                List<GraphNode<JobStep>> stepNodesNeedCreateTasks = TaskCreationConditioner.getStepNodesNeedCreateTasks(this);
                if (CollectionUtils.isEmpty(stepNodesCanCreateTasks)
                        && CollectionUtils.isEmpty(stepNodesInReadyState)
                        && CollectionUtils.isNotEmpty(stepNodesNeedCreateTasks)) {
                    newJobState = JobStateEnum.STUCK;
                }
            }
        }

        boolean updated = updateJobState(newJobState);

        return updated;
    }

    public boolean resetForRetry() {
        boolean updated = false;
        if (getState().canRetry()) {
            updated = updateJobState(JobStateEnum.UNDONE);
        }

        if (CollectionUtils.isNotEmpty(steps)) {
            for (JobStep step : steps) {
                if (step != null) {
                    updated = step.resetForRetry() || updated;
                }
            }
        }
        return updated;
    }

    // -----

    private final static long EFFORT_WEIGHT = 10000L;

    public void updateProgression() {
        if (CollectionUtils.isEmpty(steps) || getState().resultSuccess()) {
            progression = Progression.buildFullProgression();
        } else {
            long total = 0L;
            long done = 0L;
            for (JobStep step : steps) {
                if (step != null) {
                    long effort = EFFORT_WEIGHT *
                            (step.getStepDefine() != null ? step.getStepDefine().getEffort() : 1);
                    total += effort;
                    if (step.getState().resultSuccess()) {
                        done += effort;
                    } else {
                        if (step.getProgression() != null) {
                            done += effort * step.getProgression().getDone() / step.getProgression().getTotal();
                        }
                    }
                }
            }
            progression = Progression.buildProgression(done, total);
        }
    }

    // -----

    /**
     * json serialize and deserialize
     */
    private static ObjectReader reader = JsonParseUtil.getReader(Job.class);
    private static ObjectWriter writer = JsonParseUtil.getWriter(Job.class);

    public static Job fromJson(String json) throws IOException {
        Job pin = null;
        if (StringUtils.isNotBlank(json)) {
            pin = reader.readValue(json);
        }
        if (pin != null) {
            pin.setFromValue(json);
        }
        return pin;
    }

    public String toJson() throws JsonProcessingException {
        return writer.writeValueAsString(this);
    }
}