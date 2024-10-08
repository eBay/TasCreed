package com.ebay.magellan.tascreed.core.infra.storage.bulletin.etcd;

import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.PutOption;
import com.ebay.magellan.tascreed.core.domain.occupy.OccupyInfo;
import com.ebay.magellan.tascreed.core.infra.constant.TcKeys;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.BaseOccupyBulletin;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.ext.etcd.constant.EtcdConstants;
import com.ebay.magellan.tascreed.depend.ext.etcd.util.EtcdUtil;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class BaseOccupyEtcdBulletin extends BaseEtcdBulletin implements BaseOccupyBulletin {

    private static final String THIS_CLASS_NAME = BaseOccupyEtcdBulletin.class.getSimpleName();

    public BaseOccupyEtcdBulletin(TcKeys tcKeys,
                                  EtcdConstants etcdConstants,
                                  EtcdUtil etcdUtil,
                                  TcLogger logger) {
        super(tcKeys, etcdConstants, etcdUtil, logger);
    }

    // -----

    public OccupyInfo occupy(String adoptionKey, String adoptionValue) throws Exception {
        // 1. get lease to occupy
        long occupyLeaseId = etcdUtil.grantLease(etcdConstants.getOccupyLeaseSeconds());

        // 2. occupy key -> value with occupyLease
        etcdUtil.putKeyValue(adoptionKey, adoptionValue,
                PutOption.newBuilder().withLeaseId(occupyLeaseId).build());

        // 3. return occupy info
        return new OccupyInfo(occupyLeaseId, adoptionKey, adoptionValue);
    }

    // -----

    public boolean deleteAdoption(OccupyInfo occupyInfo) throws Exception {
        if (occupyInfo == null) return false;

        String adoptionKey = occupyInfo.getOccupyKey();
        String adoptionValue = occupyInfo.getOccupyValue();

        boolean success = deleteIfEquals(adoptionKey, adoptionValue);

        if (success) {
            // revoke lease
            try {
                etcdUtil.revoke(occupyInfo.getOccupyLeaseId()).get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error(THIS_CLASS_NAME, String.format("error happen when revoke lease %d", occupyInfo.getOccupyLeaseId()));
            }
        }

        return success;
    }

    // -----

    public long heartBeat(OccupyInfo occupyInfo) throws TcException {
        try {
            String key = occupyInfo.getOccupyKey();
            String value = occupyInfo.getOccupyValue();
            String valueInEtcd = etcdUtil.getSingleValue(key);

            if (!StringUtils.equals(value, valueInEtcd)) {
                logger.warn(THIS_CLASS_NAME,
                        String.format("heart beat error, key %s, value %s is different with etcd value %s",
                                key, value, valueInEtcd));
                return -1L;
            }

            CompletableFuture<LeaseKeepAliveResponse> leaseResp =
                    etcdUtil.keepAliveOnce(occupyInfo.getOccupyLeaseId());

            return leaseResp.get().getID();
        } catch (Exception e) {
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_RETRY_EXCEPTION, "heart beat exception", e);
        }
        return -1L;
    }

    // -----
    
}
