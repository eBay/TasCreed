package com.ebay.magellan.tascreed.core.infra.storage.bulletin.etcd;

import io.etcd.jetcd.Txn;
import io.etcd.jetcd.kv.TxnResponse;
import io.etcd.jetcd.op.Cmp;
import io.etcd.jetcd.op.CmpTarget;
import io.etcd.jetcd.op.Op;
import io.etcd.jetcd.options.DeleteOption;
import io.etcd.jetcd.options.PutOption;
import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.domain.task.TaskViews;
import com.ebay.magellan.tascreed.core.infra.constant.TcKeys;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.JobBulletin;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.ext.etcd.constant.EtcdConstants;
import com.ebay.magellan.tascreed.depend.ext.etcd.util.EtcdUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JobEtcdBulletin extends BaseEtcdBulletin implements JobBulletin {

    private static final String THIS_CLASS_NAME = JobEtcdBulletin.class.getSimpleName();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public JobEtcdBulletin(TcKeys tcKeys,
                           EtcdConstants etcdConstants,
                           EtcdUtil etcdUtil,
                           TcLogger logger) {
        super(tcKeys, etcdConstants, etcdUtil, logger);
    }

    // -----

    /**
     * @param job          need to update
     * @param newTasks     need to put
     * @param oldDoneTasks need to delete
     * @param oldErrorTasks need to delete
     * @return submit success or not
     * @throws Exception if there's any exception
     */
    public boolean submitJobAndTasks(Job job,
                                     List<Task> newTasks,
                                     List<Task> oldDoneTasks,
                                     List<Task> oldErrorTasks) throws Exception {
        if (job == null) return false;

        String jobKey = tcKeys.getJobKey(job.getJobName(), job.getTrigger());

        Txn txn = etcdUtil.txn();
        Cmp cmp;

        // job is new submitted or not
        if (StringUtils.isBlank(job.getFromValue())) {
            cmp = new Cmp(bs(jobKey), Cmp.Op.EQUAL, CmpTarget.createRevision(0));
        } else {
            cmp = new Cmp(bs(jobKey), Cmp.Op.EQUAL, CmpTarget.value(bs(job.getFromValue())));
        }
        txn.If(cmp);

        // job
        String jobValue = job.toJson();
        Op jobPutOp = Op.put(bs(jobKey), bs(jobValue), PutOption.DEFAULT);
        txn.Then(jobPutOp);

        // new tasks
        if (CollectionUtils.isNotEmpty(newTasks)) {
            for (Task task : newTasks) {
                String taskKey = tcKeys.getTodoTaskKey(task.getJobName(), task.getTrigger(), task.getTaskName());
                String taskValue = task.toJson(TaskViews.TASK_TODO.class);
                Op taskPutOp = Op.put(bs(taskKey), bs(taskValue), PutOption.DEFAULT);
                txn.Then(taskPutOp);
            }
        }

        // old done tasks
        if (CollectionUtils.isNotEmpty(oldDoneTasks)) {
            for (Task task : oldDoneTasks) {
                String taskKey = tcKeys.getDoneTaskKey(task.getJobName(), task.getTrigger(), task.getTaskName());
                Op taskDelOp = Op.delete(bs(taskKey), DeleteOption.DEFAULT);
                txn.Then(taskDelOp);
            }
        }

        // old error tasks
        if (CollectionUtils.isNotEmpty(oldErrorTasks)) {
            for (Task task : oldErrorTasks) {
                String taskKey = tcKeys.getErrorTaskKey(task.getJobName(), task.getTrigger(), task.getTaskName());
                Op taskDelOp = Op.delete(bs(taskKey), DeleteOption.DEFAULT);
                txn.Then(taskDelOp);
            }
        }

        TxnResponse txnResponse = txn.commit()
                .get(etcdConstants.getEtcdTimeoutInSeconds(), TimeUnit.SECONDS);

        boolean success = CollectionUtils.isNotEmpty(txnResponse.getPutResponses());

        // update job from value
        if (success) {
            job.setFromValue(jobValue);
        }

        return success;
    }

    // -----

    public String readJob(String name, String trigger) {
        return etcdUtil.getSingleValue(tcKeys.getJobKey(name, trigger));
    }
    public Map<String, String> readJobsByName(String jobName) throws Exception {
        return etcdUtil.getKVMapWithPrefix(tcKeys.getJobPrefixKey(jobName));
    }

    public Map<String, String> readAllJobs() throws Exception {
        return etcdUtil.getKVMapWithPrefix(tcKeys.buildJobInfoPrefix());
    }

    public Map<String, String> readDoneTasksOfJob(String jobName, String trigger) throws Exception {
        return etcdUtil.getKVMapWithPrefix(tcKeys.getDoneTaskOfJobPrefixKey(jobName, trigger));
    }
    public Map<String, String> readErrorTasksOfJob(String jobName, String trigger) throws Exception {
        return etcdUtil.getKVMapWithPrefix(tcKeys.getErrorTaskOfJobPrefixKey(jobName, trigger));
    }

    // -----


}
