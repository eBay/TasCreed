package com.ebay.magellan.tumbler.core.domain.util;

import com.ebay.magellan.tumbler.core.domain.define.StepTypeEnum;
import com.ebay.magellan.tumbler.core.domain.task.Task;
import com.ebay.magellan.tumbler.core.domain.task.TaskCandidate;
import com.ebay.magellan.tumbler.core.domain.task.WeightLabel;
import com.ebay.magellan.tumbler.core.domain.task.conf.TaskPackConf;
import com.ebay.magellan.tumbler.core.domain.task.conf.TaskShardConf;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SortUtilTest {

    Task buildTask(String jobName, String trigger, StepTypeEnum stepType, String stepName, Long psId) {
        Task task = new Task();
        task.setJobName(jobName);
        task.setTrigger(trigger);
        task.setStepType(stepType);
        task.setStepName(stepName);
        switch (stepType) {
            case PACK:
                task.getTaskAllConf().setPackConf(new TaskPackConf(psId != null ? psId : 0, 0, 0));
                break;
            case SHARD:
                task.getTaskAllConf().setShardConf(new TaskShardConf(10, psId != null ? psId.intValue() : 0));
                break;
            default:
        }
        return task;
    }

    WeightLabel buildWeightLabel(int priority, int affinity) {
        return new WeightLabel(priority, affinity);
    }

    TaskCandidate buildTaskCandidate(Task task, WeightLabel wl) {
        return new TaskCandidate(task, "", wl);
    }

    @Test
    public void getFirstTaskCandidate1() {
        List<TaskCandidate> candidates = new ArrayList<>();

        TaskCandidate first = SortUtil.getFirstCandidate(candidates);
        assertNull(first);
    }

    @Test
    public void getFirstTaskCandidate2() {
        List<TaskCandidate> candidates = new ArrayList<>();
        candidates.add(buildTaskCandidate(
                buildTask("j1", "t", StepTypeEnum.SIMPLE, "s1", null),
                buildWeightLabel(5, 1)));
        candidates.add(buildTaskCandidate(
                buildTask("j1", "t", StepTypeEnum.SIMPLE, "s1", null),
                buildWeightLabel(10, 1)));

        TaskCandidate first = SortUtil.getFirstCandidate(candidates);
        assertNotNull(first);
        assertEquals(10, first.getWeight().getPriorityWeight());
    }

    @Test
    public void getFirstTaskCandidate3() {
        List<TaskCandidate> candidates = new ArrayList<>();
        candidates.add(buildTaskCandidate(
                buildTask("j2", "t", StepTypeEnum.SIMPLE, "s1", null),
                buildWeightLabel(5, 1)));
        candidates.add(buildTaskCandidate(
                buildTask("j1", "t", StepTypeEnum.SIMPLE, "s1", null),
                buildWeightLabel(5, 1)));

        TaskCandidate first = SortUtil.getFirstCandidate(candidates);
        assertNotNull(first);
        assertEquals("j1", first.getTask().getJobName());
    }

    @Test
    public void getFirstTaskCandidate4() {
        List<TaskCandidate> candidates = new ArrayList<>();

        TaskCandidate tc1 = buildTaskCandidate(
                buildTask("j1", "t", StepTypeEnum.PACK, "s1", 2L),
                buildWeightLabel(5, 1));
        TaskCandidate tc2 = buildTaskCandidate(
                buildTask("j1", "t", StepTypeEnum.SIMPLE, "s1", null),
                buildWeightLabel(5, 1));
        tc1.setEmptyRate(10, 4);
        tc2.setEmptyRate(5, 2);

        candidates.add(tc1);
        candidates.add(tc2);

        TaskCandidate first = SortUtil.getFirstCandidate(candidates);
        assertNotNull(first);
        assertEquals(tc2, first);
    }

    @Test
    public void getFirstTaskCandidate5() {
        List<TaskCandidate> candidates = new ArrayList<>();

        TaskCandidate tc1 = buildTaskCandidate(
                buildTask("j1", "t", StepTypeEnum.PACK, "s2", 1L),
                buildWeightLabel(5, 1));
        TaskCandidate tc2 = buildTaskCandidate(
                buildTask("j1", "t", StepTypeEnum.PACK, "s1", 2L),
                buildWeightLabel(5, 1));
        tc1.setEmptyRate(10, 4);
        tc2.setEmptyRate(5, 2);

        candidates.add(tc1);
        candidates.add(tc2);

        TaskCandidate first = SortUtil.getFirstCandidate(candidates);
        assertNotNull(first);
        assertEquals(tc2, first);
    }

    @Test
    public void getFirstTaskCandidate6() {
        List<TaskCandidate> candidates = new ArrayList<>();

        TaskCandidate tc1 = buildTaskCandidate(
                buildTask("j1", "t", StepTypeEnum.PACK, "s1", 3L),
                buildWeightLabel(5, 1));
        TaskCandidate tc2 = buildTaskCandidate(
                buildTask("j1", "t", StepTypeEnum.PACK, "s1", 2L),
                buildWeightLabel(5, 1));
        tc1.setEmptyRate(10, 4);
        tc2.setEmptyRate(5, 2);

        candidates.add(tc1);
        candidates.add(tc2);

        TaskCandidate first = SortUtil.getFirstCandidate(candidates);
        assertNotNull(first);
        assertEquals(tc2, first);
    }

    @Test
    public void getFirstTaskCandidate7() {
        List<TaskCandidate> candidates = new ArrayList<>();

        TaskCandidate tc1 = buildTaskCandidate(
                buildTask("j1", "t", StepTypeEnum.PACK, "s1", 3L),
                buildWeightLabel(5, 1));
        TaskCandidate tc2 = buildTaskCandidate(
                buildTask("j1", "t", StepTypeEnum.PACK, "s1", 2L),
                buildWeightLabel(5, 1));
        tc1.setEmptyRate(10, 3);
        tc2.setEmptyRate(5, 2);

        candidates.add(tc1);
        candidates.add(tc2);

        TaskCandidate first = SortUtil.getFirstCandidate(candidates);
        assertNotNull(first);
        assertEquals(tc1, first);
    }

    @Test
    public void getFirstTaskCandidate8() {
        List<TaskCandidate> candidates = new ArrayList<>();

        TaskCandidate tc1 = buildTaskCandidate(
                buildTask("j1", "t2", StepTypeEnum.PACK, "s1", 3L),
                buildWeightLabel(5, 1));
        TaskCandidate tc2 = buildTaskCandidate(
                buildTask("j1", "t1", StepTypeEnum.PACK, "s1", 2L),
                buildWeightLabel(5, 1));
        tc1.setEmptyRate(10, 0);
        tc2.setEmptyRate(5, 0);

        candidates.add(tc1);
        candidates.add(tc2);

        TaskCandidate first = SortUtil.getFirstCandidate(candidates);
        assertNotNull(first);
        assertEquals(tc2, first);
    }
}
