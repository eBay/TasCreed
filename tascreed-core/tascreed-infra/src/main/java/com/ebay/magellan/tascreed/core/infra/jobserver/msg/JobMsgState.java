package com.ebay.magellan.tascreed.core.infra.jobserver.msg;

import com.ebay.magellan.tascreed.core.domain.job.JobInstKey;
import com.ebay.magellan.tascreed.depend.common.msg.MsgItem;
import com.ebay.magellan.tascreed.depend.common.msg.MsgState;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class JobMsgState extends MsgState<JobInstKey> {
    private boolean all = false;
    private Set<JobInstKey> keys = new HashSet<>();

    public static JobMsgState parseFrom(MsgState state) {
        if (state != null && state instanceof JobMsgState) {
            return (JobMsgState) state;
        } else {
            return null;
        }
    }

    @Override
    protected boolean addItemImpl(MsgItem<JobInstKey> item) {
        JobInstKey key = item.getItem();
        if (key == null) {
            all = true;
        } else {
            keys.add(key);
            if (keys.size() >= 5) all = true;
        }
        return true;
    }

}