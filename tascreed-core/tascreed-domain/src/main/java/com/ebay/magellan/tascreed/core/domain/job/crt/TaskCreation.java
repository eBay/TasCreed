package com.ebay.magellan.tascreed.core.domain.job.crt;

import com.ebay.magellan.tascreed.core.domain.define.conf.StepConf;

public interface TaskCreation<T extends StepConf> {
    boolean isDone(T sc);
}
