package com.ebay.magellan.tumbler.depend.common.msg;

public class MsgItem<T> {
    T item;

    public MsgItem(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }
}
