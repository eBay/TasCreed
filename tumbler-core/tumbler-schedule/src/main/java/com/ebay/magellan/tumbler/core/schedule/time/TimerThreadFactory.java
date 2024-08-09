package com.ebay.magellan.tumbler.core.schedule.time;

import com.ebay.magellan.tumbler.depend.common.thread.DefaultThreadFactory;
import org.springframework.stereotype.Component;

@Component
public class TimerThreadFactory extends DefaultThreadFactory {

    TimerThreadFactory() {
        super();
        namePrefix = "timer-thread-";
    }

}
