package com.ebay.magellan.tumbler.core.schedule.time;

import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class DedupKeys {

    private static final long existDuration = 1 * 60 * 60 * 1000L;  // 1 hour
    private static final long timeSlotDuration = 10 * 60 * 1000L;  // 10 min

    private long lastTruncTs = 0L;

    private Map<Long, Set<String>> time2Keys = new LinkedHashMap<>();

    private long now() {
        return System.currentTimeMillis();
    }
    private long expireTs() {
        return System.currentTimeMillis() - existDuration;
    }

    private long ts2timeSlot(long ts) {
        return ts / timeSlotDuration * timeSlotDuration;
    }

    private Set<String> getKeySetByTime(long ts) {
        long timeSlot = ts2timeSlot(ts);
        if (timeSlot < expireTs()) return null;
        if (!time2Keys.containsKey(timeSlot)) {
            time2Keys.put(timeSlot, new ConcurrentSkipListSet<>());
        }
        return time2Keys.get(timeSlot);
    }

    // -----

    public boolean insertKey(long ts, String key) {
        if (StringUtils.isBlank(key)) return false;
        Set<String> keySet = getKeySetByTime(ts);
        if (keySet == null) return false;
        if (keySet.contains(key)) return false;
        keySet.add(key);

        if (lastTruncTs < expireTs()) {
            truncExpiredKeys();
            lastTruncTs = now();
        }

        return true;
    }

    // -----

    private void truncExpiredKeys() {
        long et = expireTs();
        List<Long> expiredTimeSlots = time2Keys.keySet().stream()
                .filter(a -> a < et).collect(Collectors.toList());
        for (Long timeSlot : expiredTimeSlots) {
            time2Keys.remove(timeSlot);
        }
    }

}
