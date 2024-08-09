package com.ebay.magellan.tascreed.core.domain.builder;

import com.ebay.magellan.tascreed.core.domain.define.JobDefine;
import com.ebay.magellan.tascreed.core.domain.help.TestRepo;
import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.job.JobStep;
import com.ebay.magellan.tascreed.core.domain.trait.Trait;
import org.junit.Test;

import static org.junit.Assert.*;

public class JobBuilderTest {

    private JobBuilder jobBuilder = new JobBuilder();

    @Test
    public void buildJob1() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest1);
        assertEquals("sample", job.getJobName());
        assertEquals("20191020", job.getTrigger());
        assertEquals(Integer.valueOf(2), job.getPriority());
        assertEquals("1", job.getParams().get("p1"));
        assertEquals("2", job.getParams().get("p2"));
        assertEquals(4, job.getSteps().size());
        assertEquals(Integer.valueOf(6), job.getSteps().get(0).getStepAllConf().getShardConf().getShard());
        assertEquals(2, job.getSteps().get(0).getStepAllConf().getShardConf().taskCount());
        assertFalse(job.getSteps().get(3).getState().isIgnored());

        JobStep prep = job.findStepByName("prep");
        assertFalse(prep.getTraits().containsTrait(Trait.CAN_IGNORE));
        assertFalse(prep.getTraits().containsTrait(Trait.DELETED));
        assertTrue(prep.getTraits().containsTrait(Trait.CAN_FAIL));
        assertTrue(prep.getTraits().containsTrait(Trait.ARCHIVE));

        assertFalse(job.getTraits().containsTrait(Trait.CAN_IGNORE));
        assertFalse(job.getTraits().containsTrait(Trait.DELETED));
        assertFalse(job.getTraits().containsTrait(Trait.CAN_FAIL));
        assertFalse(job.getTraits().containsTrait(Trait.ARCHIVE));
    }

    @Test
    public void buildJob2() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest2);
        assertEquals("sample", job.getJobName());
        assertEquals("20191020", job.getTrigger());
        assertEquals(Integer.valueOf(1), job.getPriority());
        assertEquals("1", job.getParams().get("p1"));
        assertEquals("2", job.getParams().get("p2"));
        assertEquals(4, job.getSteps().size());
        assertEquals(Integer.valueOf(4), job.getSteps().get(0).getStepAllConf().getShardConf().getShard());
        assertEquals(20, job.getSteps().get(0).getStepAllConf().getShardConf().taskCount());
        assertTrue(job.getSteps().get(3).needIgnore());

        JobStep prep = job.findStepByName("prep");
        assertFalse(prep.getTraits().containsTrait(Trait.CAN_IGNORE));
        assertFalse(prep.getTraits().containsTrait(Trait.DELETED));
        assertFalse(prep.getTraits().containsTrait(Trait.CAN_FAIL));
        assertTrue(prep.getTraits().containsTrait(Trait.ARCHIVE));

        JobStep aggr = job.findStepByName("aggr-2");
        assertFalse(aggr.getTraits().containsTrait(Trait.CAN_IGNORE));
        assertFalse(aggr.getTraits().containsTrait(Trait.DELETED));
        assertFalse(aggr.getTraits().containsTrait(Trait.CAN_FAIL));
        assertTrue(aggr.getTraits().containsTrait(Trait.ARCHIVE));

        assertFalse(job.getTraits().containsTrait(Trait.CAN_IGNORE));
        assertFalse(job.getTraits().containsTrait(Trait.DELETED));
        assertFalse(job.getTraits().containsTrait(Trait.CAN_FAIL));
        assertFalse(job.getTraits().containsTrait(Trait.ARCHIVE));
    }

    // -----

    @Test
    public void assembleJob() {
        JobDefine jobDefine = TestRepo.jobDefine;
        Job job = jobBuilder.buildJob(jobDefine, TestRepo.jobRequest1);
        jobBuilder.assembleJob(job, jobDefine);

        assertEquals(4, job.getStepsMap().size());

        assertEquals(jobDefine, job.getJobDefine());
        assertEquals(4, job.getStepGraph().getAllNodes().size());
        assertEquals(jobDefine.getSteps().get(0), job.getSteps().get(0).getStepDefine());
    }
}
