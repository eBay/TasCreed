package com.ebay.magellan.tumbler.core.infra.jobserver.msg;

import com.ebay.magellan.tumbler.core.domain.job.JobInstKey;
import com.ebay.magellan.tumbler.depend.common.msg.MsgStatePool;

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
