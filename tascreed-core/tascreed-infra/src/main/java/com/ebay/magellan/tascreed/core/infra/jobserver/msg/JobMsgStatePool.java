package com.ebay.magellan.tascreed.core.infra.jobserver.msg;

import com.ebay.magellan.tascreed.core.domain.job.JobInstKey;
import com.ebay.magellan.tascreed.depend.common.msg.MsgStatePool;

public class JobMsgStatePool extends MsgStatePool<JobInstKey> {

    private JobMsgStatePool() {}
    private final static JobMsgStatePool instance = new JobMsgStatePool();
    public static JobMsgStatePool getInstance() {
        return instance;
    }

    // -----

    @Override
    protected JobMsgState initState() {
        return new JobMsgState();
    }

}
