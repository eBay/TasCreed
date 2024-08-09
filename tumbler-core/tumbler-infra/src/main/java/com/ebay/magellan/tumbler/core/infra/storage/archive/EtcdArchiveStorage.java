package com.ebay.magellan.tumbler.core.infra.storage.archive;

import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.options.PutOption;
import com.ebay.magellan.tumbler.core.domain.job.Job;
import com.ebay.magellan.tumbler.core.domain.job.JobInstKey;
import com.ebay.magellan.tumbler.core.domain.task.Task;
import com.ebay.magellan.tumbler.core.domain.task.TaskInstKey;
import com.ebay.magellan.tumbler.core.domain.util.JsonUtil;
import com.ebay.magellan.tumbler.core.infra.constant.TumblerKeys;
import com.ebay.magellan.tumbler.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tumbler.depend.ext.etcd.constant.EtcdConstants;
import com.ebay.magellan.tumbler.depend.ext.etcd.util.EtcdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class EtcdArchiveStorage implements ArchiveStorage {

    private static final String THIS_CLASS_NAME = EtcdArchiveStorage.class.getSimpleName();

    private static String ARCHIVE_ETCD_JOB_PREFIX = "%s/archive/job/";
    private static String ARCHIVE_ETCD_TASK_PREFIX = "%s/archive/task/";

    // retention hours, default 7 days; if negative, means keep forever
    @Value("${tumbler.storage.archive.etcd.retention.hours:168}")
    private int retentionHours;

    @Autowired
    private EtcdConstants etcdConstants;

    @Autowired
    private TumblerKeys tumblerKeys;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EtcdUtil etcdUtil;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    // -----

    public String name() {
        return ArchiveStorageType.ETCD.getName();
    }

    // -----

    private String archiveJobPrefix() {
        return tumblerKeys.withNamespace(ARCHIVE_ETCD_JOB_PREFIX);
    }
    private String archiveTaskPrefix() {
        return tumblerKeys.withNamespace(ARCHIVE_ETCD_TASK_PREFIX);
    }

    private String buildArchiveJobKey(String jobName, String trigger) {
        return tumblerKeys.buildJobKey(archiveJobPrefix(), jobName, trigger);
    }
    private String buildArchiveTaskKey(String jobName, String trigger, String taskName) {
        return tumblerKeys.buildTaskKey(archiveTaskPrefix(), jobName, trigger, taskName);
    }

    // -----

    public Job findJob(JobInstKey key) {
        if (key == null) return null;
        String str = etcdUtil.getSingleValue(buildArchiveJobKey(key.getName(), key.getTrigger()));
        Job job = JsonUtil.parseJob(str);
        if (job == null) {
            logger.info(THIS_CLASS_NAME, String.format("not found job %s on etcd archive storage", key));
        }
        return job;
    }

    public void archiveJob(String key, String value, String jobName, String trigger, String state) throws Exception {
        if (retentionHours == 0) return;        // no need to archive

        String jobKey = buildArchiveJobKey(jobName, trigger);

        PutOption putOption = PutOption.DEFAULT;    // archive forever
        if (retentionHours > 0) {       // archive for retention time
            long leaseId = etcdUtil.grantLease(hour2s(retentionHours));
            putOption = PutOption.newBuilder().withLeaseId(leaseId).build();
        }

        PutResponse putResponse = etcdUtil.put(jobKey, value, putOption)
                .get(etcdConstants.getEtcdTimeoutInSeconds(), TimeUnit.SECONDS);
        if (putResponse != null) {
            logger.info(THIS_CLASS_NAME, String.format("archive job %s on etcd archive storage successfully", key));
        }
    }

    // -----

    public Task findTask(TaskInstKey key) {
        logger.info(THIS_CLASS_NAME, String.format("not found task %s on etcd archive storage", key));
        return null;
    }

    public boolean archiveTasks(List<Task> tasks) {
        logger.info(THIS_CLASS_NAME, "will not archive tasks to etcd archive storage");
        return true;
    }

    // -----

    private long hour2s(long hour) {
        return hour * 60 * 60L;
    }

}
