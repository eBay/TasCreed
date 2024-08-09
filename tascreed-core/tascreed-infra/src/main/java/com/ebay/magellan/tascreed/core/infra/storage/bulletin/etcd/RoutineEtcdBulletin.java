package com.ebay.magellan.tascreed.core.infra.storage.bulletin.etcd;

import io.etcd.jetcd.Txn;
import io.etcd.jetcd.kv.TxnResponse;
import io.etcd.jetcd.op.Cmp;
import io.etcd.jetcd.op.CmpTarget;
import io.etcd.jetcd.op.Op;
import io.etcd.jetcd.options.PutOption;
import com.ebay.magellan.tascreed.core.domain.routine.Routine;
import com.ebay.magellan.tascreed.core.infra.constant.TumblerKeys;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.RoutineBulletin;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tascreed.depend.ext.etcd.constant.EtcdConstants;
import com.ebay.magellan.tascreed.depend.ext.etcd.lock.EtcdLock;
import com.ebay.magellan.tascreed.depend.ext.etcd.util.EtcdUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RoutineEtcdBulletin extends BaseOccupyEtcdBulletin implements RoutineBulletin {

    private static final String THIS_CLASS_NAME = RoutineEtcdBulletin.class.getSimpleName();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RoutineEtcdBulletin(TumblerKeys tumblerKeys,
                               EtcdConstants etcdConstants,
                               EtcdUtil etcdUtil,
                               TumblerLogger logger) {
        super(tumblerKeys, etcdConstants, etcdUtil, logger);
    }

    // -----

    public String getRoutineAdoptionKey(Routine routine) {
        if (routine == null) return null;
        return tumblerKeys.getRoutineAdoptionKey(routine.getFullName());
    }

    public String checkRoutineAdoption(Routine routine) throws TumblerException {
        try {
            String key = getRoutineAdoptionKey(routine);
            return etcdUtil.getSingleValue(key);
        } catch (Exception e) {
            TumblerExceptionBuilder.throwEtcdRetryableException(e);
        }
        return null;
    }

    public Map<String, String> readAllRoutineAdoptions() throws Exception {
        return etcdUtil.getKVMapWithPrefix(tumblerKeys.buildRoutineAdoptionPrefix());
    }

    // -----

    public String readRoutineCheckpoint(Routine routine) throws TumblerException {
        if (routine == null) return null;
        try {
            String checkpointKey = tumblerKeys.getRoutineCheckpointKey(routine.getFullName());
            return etcdUtil.getSingleValue(checkpointKey);
        } catch (Exception e) {
            TumblerExceptionBuilder.throwEtcdRetryableException(e);
        }
        return null;
    }

    public boolean updateRoutineCheckpoint(Routine routine, String adoptionValue) throws TumblerException {
        if (routine == null) return false;

        String value = routine.getCheckpointValue();
        if (StringUtils.isBlank(value)) return false;

        boolean success = false;

        String routineUpdateLock = tumblerKeys.getRoutineUpdateLockKey(routine.getFullName());
        EtcdLock lock = null;

        try {
            lock = etcdUtil.lock(routineUpdateLock);
            success = updateRoutineCheckpointImpl(routine, value, adoptionValue);
        } catch (TumblerException e) {
            throw e;
        } catch (Exception e) {
            TumblerExceptionBuilder.throwEtcdRetryableException(e);
        } finally {
            try {
                etcdUtil.unlock(lock);
            } catch (Exception e) {
                TumblerExceptionBuilder.throwEtcdRetryableException(e);
            }
        }

        return success;
    }

    boolean updateRoutineCheckpointImpl(Routine routine, String checkpointValue, String adoptionValue) throws Exception {
        if (routine == null) return false;

        String checkpointKey = tumblerKeys.getRoutineCheckpointKey(routine.getFullName());
        String adoptionKey = getRoutineAdoptionKey(routine);

        Txn txn = etcdUtil.txn();

        // compare adoption
        if (StringUtils.isNotBlank(adoptionValue)) {
            Cmp cmp = new Cmp(bs(adoptionKey), Cmp.Op.EQUAL, CmpTarget.value(bs(adoptionValue)));
            txn.If(cmp);
        }

        // put routine checkpoint
        Op putOp = Op.put(bs(checkpointKey), bs(checkpointValue), PutOption.DEFAULT);
        txn.Then(putOp);

        TxnResponse txnResponse = txn.commit()
                .get(etcdConstants.getEtcdTimeoutInSeconds(), TimeUnit.SECONDS);
        boolean success = CollectionUtils.isNotEmpty(txnResponse.getPutResponses());

        return success;
    }
}
