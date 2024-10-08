package com.ebay.magellan.tascreed.core.infra.storage.bulletin.etcd;

import io.etcd.jetcd.Txn;
import io.etcd.jetcd.kv.TxnResponse;
import io.etcd.jetcd.op.Cmp;
import io.etcd.jetcd.op.CmpTarget;
import io.etcd.jetcd.op.Op;
import io.etcd.jetcd.options.PutOption;
import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.schedule.Schedule;
import com.ebay.magellan.tascreed.core.infra.constant.TcKeys;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.ScheduleBulletin;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.ext.etcd.constant.EtcdConstants;
import com.ebay.magellan.tascreed.depend.ext.etcd.util.EtcdUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduleEtcdBulletin extends BaseEtcdBulletin implements ScheduleBulletin {

    private static final String THIS_CLASS_NAME = ScheduleEtcdBulletin.class.getSimpleName();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ScheduleEtcdBulletin(TcKeys tcKeys,
                                EtcdConstants etcdConstants,
                                EtcdUtil etcdUtil,
                                TcLogger logger) {
        super(tcKeys, etcdConstants, etcdUtil, logger);
    }

    // -----

    /**
     * submit schedule together with new triggered jobs
     * @param schedule the schedule to be updated
     * @param newJobs the new jobs to be submitted if any
     * @return success or not
     * @throws Exception
     */
    public boolean submitScheduleAndJobs(Schedule schedule, List<Job> newJobs) throws Exception {
        if (schedule == null) return false;

        String scheduleKey = tcKeys.getScheduleKey(schedule.getScheduleName());

        Txn txn = etcdUtil.txn();
        Cmp cmp;

        // schedule is new submitted or not
        if (StringUtils.isBlank(schedule.getFromValue())) {
            cmp = new Cmp(bs(scheduleKey), Cmp.Op.EQUAL, CmpTarget.createRevision(0));
        } else {
            cmp = new Cmp(bs(scheduleKey), Cmp.Op.EQUAL, CmpTarget.value(bs(schedule.getFromValue())));
        }
        txn.If(cmp);

        // new jobs
        if (CollectionUtils.isNotEmpty(newJobs)) {
            List<Pair<Cmp, Op>> jobOpPairs = new ArrayList<>();
            for (Job job : newJobs) {
                String jobKey = tcKeys.getJobKey(job.getJobName(), job.getTrigger());
                String jobValue = job.toJson();
                Cmp jobCmp = new Cmp(bs(jobKey), Cmp.Op.EQUAL, CmpTarget.createRevision(0));
                Op jobPut = Op.put(bs(jobKey), bs(jobValue), PutOption.DEFAULT);
                jobOpPairs.add(new ImmutablePair<>(jobCmp, jobPut));
            }
            for (Pair<Cmp, Op> pair : jobOpPairs) {
                txn.If(pair.getLeft());
            }
            for (Pair<Cmp, Op> pair : jobOpPairs) {
                txn.Then(pair.getRight());
            }
        }

        // schedule
        String scheduleValue = schedule.toJson();
        Op schedulePutOp = Op.put(bs(scheduleKey), bs(scheduleValue), PutOption.DEFAULT);
        txn.Then(schedulePutOp);

        TxnResponse txnResponse = txn.commit()
                .get(etcdConstants.getEtcdTimeoutInSeconds(), TimeUnit.SECONDS);

        boolean success = CollectionUtils.isNotEmpty(txnResponse.getPutResponses());

        // update schedule from value
        if (success) {
            schedule.setFromValue(scheduleValue);
        }

        return success;
    }

    // -----

    public String readSchedule(String scheduleName) {
        return etcdUtil.getSingleValue(tcKeys.getScheduleKey(scheduleName));
    }
    public Map<String, String> readAllSchedules() throws Exception {
        return etcdUtil.getKVMapWithPrefix(tcKeys.buildScheduleInfoPrefix());
    }

    // -----

    public String deleteSchedule(String scheduleName) throws Exception{
        String k = tcKeys.getScheduleKey(scheduleName);
        String v = getSingleValue(k);
        if (StringUtils.isBlank(v)) return null;

        boolean deleted = deleteIfEquals(k, v);
        if (deleted) {
            return v;
        }
        return null;
    }

}
