package com.ebay.magellan.tumbler.depend.common.collection;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class GeneralDataListMap<K, V> {

    protected Map<K, List<V>> map = new HashMap<>();

    public void append(K k, V v) {
        if (k == null || v == null) return;
        List<V> list = map.computeIfAbsent(k, key -> new ArrayList<>());
        list.add(v);
    }

    public void appendAll(K k, List<V> vs) {
        if (k == null || CollectionUtils.isEmpty(vs)) return;
        List<V> list = map.computeIfAbsent(k, key -> new ArrayList<>());
        list.addAll(vs);
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public List<V> get(K k) {
        return map.get(k);
    }

    public void put(K k, List<V> list) {
        map.put(k, list);
    }

    public void clear() {
        map.clear();
    }

    public void remove(K k) {
        map.remove(k);
    }

    public void remove(K k, V v) {
        List<V> vs = map.get(k);
        if (CollectionUtils.isNotEmpty(vs)) {
            vs.remove(v);
        }
    }

    public List<V> getFlatList() {
        List<V> all = new ArrayList<>();
        for (List<V> list : map.values()) {
            all.addAll(list);
        }
        return all;
    }

    public boolean isEmpty() {
        for (List<V> list : map.values()) {
            if (CollectionUtils.isNotEmpty(list)) {
                return false;
            }
        }
        return true;
    }

}
