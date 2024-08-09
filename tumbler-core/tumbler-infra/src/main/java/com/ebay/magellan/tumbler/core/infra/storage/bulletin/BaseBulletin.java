package com.ebay.magellan.tumbler.core.infra.storage.bulletin;

import com.ebay.magellan.tumbler.depend.ext.etcd.lock.EtcdLock;

public interface BaseBulletin {

    EtcdLock lock(String lockKey) throws Exception;

    void unlock(EtcdLock lock) throws Exception;

    String getSingleValue(String key);

    // -----

    boolean deleteIfEquals(String k, String v) throws Exception;

    void deleteKeyAnyway(String key) throws Exception;

    void deletePrefixAnyway(String prefix) throws Exception;

}
