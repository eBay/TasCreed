package com.ebay.magellan.tascreed.core.infra.jobserver.notify;

import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadFactory;
import org.springframework.stereotype.Component;

@Component
public class JobNotifyExecThreadFactory extends DefaultThreadFactory {
    JobNotifyExecThreadFactory() {
        super();
        namePrefix = "tascreed-job-update-thread-";
    }

    public JobNotifyExecThread buildJobUpdateThread() {
        JobNotifyExecThread jobNotifyExecThread = context.getBean(JobNotifyExecThread.class);
        return jobNotifyExecThread;
    }
}
