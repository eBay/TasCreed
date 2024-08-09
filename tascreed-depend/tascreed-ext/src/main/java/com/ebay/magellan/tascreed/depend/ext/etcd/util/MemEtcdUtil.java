package com.ebay.magellan.tascreed.depend.ext.etcd.util;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Txn;
import io.etcd.jetcd.api.*;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.kv.TxnResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.lease.LeaseRevokeResponse;
import io.etcd.jetcd.op.TxnImpl;
import io.etcd.jetcd.options.PutOption;
import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tascreed.depend.ext.etcd.lock.EtcdLock;
import com.ebay.magellan.tascreed.depend.ext.etcd.util.mock.MemStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@ConditionalOnProperty(prefix = "tumbler", name = "etcd", havingValue = "mem", matchIfMissing = true)
public class MemEtcdUtil implements EtcdUtil {

    private static final String THIS_CLASS_NAME = MemEtcdUtil.class.getSimpleName();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    private MemStore memStore = new MemStore();

    // check if the key exists
    public boolean existKey(String key) {
        return memStore.exist(key);
    }

    // get value of a single key
    public String getSingleValue(String key) {
        return memStore.get(key);
    }

    // get value of a single key, or use default value if value is blank
    public String getSingleValueOrDefault(String key, String defaultValue) {
        String value = memStore.get(key);
        return value == null ? defaultValue : value;
    }

    // get kv map with prefix
    public Map<String, String> getKVMapWithPrefix(String prefix) throws Exception {
        return memStore.getKVMapWithPrefix(prefix);
    }

    // -----

    // grant lease, return the lease id
    public long grantLease(long seconds) throws Exception {
        return memStore.grantLease(seconds);
    }

    // lock
    public EtcdLock lock(String lockKey) throws Exception {
        Long v = memStore.lock(lockKey);
        return v != null ? new EtcdLock(v.longValue(), bs(lockKey)) : null;
    }

    // unlock
    public void unlock(EtcdLock lock) throws Exception {
        if (lock != null) {
            memStore.unlock(lock.getKey().toString(Charset.forName("UTF-8")), lock.getLeaseId());
        }
    }

    // -----

    // delete key
    public void deleteKey(String key) throws Exception {
        memStore.delete(key);
    }

    // delete key if the value equals to the expected value
    public boolean deleteIfEquals(String key, String expectedValue) throws Exception {
        return memStore.deleteIfEquals(key, expectedValue);
    }

    // delete kv map with prefix
    public Map<String, String> deleteKVMapWithPrefix(String prefix) throws Exception {
        return memStore.deleteKVMapWithPrefix(prefix);
    }

    // -----

    // put key value
    public CompletableFuture<PutResponse> put(String key, String value, PutOption option) {
        CompletableFuture<PutResponse> completableFuture = new CompletableFuture<>();
        memStore.put(key, value);
        PutResponse pr = new PutResponse(io.etcd.jetcd.api.PutResponse.newBuilder().build(), ByteSequence.EMPTY);
        completableFuture.complete(pr);
        return completableFuture;
    }

    // create a transaction
    public Txn txn() {
        return TxnImpl.newTxn(req -> {
            CompletableFuture<TxnResponse> completableFuture = new CompletableFuture<>();
            boolean cr = true;
            for (Compare c : req.getCompareList()) {
                String key = c.getKey().toStringUtf8();
                if (c.getTarget() == Compare.CompareTarget.VALUE) {
                    if (!memStore.compare(key, c.getValue().toStringUtf8())) cr = false;
                } else if (c.getTarget() == Compare.CompareTarget.CREATE) {
                    if (memStore.exist(key)) cr = false;
                } else {
                    System.out.println(c.getTarget());
                }
            }
            io.etcd.jetcd.api.TxnResponse.Builder builder = io.etcd.jetcd.api.TxnResponse.newBuilder().setSucceeded(cr);
            if (cr) {
                for (RequestOp s : req.getSuccessList()) {
                    if (s.hasRequestPut()) {
                        memStore.put(s.getRequestPut().getKey().toStringUtf8(), s.getRequestPut().getValue().toStringUtf8());
                        builder.addResponses(io.etcd.jetcd.api.ResponseOp.newBuilder().setResponsePut(
                                io.etcd.jetcd.api.PutResponse.newBuilder().build()
                        ).build());
                    } else if (s.hasRequestDeleteRange()) {
                        System.out.println(s.getRequestDeleteRange().getKey().toStringUtf8());
                        memStore.delete(s.getRequestDeleteRange().getKey().toStringUtf8());
                        builder.addResponses(io.etcd.jetcd.api.ResponseOp.newBuilder().setResponseDeleteRange(
                                io.etcd.jetcd.api.DeleteRangeResponse.newBuilder().build()
                        ).build());
                    }
                }
            }
            TxnResponse tr = new TxnResponse(builder.build(), ByteSequence.EMPTY);
            completableFuture.complete(tr);
            return completableFuture;
        }, ByteSequence.EMPTY);
    }

    // keep alive one lease only once
    public CompletableFuture<LeaseKeepAliveResponse> keepAliveOnce(long leaseId) {
        CompletableFuture<LeaseKeepAliveResponse> completableFuture = new CompletableFuture<>();
        if (memStore.keepAliveOnce(leaseId)) {
            LeaseKeepAliveResponse pr = new LeaseKeepAliveResponse(io.etcd.jetcd.api.LeaseKeepAliveResponse.newBuilder().setID(leaseId).build());
            completableFuture.complete(pr);
        } else {
            completableFuture.completeExceptionally(new Exception("lease not found"));
        }
        return completableFuture;
    }

    // revoke one lease and the key bind to this lease will be removed.
    public CompletableFuture<LeaseRevokeResponse> revoke(long leaseId) {
        CompletableFuture<LeaseRevokeResponse> completableFuture = new CompletableFuture<>();
        memStore.revoke(leaseId);
        LeaseRevokeResponse pr = new LeaseRevokeResponse(io.etcd.jetcd.api.LeaseRevokeResponse.newBuilder().build());
        completableFuture.complete(pr);
        return completableFuture;
    }

}
