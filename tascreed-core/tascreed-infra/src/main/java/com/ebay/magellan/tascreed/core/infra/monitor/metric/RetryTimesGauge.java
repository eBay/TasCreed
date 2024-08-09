package com.ebay.magellan.tascreed.core.infra.monitor.metric;

import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.depend.common.util.DefaultValueUtil;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

@Getter
public class RetryTimesGauge {
    private Map<String, GaugeValue> pickedTimesGaugeMap = new HashMap<>();
    private Map<String, GaugeValue> triedTimesGaugeMap = new HashMap<>();

    private int numRepickedTasks;
    private int maxRepickedTimes;
    private int numRetriedTasks;
    private int maxRetriedTimes;

    // -----

    private void resetRetryMetrics() {
        this.numRepickedTasks = 0;
        this.maxRepickedTimes = 0;
        this.numRetriedTasks = 0;
        this.maxRetriedTimes = 0;
    }

    // -----

    public void updateByAliveTasks(List<Task> tasks) {
        resetRetryMetrics();

        // aggregate by key
        Map<String, Integer> pickedTimesMap = new HashMap<>();
        Map<String, Integer> triedTimesMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(tasks)) {
            for (Task task : tasks) {
                if (task == null) continue;
                int picked = task.getMidState().pickedTimesValue();
                int tried = task.getMidState().triedTimesValue();
                aggregateByKey(pickedTimesMap, jobStepName(task), picked);
                aggregateByKey(triedTimesMap, jobStepName(task), tried);

                if (picked > 0) {
                    numRepickedTasks++;
                    maxRepickedTimes = Math.max(maxRepickedTimes, picked);
                }
                if (tried > 0) {
                    numRetriedTasks++;
                    maxRetriedTimes = Math.max(maxRetriedTimes, tried);
                }
            }
        }

        long t = System.currentTimeMillis();

        // update gauge map
        updateGaugeMap(pickedTimesGaugeMap, pickedTimesMap, t);
        updateGaugeMap(triedTimesGaugeMap, triedTimesMap, t);

        // expire gauge map
        expireGaugeMap(pickedTimesGaugeMap, t);
        expireGaugeMap(triedTimesGaugeMap, t);
    }

    private String jobStepName(Task task) {
        if (task == null) return null;
        return String.format("%s.%s", task.getJobName(), task.getStepName());
    }

    private void aggregateByKey(Map<String, Integer> aggrMap, String key, int value) {
        int nv = value;
        Integer av = aggrMap.get(key);
        if (av != null) {
            nv = av + value;
        }
        aggrMap.put(key, nv);
    }

    private void updateGaugeMap(Map<String, GaugeValue> gaugeMap, Map<String, Integer> aggrMap, long t) {
        // update keys in aggr map
        for (Map.Entry<String, Integer> entry : aggrMap.entrySet()) {
            String k = entry.getKey();
            int v = DefaultValueUtil.intValue(entry.getValue());
            GaugeValue gv = gaugeMap.get(k);
            if (gv == null) {
                gv = new GaugeValue(t);
                gaugeMap.put(k, gv);
            }
            if (gv != null) {
                gv.setValue(v, t);
            }
        }
        // update keys not in aggr map
        for (Map.Entry<String, GaugeValue> entry : gaugeMap.entrySet()) {
            String k = entry.getKey();
            GaugeValue gv = entry.getValue();
            if (gv != null && !aggrMap.containsKey(k)) {
                gv.reset(t);
            }
        }
    }

    private void expireGaugeMap(Map<String, GaugeValue> gaugeMap, long t) {
        // keys expired
        Set<String> expiredKeys = new HashSet<>();
        for (Map.Entry<String, GaugeValue> entry : gaugeMap.entrySet()) {
            String k = entry.getKey();
            GaugeValue gv = entry.getValue();
            if (gv != null && gv.expired(t)) {
                expiredKeys.add(k);
            }
        }
        // remove expired keys
        for (String k : expiredKeys) {
            gaugeMap.remove(k);
        }
    }

    // -----

    @Getter
    public class GaugeValue {
        int value;
        long expireTime;
        private static final long EXPIRE_INTERVAL_MS = 3600 * 1000L;   // keep data in mem for 1 hour, for prometheus server fetch

        public GaugeValue(long t) {
            this.expireTime = t + EXPIRE_INTERVAL_MS;
        }
        void reset(long t) {
            setValue(0, t);
        }
        void setValue(int v, long t) {
            if (value != v) {
                value = v;
                expireTime = t + EXPIRE_INTERVAL_MS;
            }
        }
        boolean expired(long t) {
            return expireTime < t;
        }
    }
}
