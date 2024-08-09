package com.ebay.magellan.tumbler.core.schedule.time.task;

import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class AbstractTimerTask implements TimerTask {

    private String key;
    private long triggerTime;

    @Override
    public void run(Timeout timeout) throws Exception {
        exec(timeout);
    }

    // -----

    public abstract void exec(Timeout timeout) throws Exception;

}
