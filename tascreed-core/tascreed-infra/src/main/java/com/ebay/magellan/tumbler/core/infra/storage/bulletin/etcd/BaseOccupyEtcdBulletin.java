package com.ebay.magellan.tumbler.core.infra.storage.bulletin.etcd;

import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.PutOption;
import com.ebay.magellan.tumbler.core.domain.occupy.OccupyInfo;
import com.ebay.magellan.tumbler.core.infra.constant.TumblerKeys;
import com.ebay.magellan.tumbler.core.infra.storage.bulletin.BaseOccupyBulletin;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerErrorEnum;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerExceptionBuilder;
import com.ebay.magellan.tumbler.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tumbler.depend.ext.etcd.constant.EtcdConstants;
import com.ebay.magellan.tumbler.depend.ext.etcd.util.EtcdUtil;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class BaseOccupyEtcdBulletin extends BaseEtcdBulletin implements BaseOccupyBulletin {

    private static final String THIS_CLASS_NAME = BaseOccupyEtcdBulletin.class.getSimpleName();

    public BaseOccupyEtcdBulletin(TumblerKeys tumblerKeys,
                                  EtcdConstants etcdConstants,
                                  EtcdUtil etcdUtil,
                                  TumblerLogger logger) {
        super(tumblerKeys, etcdConstants, etcdUtil, logger);
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

    public long heartBeat(OccupyInfo occupyInfo) throws TumblerException {
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
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_RETRY_EXCEPTION, "heart beat exception", e);
        }
        return -1L;
    }

    // -----
    
}
