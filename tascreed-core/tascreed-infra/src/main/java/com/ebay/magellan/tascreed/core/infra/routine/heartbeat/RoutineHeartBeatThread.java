package com.ebay.magellan.tascreed.core.infra.routine.heartbeat;

import com.ebay.magellan.tascreed.core.domain.occupy.OccupyInfo;
import com.ebay.magellan.tascreed.core.infra.constant.TcConstants;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.RoutineBulletin;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.common.retry.RetryBackoffStrategy;
import com.ebay.magellan.tascreed.depend.common.retry.RetryCounter;
import com.ebay.magellan.tascreed.depend.common.retry.RetryCounterFactory;
import com.ebay.magellan.tascreed.depend.common.retry.RetryStrategy;
import com.ebay.magellan.tascreed.depend.common.util.ExceptionUtil;
import com.ebay.magellan.tascreed.depend.ext.etcd.constant.EtcdConstants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Scope("prototype")
public class RoutineHeartBeatThread implements Runnable {
    private static final String THIS_CLASS_NAME = RoutineHeartBeatThread.class.getSimpleName();

    private RetryStrategy retryStrategyForHeartBeat = RetryBackoffStrategy.newDefaultInstance();

    @Autowired
    private EtcdConstants etcdConstants;
    @Autowired
    private TcConstants tcConstants;

    @Autowired
    private RoutineBulletin routineBulletin;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TcLogger logger;

    private volatile OccupyInfo occupyInfo;

    // active state, only set by routine thread
    private volatile boolean active = true;

    @Override
    public void run() {
        long sleepMilliseconds = getSleepMilliseconds();
        RetryCounter retryCounter = RetryCounterFactory.buildRetryCounter(retryStrategyForHeartBeat);
        while (isActive()) {
            try {
                heartBeat();        // heart beat
                Thread.sleep(sleepMilliseconds);
                retryCounter.reset();
            } catch (TcException e) {
                if (e.isRetry()) {     // retry-able
                    retryCounter.grow();
                    String head = String.format("heart beat fails by retryable exception:\n%s", ExceptionUtil.getStackTrace(e));
                    logger.warn(THIS_CLASS_NAME, retryCounter.status(head));
                } else {        // nont-retry-able
                    retryCounter.forceStop();
                    String head = String.format("heart beat fails by non-retryable exception:\n%s", ExceptionUtil.getStackTrace(e));
                    logger.error(THIS_CLASS_NAME, retryCounter.status(head));
                }
            } catch (InterruptedException e) {
                logger.error(THIS_CLASS_NAME, "heart beat thread interrupted");
            } catch (Exception e) {        //NOSONAR
                // unknown, retry for certain times
                retryCounter.grow();
                String head = String.format("heart beat fails by unknown exception:\n%s", ExceptionUtil.getStackTrace(e));
                logger.error(THIS_CLASS_NAME, retryCounter.status(head));
            }

            if (!retryCounter.isAlive()) {
                break;
            }
            retryCounter.waitForNextRetry();
        }

        endHeartBeatThread();
    }

    boolean needHeartBeat() {
        return occupyInfo != null && !occupyInfo.isFinished();
    }

    long getHeartbeatMillisecondsFromTcConstants() {
        return tcConstants.getOccupyRoutineHeartbeatPeriodInSeconds() > 0 ?
                1000L * tcConstants.getOccupyRoutineHeartbeatPeriodInSeconds() :
                1000L * tcConstants.getOccupyRoutineLeaseInSeconds() / 3;
    }
    long getHeartbeatMillisecondsFromEtcdConstants() {
        return etcdConstants.getHeartbeatPeriodSeconds() > 0 ?
                1000L * etcdConstants.getHeartbeatPeriodSeconds() :
                1000L * etcdConstants.getOccupyLeaseSeconds() / 3;
    }
    long getSleepMilliseconds() {
        long ms = getHeartbeatMillisecondsFromTcConstants();
        if (ms <= 0) {
            ms = getHeartbeatMillisecondsFromEtcdConstants();
        }
        return ms;
    }

    // routineThread may occupy lease at the same time, need synchronized
    public synchronized void heartBeat() throws TcException {
        if (!needHeartBeat()) {
            String key = occupyInfo != null ? occupyInfo.getOccupyKey() : "null";
            logger.info(THIS_CLASS_NAME, String.format("%s no need to heart beat, just ignore ...", key));
            return;
        }

        String key = occupyInfo.getOccupyKey();
        // no need to retry heart beat, just throw retry-able exception
        long leaseId = routineBulletin.heartBeat(occupyInfo);

        occupyInfo.setAlive(leaseId >= 0);        // update pack alive state

        if (leaseId >= 0) {
            occupyInfo.setOccupyLeaseId(leaseId);
            logger.info(THIS_CLASS_NAME, String.format("%s heart beat, with leaseId: %d", key, leaseId));
        } else {
            String msg = String.format("heart beat fails: %s heart beat leaseId < 0, leaseId is %d", key, leaseId);
            logger.error(THIS_CLASS_NAME, msg);
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_NON_RETRY_HEARTBEAT_EXCEPTION, msg);
        }
    }

    public synchronized void setOccupiedInfo(OccupyInfo occupyInfo) {
        this.occupyInfo = occupyInfo;
    }

    synchronized void endHeartBeatThread() {
        inactiveCurrentHeartBeat();
        setActive(false);
    }

    // -----

    // inactive heartbeat for the current occupied routine, will just ignore heartbeat
    public synchronized void inactiveCurrentHeartBeat() {
        if (occupyInfo != null) {
            occupyInfo.setAlive(false);
        }
        occupyInfo = null;
    }

    // inactive the heartbeat thread, will gracefully end thread
    public void inactiveHeartBeat() {
        setActive(false);
    }

}
