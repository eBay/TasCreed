package com.ebay.magellan.tascreed.core.domain.validate;

import com.ebay.magellan.tascreed.core.domain.define.JobDefine;
import com.ebay.magellan.tascreed.core.domain.define.StepDefine;
import com.ebay.magellan.tascreed.core.domain.graph.Graph;
import com.ebay.magellan.tascreed.core.domain.graph.GraphNode;
import com.ebay.magellan.tascreed.core.domain.graph.Phase;
import com.ebay.magellan.tascreed.core.domain.graph.PhaseList;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class StepPhaseListValidator implements Validator<JobDefine> {

    private static final String DataName = "StepPhaseList";

    public ValidateResult validate(JobDefine jobDefine) {
        ValidateResult result = ValidateResult.init(DataName);
        if (jobDefine == null) return result;

        PhaseList<StepDefine> stepPhaseList = jobDefine.getStepDefinePhaseList();
        result.setTitle(String.format("%s (%s)", DataName, jobDefine.getJobName()));
        if (stepPhaseList == null) {
            result.addMsg("step phase list is null");
        } else {
            List<String> dependLaterPhaseSteps = stepsDependOnLaterPhase(stepPhaseList, jobDefine.getStepDefineGraph());
            if (CollectionUtils.isNotEmpty(dependLaterPhaseSteps)) {
                String dependLaterPhaseStepsStr = StringUtils.join(dependLaterPhaseSteps, ", ");
                result.addMsg(String.format(
                        "these steps depend on steps in later phase: %s", dependLaterPhaseStepsStr));
            }
        }
        return result;
    }

    List<String> stepsDependOnLaterPhase(PhaseList<StepDefine> stepPhaseList, Graph<StepDefine> stepGraph) {
        if (stepPhaseList == null || stepGraph == null) return null;
        List<String> ret = new ArrayList<>();
        for (Phase<StepDefine> phase : stepPhaseList.getPhases()) {
            for (StepDefine step : phase.getNodes().values()) {
                GraphNode<StepDefine> node = stepGraph.findNodeByName(step.getStepName());
                int curPhase = node.getData().phaseValue();
                String curStepName = node.getData().getStepName();
                for (GraphNode<StepDefine> prevNode : node.getPrevNodes()) {
                    int dependentStepPhase = prevNode.getData().phaseValue();
                    if (curPhase < dependentStepPhase) {
                        ret.add(curStepName);
                        break;
                    }
                }
            }
        }
        return ret;
    }


    // -----
}
