package com.ebay.magellan.tascreed.depend.common.collection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyValuePair<K, V> {
    protected K key;
    protected V value;

    public KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
