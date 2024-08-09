package com.ebay.magellan.tumbler.depend.common.msg;

public abstract class MsgState<T> {
    volatile boolean updated = false;

    boolean addItem(MsgItem<T> item) {
        if (item == null) return false;
        boolean initial = !updated;
        boolean changed = addItemImpl(item);
        if (changed) updated = true;
        boolean firstChanged = initial && updated;
        return firstChanged;
    }

    protected abstract boolean addItemImpl(MsgItem<T> item);
}
