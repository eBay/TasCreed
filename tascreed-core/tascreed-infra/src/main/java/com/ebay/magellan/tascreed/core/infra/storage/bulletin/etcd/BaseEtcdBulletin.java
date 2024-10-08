package com.ebay.magellan.tascreed.core.infra.storage.bulletin.etcd;

import io.etcd.jetcd.ByteSequence;
import com.ebay.magellan.tascreed.core.infra.constant.TcKeys;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.BaseBulletin;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.ext.etcd.constant.EtcdConstants;
import com.ebay.magellan.tascreed.depend.ext.etcd.lock.EtcdLock;
import com.ebay.magellan.tascreed.depend.ext.etcd.util.EtcdUtil;

public abstract class BaseEtcdBulletin implements BaseBulletin {
    protected TcKeys tcKeys;
    protected EtcdConstants etcdConstants;
    protected EtcdUtil etcdUtil;
    protected TcLogger logger;

    public BaseEtcdBulletin(TcKeys tcKeys,
                            EtcdConstants etcdConstants,
                            EtcdUtil etcdUtil,
                            TcLogger logger) {
        this.tcKeys = tcKeys;
        this.etcdConstants = etcdConstants;
        this.etcdUtil = etcdUtil;
        this.logger = logger;
    }

    // -----

    protected ByteSequence bs(String s) {
        return etcdUtil.bs(s);
    }

    public EtcdLock lock(String lockKey) throws Exception {
        return etcdUtil.lock(lockKey);
    }

    public void unlock(EtcdLock lock) throws Exception {
        etcdUtil.unlock(lock);
    }

    public String getSingleValue(String key) {
        return etcdUtil.getSingleValue(key);
    }

    // -----

    public boolean deleteIfEquals(String k, String v) throws Exception {
        return etcdUtil.deleteIfEquals(k, v);
    }

    public void deleteKeyAnyway(String key) throws Exception {
        etcdUtil.deleteKey(key);
    }

    public void deletePrefixAnyway(String prefix) throws Exception {
        etcdUtil.deleteKVMapWithPrefix(prefix);
    }

    // -----

}
