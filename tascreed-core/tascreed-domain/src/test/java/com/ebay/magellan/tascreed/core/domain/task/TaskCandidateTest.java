package com.ebay.magellan.tascreed.core.domain.task;

import com.ebay.magellan.tascreed.core.domain.util.SortUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TaskCandidateTest {

    Task buildTask(int priority) {
        Task t = new Task();
        t.setPriority(priority);
        t.setJobName("job");
        t.setStepName("step");
        return t;
    }

    TaskCandidate buildTaskCandidate(Task task, String key) {
        return new TaskCandidate(task, key, new WeightLabel(
                task.getPriorityWeight(), 1));
    }

    @Test
    public void testTaskCandidateSort1() {
        List<TaskCandidate> taskCandidates = new ArrayList<>();

        TaskCandidate tc1 = buildTaskCandidate(buildTask(1), "k1");
        TaskCandidate tc2 = buildTaskCandidate(buildTask(2), "k2");
        TaskCandidate tc3 = buildTaskCandidate(buildTask(3), "k3");

        taskCandidates.add(tc1);
        taskCandidates.add(tc2);
        taskCandidates.add(tc3);

        TaskCandidate winner = SortUtil.getFirstCandidate(taskCandidates);
        assertEquals(tc3, winner);
    }

    @Test
    public void testTaskCandidateSort2() {
        List<TaskCandidate> taskCandidates = new ArrayList<>();

        TaskCandidate tc1 = buildTaskCandidate(buildTask(1), "k1");
        TaskCandidate tc2 = buildTaskCandidate(buildTask(1), "k2");
        TaskCandidate tc3 = buildTaskCandidate(buildTask(1), "k3");
        tc1.setEmptyRate(10, 4);
        tc2.setEmptyRate(20, 7);
        tc3.setEmptyRate(5, 3);

        taskCandidates.add(tc1);
        taskCandidates.add(tc2);
        taskCandidates.add(tc3);

        TaskCandidate winner = SortUtil.getFirstCandidate(taskCandidates);
        assertEquals(tc2, winner);
    }

    @Test
    public void testTaskCandidateSort3() {
        List<TaskCandidate> taskCandidates = new ArrayList<>();

        TaskCandidate tc1 = buildTaskCandidate(buildTask(1), "k1");
        TaskCandidate tc2 = buildTaskCandidate(buildTask(1), "k2");
        TaskCandidate tc3 = buildTaskCandidate(buildTask(1), "k3");
        tc1.setEmptyRate(1, 0);
        tc2.setEmptyRate(20, 15);
        tc3.setEmptyRate(5, 3);

        taskCandidates.add(tc1);
        taskCandidates.add(tc2);
        taskCandidates.add(tc3);

        TaskCandidate winner = SortUtil.getFirstCandidate(taskCandidates);
        assertEquals(tc1, winner);
    }
}
