package com.ebay.magellan.tascreed.depend.ext.etcd.util;

import io.etcd.jetcd.Txn;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.lease.LeaseRevokeResponse;
import io.etcd.jetcd.options.PutOption;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import com.ebay.magellan.tascreed.depend.ext.etcd.lock.EtcdLock;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface EtcdUtil {

    // transfer string to ByteSequence
    default ByteSequence bs(String s) {
        return ByteSequence.from(s, Charset.forName("UTF-8"));
    }

    // -----

    // check if the key exists
    boolean existKey(String key);

    // get value of a single key
    String getSingleValue(String key);

    // get value of a single key, or use default value if value is blank
    String getSingleValueOrDefault(String key, String defaultValue);

    // get kv map with prefix
    Map<String, String> getKVMapWithPrefix(String prefix) throws Exception;

    // -----

    // grant lease, return the lease id
    long grantLease(long seconds) throws Exception;

    // lock
    EtcdLock lock(String lockKey) throws Exception;

    // unlock
    void unlock(EtcdLock lock) throws Exception;

    // -----

    // delete key
    void deleteKey(String key) throws Exception;

    // delete key if the value equals to the expected value
    boolean deleteIfEquals(String key, String expectedValue) throws Exception;

    // delete kv map with prefix
    Map<String, String> deleteKVMapWithPrefix(String prefix) throws Exception;

    // -----

    // put key value
    CompletableFuture<PutResponse> put(String key, String value, PutOption option);

    // create a transaction
    Txn txn();

    // keep alive one lease only once
    CompletableFuture<LeaseKeepAliveResponse> keepAliveOnce(long leaseId);

    // revoke one lease and the key bind to this lease will be removed.
    CompletableFuture<LeaseRevokeResponse> revoke(long leaseId);

    // -----

    // put key value with default option
    default void putKeyValue(String key, String value) throws TcException {
        putKeyValue(key, value, PutOption.DEFAULT);
    }

    // put key value with option
    default void putKeyValue(String key, String value, PutOption option) throws TcException {
        try {
            put(key, value, option).get();
        } catch (InterruptedException | ExecutionException e) {
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_NON_RETRY_EXCEPTION,
                    String.format("EtcdUtil putKeyValue error: %s", e.getMessage()));
        }
    }

}
