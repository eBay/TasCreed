package com.ebay.magellan.tumbler.depend.ext.etcd.util.mock;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MemStore {

    private Map<String, String> store = new TreeMap<>();

    public void put(String key, String value) {
        store.put(key, value);
    }

    public String get(String key) {
        return store.get(key);
    }

    public boolean compare(String key, String value) {
        return value != null && value.equals(store.get(key));
    }

    public void delete(String key) {
        store.remove(key);
    }

    public boolean deleteIfEquals(String key, String expectedValue) {
        return store.remove(key, expectedValue);
    }

    public boolean exist(String key) {
        return store.containsKey(key);
    }

    public void clear() {
        store.clear();
    }

    public int size() {
        return store.size();
    }

    public boolean isEmpty() {
        return store.isEmpty();
    }

    public Map<String, String> getKVMapWithPrefix(String prefix) {
        Map<String, String> result = new TreeMap<>();
        for (Map.Entry<String, String> entry : store.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public Map<String, String> deleteKVMapWithPrefix(String prefix) {
        Map<String, String> result = new TreeMap<>();
        for (Map.Entry<String, String> entry : store.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        for (String key : result.keySet()) {
            store.remove(key);
        }
        return result;
    }

    // -----

    private Map<String, Long> locks = new ConcurrentHashMap<>();

    public Long lock(String key) {
        Long v = locks.get(key);
        if (v == null) {
            v = new Random().nextLong();
            locks.put(key, v);
        }
        return v;
    }

    public void unlock(String key, Long value) {
        locks.remove(key, value);
    }

    // -----

    private Map<Long, Pair<Long, Long>> leases = new ConcurrentHashMap<>();

    public Long grantLease(long seconds) {
        long now = System.currentTimeMillis();
        Long leaseId = Math.abs(new Random().nextLong());
        if (leases.containsKey(leaseId)) {
            return -1L;
        }
        long duration = seconds * 1000L;
        Pair<Long, Long> pair = Pair.of(duration, now + duration);
        leases.put(leaseId, pair);
        return leaseId;
    }

    public boolean keepAliveOnce(long leaseId) {
        long now = System.currentTimeMillis();
        Pair<Long, Long> pair = leases.get(leaseId);
        if (pair != null) {
            if (pair.getRight() > now) {
                pair = Pair.of(pair.getLeft(), now + pair.getLeft());
                leases.put(leaseId, pair);
                return true;
            }
        }
        return false;
    }

    public void revoke(long leaseId) {
        leases.remove(leaseId);
    }

}
