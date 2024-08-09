package com.ebay.magellan.tascreed.core.domain.builder;

import com.ebay.magellan.tascreed.core.domain.define.conf.StepAllConf;
import com.ebay.magellan.tascreed.core.domain.define.conf.StepConf;
import com.ebay.magellan.tascreed.core.domain.define.conf.StepPackConf;
import com.ebay.magellan.tascreed.core.domain.define.conf.StepShardConf;
import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.job.JobStep;
import com.ebay.magellan.tascreed.core.domain.request.JobRequest;
import com.ebay.magellan.tascreed.core.domain.request.StepRequest;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Date;

public class JobUpdateBuilder {

    // -----

    boolean jobCanUpdate(Job job) {
        if (job == null) return false;
        return job.getState().canCreateTask();
    }

    boolean stepCanUpdate(JobStep step) {
        if (step == null) return false;
        return step.getState().canCreateTask();
    }

    // -----

    public boolean updateJob(Job job, JobRequest jobRequest) {
        if (jobRequest == null) return false;
        if (!jobCanUpdate(job)) return false;
        boolean updated = false;

        if (updateSteps(job, jobRequest)) {
            updated = true;
        }

        if (updated) {
            job.getMidState().setModifyTime(new Date());
        }
        return updated;
    }

    boolean updateSteps(Job job, JobRequest jobRequest) {
        if (CollectionUtils.isEmpty(job.getSteps())) return false;
        boolean updated = false;

        for (JobStep step : job.getSteps()) {
            if (updateStep(step, jobRequest)) {
                updated = true;
            }
        }

        return updated;
    }

    boolean updateStep(JobStep step, JobRequest jobRequest) {
        if (!stepCanUpdate(step)) return false;
        StepRequest stepRequest = jobRequest.findStepRequestByName(step.getStepName());
        if (stepRequest == null) return false;
        boolean updated = false;

        if (updateStepConf(step.getStepAllConf(), stepRequest.getStepAllConf())) {
            updated = true;
        }

        if (updated) {
            step.getMidState().setModifyTime(new Date());
        }

        return updated;
    }

    // -----

    boolean updateStepConf(StepAllConf stepConf, StepAllConf requestConf) {
        if (stepConf == null || requestConf == null) return false;
        boolean updated = false;

        if (updatePackConf(stepConf.getPackConf(), requestConf.getPackConf())) {
            updated = true;
        }

        if (updateShardConf(stepConf.getShardConf(), requestConf.getShardConf())) {
            updated = true;
        }

        // update max pick times
        Integer udtMaxPickTimes = requestConf.getMaxPickTimes();
        if (udtMaxPickTimes != null && udtMaxPickTimes != stepConf.getMaxPickTimes()) {
            stepConf.setMaxPickTimes(udtMaxPickTimes);
            updated = true;
        }

        return updated;
    }

    private boolean updatePackConf(StepPackConf stepPackConf, StepPackConf requestStepPackConf) {
        if (stepPackConf == null || requestStepPackConf == null) return false;
        boolean updated = false;

        if (updateStepConf(stepPackConf, requestStepPackConf)) {
            updated = true;
        }

        // update pack conf size
        Long udtPackSize = requestStepPackConf.getSize();
        if (udtPackSize != null && udtPackSize != stepPackConf.getSize()) {
            stepPackConf.setSize(udtPackSize);
            updated = true;
        }

        return updated;
    }

    private boolean updateShardConf(StepShardConf stepShardConf, StepShardConf requestStepShardConf) {
        if (stepShardConf == null || requestStepShardConf == null) return false;
        boolean updated = false;

        if (updateStepConf(stepShardConf, requestStepShardConf)) {
            updated = true;
        }

        return updated;
    }

    private boolean updateStepConf(StepConf stepConf, StepConf requestStepConf) {
        if (stepConf == null || requestStepConf == null) return false;
        boolean updated = false;

        // update max task count
        Integer udtMaxTaskCount = requestStepConf.getMaxTaskCount();
        if (udtMaxTaskCount != null && udtMaxTaskCount != stepConf.getMaxTaskCount()) {
            stepConf.setMaxTaskCount(udtMaxTaskCount);
            updated = true;
        }

        return updated;
    }

    // -----

}
