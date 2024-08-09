package com.ebay.magellan.tascreed.core.domain.builder;

import com.ebay.magellan.tascreed.core.domain.define.JobDefine;
import com.ebay.magellan.tascreed.core.domain.define.StepDefine;
import com.ebay.magellan.tascreed.core.domain.define.conf.StepAllConf;
import com.ebay.magellan.tascreed.core.domain.define.conf.StepPackConf;
import com.ebay.magellan.tascreed.core.domain.define.conf.StepShardConf;
import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.job.JobStep;
import com.ebay.magellan.tascreed.core.domain.job.mid.JobMidState;
import com.ebay.magellan.tascreed.core.domain.job.mid.StepMidState;
import com.ebay.magellan.tascreed.core.domain.request.JobRequest;
import com.ebay.magellan.tascreed.core.domain.request.StepRequest;
import com.ebay.magellan.tascreed.depend.common.util.DefaultValueUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class JobBuilder {

    public Job buildJob(JobDefine jobDefine, JobRequest jobRequest) {
        if (jobRequest == null || jobDefine == null) return null;
        Job job = new Job(jobDefine);

        job.setJobName(jobRequest.getJobName());
        job.setTrigger(jobRequest.getTrigger());

        job.setPriority(jobDefine.getPriority());
        if (jobRequest.getPriority() != null) {
            job.setPriority(jobRequest.getPriority());
        }

        // assemble params by job define and job request params
        assembleMultiJobParams(job, jobDefine.getParams(), jobRequest.getParams());

        job.setSteps(buildSteps(jobDefine, jobRequest));

        // set traits
        job.getTraits().copyFromTraits(jobDefine.getTraits());
        job.getTraits().amendTraits(jobRequest.getTraitsAmend());

        // mid state
        assembleJobMidState(job.getMidState(), jobDefine, jobRequest);

        job.postBuild();

        return job;
    }

    // -----

    // assemble job params, the later will cover the former
    void assembleMultiJobParams(Job job, Map<String, String>... paramsArr) {
        for (Map<String, String> params : paramsArr) {
            assembleJobParams(job, params);
        }
    }
    void assembleJobParams(Job job, Map<String, String> params) {
        if (job == null || MapUtils.isEmpty(params)) return;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            job.addParam(entry.getKey(), entry.getValue());
        }
    }

    // assemble job mid state
    void assembleJobMidState(JobMidState midState, JobDefine jobDefine, JobRequest jobRequest) {
        if (midState == null) return;
        // define first
        if (jobDefine != null) {
            if (jobDefine.getDuration() != null) {
                midState.setDuration(jobDefine.getDuration() >= 0 ? jobDefine.getDuration() : null);
            }
        }
        // request overwrite
        if (jobRequest != null) {
            midState.setAfterTime(jobRequest.getAfterTime());
            if (jobRequest.getDuration() != null) {
                midState.setDuration(jobRequest.getDuration() >= 0 ? jobRequest.getDuration() : null);
            }
        }
        midState.setCreateTime(new Date());
    }

    // -----

    List<JobStep> buildSteps(JobDefine jobDefine, JobRequest jobRequest) {
        List<JobStep> steps = new ArrayList<>();
        if (jobRequest == null || jobDefine == null) return steps;

        for (StepDefine stepDefine : jobDefine.getSteps()) {
            StepRequest stepRequest = jobRequest.findStepRequestByName(stepDefine.name());
            JobStep step = buildStep(stepDefine, stepRequest);
            if (step != null) {
                steps.add(step);
            }
        }

        return steps;
    }

    JobStep buildStep(StepDefine stepDefine, StepRequest stepRequest) {
        if (stepDefine == null) return null;
        JobStep step = new JobStep(stepDefine);

        step.setStepName(stepDefine.name());

        // set exe class
        step.setExeClass(stepDefine.getExeClass());
        if (stepRequest != null && StringUtils.isNotBlank(stepRequest.getExeClass())) {
            step.setExeClass(stepRequest.getExeClass());
        }

        // ignore step only if it is ignorable and request step ignore
        boolean ignore = stepRequest != null ? stepRequest.isIgnore() : false;
        if (stepDefine.canIgnore() && ignore) {
            step.setIgnore(true);
        }

        // set affinity rule
        step.setAffinityRule(stepDefine.getAffinityRule());
        if (stepRequest != null && StringUtils.isNotBlank(stepRequest.getAffinityRule())) {
            step.setAffinityRule(stepRequest.getAffinityRule());
        }

        // set traits
        step.getTraits().copyFromTraits(stepDefine.getTraits());
        if (stepRequest != null) {
            step.getTraits().amendTraits(stepRequest.getTraitsAmend());
        }
//        step.setArchive(stepDefine.getArchive());
//        if (stepRequest != null && stepRequest.getArchive() != null) {
//            step.setArchive(stepRequest.getArchive());
//        }

        // assemble step conf
        assembleStepConf(step, stepRequest);

        // assemble params by step define and step request params
        assembleMultiStepParams(step, stepDefine.getParams(),
                stepRequest != null ? stepRequest.getParams() : null);

        // mid state
        assembleStepMidState(step.getMidState(), stepDefine, stepRequest);

        return step;
    }

    // assemble step conf
    private void assembleStepConf(JobStep step, StepRequest stepRequest) {
        if (step == null || step.getStepDefine() == null) return;
        StepDefine stepDefine = step.getStepDefine();
        StepAllConf allConf = step.getStepAllConf();

        // different step mode
        if (stepDefine.isShard()) {
            StepShardConf rsc = stepRequest != null ?
                    stepRequest.getStepAllConf().getShardConf() : null;
            allConf.setShardConf(
                    StepShardConf.merge(stepDefine.getStepAllConf().getShardConf(), rsc));
        } else if (stepDefine.isPack()) {
            StepPackConf rsc = stepRequest != null ?
                    stepRequest.getStepAllConf().getPackConf() : null;
            allConf.setPackConf(
                    StepPackConf.merge(stepDefine.getStepAllConf().getPackConf(), rsc));
        }

        // max pick times
        allConf.setMaxPickTimes(DefaultValueUtil.defValue(
                stepRequest != null ? stepRequest.getStepAllConf().getMaxPickTimes() : null,
                stepDefine.getStepAllConf().getMaxPickTimes()));
    }

    // assemble step params, the later will cover the former
    private void assembleMultiStepParams(JobStep step, Map<String, String>... paramsArr) {
        for (Map<String, String> params : paramsArr) {
            assembleStepParams(step, params);
        }
    }
    private void assembleStepParams(JobStep step, Map<String, String> params) {
        if (step == null || MapUtils.isEmpty(params)) return;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            step.addParam(entry.getKey(), entry.getValue());
        }
    }

    // assemble step mid state
    void assembleStepMidState(StepMidState midState, StepDefine stepDefine, StepRequest stepRequest) {
        if (midState == null) return;
        // define first
        if (stepDefine != null) {
            if (stepDefine.getDuration() != null) {
                midState.setDuration(stepDefine.getDuration() >= 0 ? stepDefine.getDuration() : null);
            }
        }
        // request overwrite
        if (stepRequest != null) {
            midState.setAfterTime(stepRequest.getAfterTime());
            if (stepRequest.getDuration() != null) {
                midState.setDuration(stepRequest.getDuration() >= 0 ? stepRequest.getDuration() : null);
            }
        }
    }

    // -----

    // assemble job with job define
    public void assembleJob(Job job, JobDefine jobDefine) {
        if (job == null || jobDefine == null) return;
        job.setJobDefine(jobDefine);
        assembleSteps(job.getSteps(), jobDefine);
        job.postBuild();
    }

    // assemble steps with step defines
    public void assembleSteps(List<JobStep> steps, JobDefine jobDefine) {
        if (CollectionUtils.isEmpty(steps) || jobDefine == null) return;
        for (JobStep step : steps) {
            step.setStepDefine(jobDefine.findStepDefineByName(step.getStepName()));
        }
    }
}
