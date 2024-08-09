package com.ebay.magellan.tumbler.core.infra.scheduleserver.routine;

import com.ebay.magellan.tumbler.core.domain.job.Job;
import com.ebay.magellan.tumbler.core.domain.job.JobInstKey;
import com.ebay.magellan.tumbler.core.domain.request.JobRequest;
import com.ebay.magellan.tumbler.core.domain.schedule.Schedule;
import com.ebay.magellan.tumbler.core.domain.schedule.Trigger;
import com.ebay.magellan.tumbler.core.domain.schedule.mid.TriggerState;
import com.ebay.magellan.tumbler.core.infra.jobserver.JobServer;
import com.ebay.magellan.tumbler.core.infra.scheduleserver.help.ScheduleHelper;
import com.ebay.magellan.tumbler.core.schedule.time.task.AbstractTimerTask;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;
import com.ebay.magellan.tumbler.depend.common.logger.TumblerLogger;
import io.netty.util.Timeout;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Scope("prototype")
public class ScheduleTimerTask extends AbstractTimerTask {

    private static final String THIS_CLASS_NAME = ScheduleTimerTask.class.getSimpleName();

    @Autowired
    private ScheduleHelper scheduleHelper;

    @Autowired
    private JobServer jobServer;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    private String scheduleName;

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    // -----

    @Override
    public void exec(Timeout timeout) throws Exception {
        if (StringUtils.isBlank(scheduleName)) return;

        // read schedule
        Schedule schedule = scheduleHelper.readSchedule(scheduleName);
        if (schedule == null) {
            logger.warn(THIS_CLASS_NAME, String.format(
                    "schedule trigger %s ignores: the schedule not found", scheduleName));
        }

        // check can trigger or not
        Date triggerDate = new Date(getTriggerTime());
        if (!schedule.canTrigger(triggerDate) || !schedule.validTriggerTime(triggerDate)) {
            logger.warn(THIS_CLASS_NAME, String.format(
                    "schedule trigger %s ignores: the trigger time %s can not be triggered",
                    scheduleName, triggerDate));
        }
        Trigger trigger = schedule.trigger(triggerDate);

        // find job is created or not
        JobRequest jr = trigger.getJobRequest();
        JobInstKey id = new JobInstKey(jr.getJobName(), jr.getTrigger());
        Job job = jobServer.findJobByJobIdPair(id);
        if (job != null) {
            logger.info(THIS_CLASS_NAME, String.format(
                    "schedule trigger %s fails: the job instance key %s is duplicated", scheduleName, id));
            return;
        }

        // build new trigger job
        TriggerState curTriggerState = new TriggerState();
        curTriggerState.setTime(trigger.getTriggerTime());
        List<Job> newJobs = new ArrayList<>();
        try {
            job = jobServer.createNewJob(jr);
            newJobs.add(job);
        } catch (TumblerException e) {
            String err = String.format("schedule trigger %s fails: %s", scheduleName, e.getMessage());
            curTriggerState.fail(err);
            logger.error(THIS_CLASS_NAME, err);
        }
        schedule.updateTriggerState(curTriggerState);

        // submit trigger job and schedule
        logger.info(THIS_CLASS_NAME, String.format("schedule trigger %s submit", scheduleName));
        try {
            scheduleHelper.submitScheduleWithJobs(schedule, newJobs);
            logger.info(THIS_CLASS_NAME, String.format("schedule trigger %s submit success", scheduleName));
        } catch (TumblerException e) {
            logger.warn(THIS_CLASS_NAME, String.format(
                    "schedule trigger %s submit fails: %s", scheduleName, e.getMessage()));
        }
    }

}
