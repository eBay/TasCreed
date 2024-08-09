package com.ebay.magellan.tascreed.core.domain.define;

import com.ebay.magellan.tascreed.core.domain.graph.Graph;
import com.ebay.magellan.tascreed.core.domain.graph.PhaseList;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class JobDefineTest {

    String str = "{\"jobName\":\"sample\",\"version\":1,\"params\":{\"p1\":\"0\"},\"traits\":[\"UNKNOWN\"],\"steps\":[{\"stepName\":\"sample-prep\",\"stepType\":\"SHARD\",\"shardConf\":{\"shard\":4},\"dependency\":{\"phase\":2}},{\"stepName\":\"sample-calc\",\"stepType\":\"PACK\",\"packConf\":{\"size\":100,\"start\":0,\"end\":1005},\"dependentStep\":\"sample-prep\"},{\"stepName\":\"sample-aggr-1\",\"dependentStep\":\"sample-calc\"},{\"stepName\":\"sample-aggr-2\",\"dependentStep\":\"sample-calc\",\"params\":{\"p1\":\"2\",\"p2\":\"4\"}}]}";

    @Test
    public void fromJson() throws Exception {
        JobDefine jd = JobDefine.fromJson(str);
        assertEquals("sample", jd.getJobName());
        assertEquals(1, jd.getVersion());

        Map<String, String> params = jd.getParams();
        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals("0", params.get("p1"));

        assertEquals(4, jd.getSteps().size());

        System.out.println(jd.toJson());
    }

    @Test
    public void findStepDefineByName() throws Exception {
        JobDefine jd = JobDefine.fromJson(str);

        assertNotNull(jd.findStepDefineByName("sample-calc"));
        assertNull(jd.findStepDefineByName("unknown"));
        assertNull(jd.findStepDefineByName(null));
    }

    @Test
    public void postBuild() throws Exception {
        JobDefine jd = JobDefine.fromJson(str);

        jd.postBuild();

        Graph<StepDefine> graph = jd.getStepDefineGraph();
        assertNotNull(graph);
        assertEquals(4, graph.getAllNodes().size());
        assertEquals(4, jd.getSteps().size());
        assertEquals(4, jd.getStepsMap().size());

        PhaseList<StepDefine> phaseList = jd.getStepDefinePhaseList();
        assertNotNull(phaseList);
        assertEquals(2, phaseList.getPhases().size());
        assertEquals(0, phaseList.getPhases().get(0).getPhase());
        assertEquals(3, phaseList.getPhases().get(0).getNodes().size());
        assertEquals(2, phaseList.getPhases().get(1).getPhase());
        assertEquals(1, phaseList.getPhases().get(1).getNodes().size());
        assertNull(phaseList.findPhase(1));
    }

}
