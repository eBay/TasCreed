package com.ebay.magellan.tumbler.core.domain.job.crt;

import com.ebay.magellan.tumbler.core.domain.define.conf.StepConf;

public interface TaskCreation<T extends StepConf> {
    boolean isDone(T sc);
}
