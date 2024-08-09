package com.ebay.magellan.tumbler.core.schedule.time;

import com.ebay.magellan.tumbler.core.schedule.time.task.AbstractTimerTask;
import io.netty.util.HashedWheelTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TimeWheel {
    @Autowired
    private TimerThreadFactory timerThreadFactory;

    private volatile HashedWheelTimer hwt = null;

    public void init() {
        if (hwt == null) {
            hwt = new HashedWheelTimer(timerThreadFactory, 500, TimeUnit.MICROSECONDS);
        }
    }

    public void stop() {
        if (hwt != null) {
            hwt.stop();
        }
    }

    // -----

    private DedupKeys dedupKeys = new DedupKeys();

    public void insert(AbstractTimerTask tt) {
        if (tt == null) return;
        if (dedupKeys.insertKey(tt.getTriggerTime(), tt.getKey())) {
            long now = System.currentTimeMillis();
            long delay = tt.getTriggerTime() > now ? tt.getTriggerTime() - now : 0L;
            hwt.newTimeout(tt, delay, TimeUnit.MILLISECONDS);
        }
    }
}
