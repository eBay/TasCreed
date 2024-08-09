package com.ebay.magellan.tascreed.core.infra.jobserver.help;

import com.ebay.magellan.tascreed.core.domain.define.conf.StepAllConf;
import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.job.JobStep;
import com.ebay.magellan.tascreed.core.domain.state.StateChange;
import com.ebay.magellan.tascreed.core.domain.state.StepStateEnum;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.infra.constant.TumblerKeys;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.JobBulletin;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tascreed.depend.common.util.DateUtil;
import com.ebay.magellan.tascreed.depend.ext.etcd.lock.EtcdLock;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JobHelper {

    private static final String THIS_CLASS_NAME = JobHelper.class.getSimpleName();

    @Autowired
    private TumblerKeys tumblerKeys;

    @Autowired
    private JobBulletin jobBulletin;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    public boolean submitJobWithTasks(Job job,
                                      List<Task> newTasks,
                                      List<Task> oldDoneTasks,
                                      List<Task> oldErrorTasks) throws TumblerException {
        boolean ret = false;
        if (job == null) return ret;

        job.getMidState().setModifyTime(new Date());

        String jobUpdateLock = tumblerKeys.getJobUpdateLockKey(job.getJobName(), job.getTrigger());
        EtcdLock lock = null;
        try {
            lock = jobBulletin.lock(jobUpdateLock);

            ret = jobBulletin.submitJobAndTasks(job, newTasks, oldDoneTasks, oldErrorTasks);

        } catch (JsonProcessingException e) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_VALIDATION_EXCEPTION, e.getMessage());
        } catch (Exception e) {
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_RETRY_EXCEPTION, e.getMessage());
        } finally {
            try {
                jobBulletin.unlock(lock);
            } catch (Exception e) {
                TumblerExceptionBuilder.throwTumblerException(
                        TumblerErrorEnum.TUMBLER_RETRY_EXCEPTION, e.getMessage());
            }
        }
        return ret;
    }

    // -----

    public StateChange updateJobStateByDoneTasks(Job job, List<Task> doneTasks, long curTime) {
        StateChange stateChange = StateChange.init();
        updateStatesInJob(stateChange, job, doneTasks, curTime);
        return stateChange;
    }

    /**
     * update job/step states and progressions
     * updated job params and configs
     */
    void updateStatesInJob(StateChange stateChange, Job job, List<Task> doneTasks, long curTime) {
        if (stateChange == null) return;
        if (job == null) return;

        if (CollectionUtils.isNotEmpty(doneTasks)) {
            // task
            for (Task doneTask : doneTasks) {
                if (doneTask.getResult() == null || doneTask.getResult().getState() == null) continue;
                // only success task can update param and config
                if (doneTask.getResult().getState().isSuccess()) {
                    // update job global param
                    assembleJobUpdatedParams(job, doneTask.getUpdatedParams());
                    // update job step configs
                    assembleJobStepUpdatedConfigs(job, doneTask.getUpdatedConfigs());
                }

                JobStep step = job.findStepByName(doneTask.getStepName());
                if (step != null) {
                    // update step state
                    boolean taskStateChanged = step.updateTaskState(
                            doneTask.getTaskAllConf(),
                            doneTask.getResult().getState());
                    stateChange.changeTaskState(taskStateChanged);
                    if (taskStateChanged && doneTask.getResult().getState().isSuccess()) {
                        // update step progression
                        step.updateStepAllDoneRange(doneTask.getTaskAllConf());
                    }
                }
            }

            // step
            for (JobStep step : job.getSteps()) {
                stateChange.changeStepState(step.updateStepStateByTaskStates());
                step.refreshTaskStates();
                if (stateChange.isTaskStateChanged()) {
                    step.updateProgression();
                }
            }
        }

        // loop to update all the step states until no change any more
        updateStepStatesInternal(stateChange, job, curTime);

        // job
        stateChange.changeJobState(job.updateJobStateByStepStates());
        if (stateChange.anyStateChanged()) {
            job.updateProgression();
        }
    }

    // update job global param
    void assembleJobUpdatedParams(Job job, Map<String, String> params) {
        if (job == null || MapUtils.isEmpty(params)) return;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            job.addUpdatedParam(key, value);
            logger.info(THIS_CLASS_NAME, String.format("update param %s with value: %s", key, value));
        }
    }

    // update job step configs
    void assembleJobStepUpdatedConfigs(Job job, Map<String, StepAllConf> confs) {
        if (job == null || MapUtils.isEmpty(confs)) return;
        for (Map.Entry<String, StepAllConf> entry : confs.entrySet()) {
            String stepName = entry.getKey();
            JobStep step = job.findStepByName(stepName);
            if (step != null) {
                if (step.getState().isDormant()) {      // only dormant step config can be updated
                    StepAllConf conf = entry.getValue();
                    step.getStepAllConf().assembleOther(conf);
                    logger.info(THIS_CLASS_NAME, String.format("update step %s with config: %s", stepName, conf));
                } else {
                    logger.warn(THIS_CLASS_NAME, String.format("step %s is not dormant, fail to update config", stepName));
                }
            }
        }
    }

    /**
     * while loop to iterate the steps until no step state update
     */
    void updateStepStatesInternal(StateChange stateChange, Job job, long curTime) {
        if (stateChange == null) return;
        if (job == null) return;
        if (!DateUtil.reachDate(curTime, job.getMidState().getAfterTime())) return;
        while (true) {
            boolean updated = false;
            for (JobStep step : job.getSteps()) {
                if (updateStepStateInternal(step, job, curTime)) {
                    stateChange.changeStepState(true);
                    step.updateProgression();
                    updated = true;
                }
            }
            if (!updated) break;
        }
    }

    /**
     * directly update step state to ignored, skip_by_failed, skip_by_error
     * update step state need to wait for step after time
     */
    boolean updateStepStateInternal(JobStep step, Job job, long curTime) {
        if (step == null || step.getStepDefine() == null || job == null) return false;
        if (!DateUtil.reachDate(curTime, step.getMidState().getAfterTime())) return false;
        if (step.getState().isDormant()) {
            boolean prevDone = true;    // all prev steps done
            boolean prevSuccess = true; // all prev steps finishes
            boolean prevError = false;  // any prev step error
            boolean prevFailed = false; // any prev step failed
            for (String prevStepName : step.getStepDefine().dependencyDoneStepNames()) {
                JobStep prevStep = job.findStepByName(prevStepName);
                if (prevStep == null) continue;
                prevDone = prevDone && prevStep.getState().done();
                prevSuccess = prevSuccess && prevStep.getState().resultSuccess();
                prevError = prevError || prevStep.getState().resultError();
                prevFailed = prevFailed || prevStep.getState().resultFailed();
            }
            // only if all prev steps done, current step can be updated
            if (prevDone) {
                // DORMANT -> IGNORED: when dependent steps all result success, and need ignore
                if (step.needIgnore() && prevSuccess) {
                    step.setState(StepStateEnum.IGNORED);
                    return true;
                }
                // DORMANT -> SKIP_BY_ERROR: if any dependent step result error
                if (prevError) {
                    step.setState(StepStateEnum.SKIP_BY_ERROR);
                    return true;
                }
                // DORMANT -> SKIP_BY_FAILED: if any dependent step result failed
                if (prevFailed) {
                    step.setState(step.canFail() ? StepStateEnum.ACCEPTABLE_FAILED : StepStateEnum.SKIP_BY_FAILED);
//                step.setState(StepStateEnum.SKIP_BY_FAILED);
                    return true;
                }
            }
        }
        return false;
    }

}
