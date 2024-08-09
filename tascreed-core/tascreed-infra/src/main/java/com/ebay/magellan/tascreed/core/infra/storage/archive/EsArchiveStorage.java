package com.ebay.magellan.tascreed.core.infra.storage.archive;

import com.ebay.magellan.tascreed.core.domain.task.TaskViews;
import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.job.JobInstKey;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.domain.task.TaskInstKey;
import com.ebay.magellan.tascreed.core.domain.util.JsonUtil;
import com.ebay.magellan.tascreed.core.infra.constant.TcKeys;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.ext.es.doc.*;
import com.ebay.magellan.tascreed.depend.ext.es.util.EsUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Component
public class EsArchiveStorage implements ArchiveStorage {

    private static final String THIS_CLASS_NAME = EsArchiveStorage.class.getSimpleName();

    @Autowired
    private TcKeys tcKeys;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EsUtil esUtil;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TcLogger logger;

    // -----

    public String name() {
        return ArchiveStorageType.ES.getName();
    }

    // -----

    private DocValue findJobFromEs(String name, String trigger)
            throws TcException, UnsupportedEncodingException {
        String jobKey = tcKeys.getJobKey(name, trigger);
        DocKey docKey = new DocKey(DocType.JOB, tcKeys.getTcConstants().getTcNamespace(), jobKey);
        return esUtil.getDocValue(docKey);
    }

    // -----

    private void updateJobToEs(String key, String value,
                               String jobName, String trigger, String state)
            throws TcException, UnsupportedEncodingException {
        DocKey docKey = new DocKey(DocType.JOB, tcKeys.getTcConstants().getTcNamespace(), key);
        DocValue docValue = new DocValue(value, null);
        docValue.addAttr("jobName", jobName);
        docValue.addAttr("trigger", trigger);
        docValue.addAttr("state", state);

        esUtil.syncPutDoc(docKey, docValue);
        logger.info(THIS_CLASS_NAME, String.format("archive job %s on es archive storage successfully", key));
    }

    // -----

    private String buildTaskEsKey(String jobName, String trigger, String taskName) {
        return String.format("%s.%s.%s", jobName, trigger, taskName);
    }

    private boolean updateTasksToEs(List<Task> tasks) {
        if (CollectionUtils.isEmpty(tasks)) return false;
        Map<DocKey, DocValue> kvs = new HashMap<>();
        for (Task task : tasks) {
            if (task == null) continue;
            try {
                String key = buildTaskEsKey(task.getJobName(), task.getTrigger(), task.getTaskName());
                String value = task.toJson(TaskViews.TASK_DONE.class);

                DocKey docKey = new DocKey(DocType.TASK, tcKeys.getTcConstants().getTcNamespace(), key);
                DocValue docValue = new DocValue(value, null);
                docValue.addAttr("jobName", task.getJobName());
                docValue.addAttr("trigger", task.getTrigger());
                docValue.addAttr("taskName", task.getTaskName());
                docValue.addAttr("state", task.getTaskState().name());

                kvs.put(docKey, docValue);
            } catch (JsonProcessingException e) {
                logger.error(THIS_CLASS_NAME, String.format("failed to serialize task to json: %s", e.getMessage()));
            }
        }

        try {
            esUtil.asyncPutDocs(kvs, 10);
        } catch (Exception e) {
            logger.error(THIS_CLASS_NAME, String.format("failed to async put docs: %s", e.getMessage()));
        }
        return true;
    }

    private DocValue findTaskFromEs(String jobName, String trigger, String taskName)
            throws TcException, UnsupportedEncodingException {
        String taskKey = buildTaskEsKey(jobName, trigger, taskName);
        DocKey docKey = new DocKey(DocType.TASK, tcKeys.getTcConstants().getTcNamespace(), taskKey);
        return esUtil.getDocValue(docKey);
    }

    // -----

    public Job findJob(JobInstKey key) {
        if (key == null) return null;
        try {
            DocValue jd = findJobFromEs(key.getName(), key.getTrigger());
            if (jd != null) {
                return JsonUtil.parseJob(jd.getValue());
            } else return null;
        } catch (Exception e) {
            // throw runtime exception to stop this job creation
            throw new RuntimeException(e);
        }
    }

    public void archiveJob(String key, String value, String jobName, String trigger, String state) throws Exception {
        updateJobToEs(key, value, jobName, trigger, state);
    }

    // -----

    public Task findTask(TaskInstKey key) {
        if (key == null) return null;
        try {
            DocValue td = findTaskFromEs(key.getJobName(), key.getTrigger(), key.getTaskName());
            if (td != null) {
                return JsonUtil.parseTask(td.getValue());
            } else return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean archiveTasks(List<Task> tasks) {
        return updateTasksToEs(tasks);
    }

}
