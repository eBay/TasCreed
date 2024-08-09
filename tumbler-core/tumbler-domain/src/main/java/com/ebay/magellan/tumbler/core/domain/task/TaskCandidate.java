package com.ebay.magellan.tumbler.core.domain.task;

import com.ebay.magellan.tumbler.core.domain.define.StepTypeEnum;
import com.ebay.magellan.tumbler.core.domain.task.conf.TaskPackConf;
import com.ebay.magellan.tumbler.core.domain.task.conf.TaskShardConf;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskCandidate implements Comparable<TaskCandidate> {
    private Task task;
    private String adoptionKey;
    private WeightLabel weight;

    private int emptyRate = TOTAL_RATE;
    private static final int TOTAL_RATE = 1000;

    public TaskCandidate(Task task, String adoptionKey, WeightLabel weight) {
        this.task = task;
        this.adoptionKey = adoptionKey;
        this.weight = weight;
    }

    // -----

    public void setEmptyRate(int total, int active) {
        int calcEmptyRate = (total - active) * TOTAL_RATE / total;
        this.emptyRate = Math.min(TOTAL_RATE, Math.max(0, calcEmptyRate));
    }

    // -----

    // compare priority first, then affinity
    public int compareTo(TaskCandidate other) {
        // 1. compare by weight
        int result = getWeight().compareTo(other.getWeight());
        if (result == 0) {
            // 2. compare by empty rate
            result = getEmptyRate() - other.getEmptyRate();
            if (result == 0) {
                // 3. compare by task information
                Task t1 = this.getTask();
                Task t2 = other.getTask();

                // compare job name, trigger, step type, step name, pack id / shard id
                int r = t1.getJobName().compareTo(t2.getJobName());
                if (r == 0) {
                    r = t1.getTrigger().compareTo(t2.getTrigger());
                    if (r == 0) {
                        int w1 = StepTypeEnum.getOrder(t1.getStepType());
                        int w2 = StepTypeEnum.getOrder(t2.getStepType());
                        r = w1 - w2;
                        if (r == 0) {
                            r = t1.getStepName().compareTo(t2.getStepName());
                            if (r == 0) {
                                if (t1.isPack() && t2.isPack()) {
                                    TaskPackConf c1 = t1.getTaskAllConf().getPackConf();
                                    TaskPackConf c2 = t2.getTaskAllConf().getPackConf();
                                    r = Long.valueOf(c1.getId()).compareTo(Long.valueOf(c2.getId()));
                                } else if (t1.isShard() && t2.isShard()) {
                                    TaskShardConf c1 = t1.getTaskAllConf().getShardConf();
                                    TaskShardConf c2 = t2.getTaskAllConf().getShardConf();
                                    r = Long.valueOf(c1.getIndex()).compareTo(Long.valueOf(c2.getIndex()));
                                }
                            }
                        }
                    }
                }
                result = 0 - r;     // minor name will be prior
            }
        }
        return result;
    }
}
