package com.ebay.magellan.tascreed.core.infra.jobserver.msg;

import com.ebay.magellan.tascreed.core.domain.job.JobInstKey;
import com.ebay.magellan.tascreed.depend.common.msg.MsgItem;

public class JobMsgItem extends MsgItem<JobInstKey> {

    private JobMsgItem(JobInstKey jobId) {
        super(jobId);
    }

    // -----

    public static JobMsgItem refreshAll() {
        return new JobMsgItem(null);
    }
    public static JobMsgItem refresh(JobInstKey jobId) {
        return new JobMsgItem(jobId);
    }

}
