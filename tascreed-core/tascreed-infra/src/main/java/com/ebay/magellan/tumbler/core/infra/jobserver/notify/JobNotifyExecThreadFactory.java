package com.ebay.magellan.tumbler.core.infra.jobserver.notify;

import com.ebay.magellan.tumbler.depend.common.thread.DefaultThreadFactory;
import org.springframework.stereotype.Component;

@Component
public class JobNotifyExecThreadFactory extends DefaultThreadFactory {
    JobNotifyExecThreadFactory() {
        super();
        namePrefix = "tumbler-job-update-thread-";
    }

    public JobNotifyExecThread buildJobUpdateThread() {
        JobNotifyExecThread jobNotifyExecThread = context.getBean(JobNotifyExecThread.class);
        return jobNotifyExecThread;
    }
}
