package com.ebay.magellan.tascreed.core.schedule.time.task;

import io.netty.util.Timeout;
import lombok.Getter;

import java.util.concurrent.Semaphore;

public class SignalTimerTask extends AbstractTimerTask {

    @Getter
    private final Semaphore signal = new Semaphore(0);

    /**
     * the signal is released when timer triggers
     */
    @Override
    public void exec(Timeout timeout) throws Exception {
        signal.release();
    }

    /**
     * external thread wait and acquires the signal
     */
    public void acquire() {
        try {
            signal.acquire();
        } catch (Exception e) {
            // do nothing
        }
    }

}
