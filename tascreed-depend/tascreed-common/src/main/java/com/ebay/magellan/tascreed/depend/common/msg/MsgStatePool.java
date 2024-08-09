package com.ebay.magellan.tascreed.depend.common.msg;

import java.util.concurrent.Semaphore;

public abstract class MsgStatePool<T> {

    private volatile MsgState<T> curState = initState();
    private final Semaphore notEmpty = new Semaphore(0);

    // -----

    protected abstract MsgState<T> initState();

    public void addItem(MsgItem<T> item) {
        synchronized (this) {
            if (curState == null) curState = initState();
            boolean newState = curState.addItem(item);
            if (newState) {
                notEmpty.release();
            }
        }
    }

    public MsgState<T> readState() {
        try {
            notEmpty.acquire();
        } catch (Exception e) {
            // do nothing
        }

        MsgState<T> ret = null;
        synchronized (this) {
            ret = curState;
            curState = initState();
        }
        return ret;
    }

}
