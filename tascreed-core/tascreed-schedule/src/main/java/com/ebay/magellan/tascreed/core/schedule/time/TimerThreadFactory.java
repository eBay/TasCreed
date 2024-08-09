package com.ebay.magellan.tascreed.core.schedule.time;

import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadFactory;
import org.springframework.stereotype.Component;

@Component
public class TimerThreadFactory extends DefaultThreadFactory {

    TimerThreadFactory() {
        super();
        namePrefix = "timer-thread-";
    }

}
