package com.ebay.magellan.tascreed.core.infra.scheduleserver;

import com.ebay.magellan.tascreed.core.domain.builder.ScheduleBuilder;
import com.ebay.magellan.tascreed.core.domain.define.JobDefine;
import com.ebay.magellan.tascreed.core.domain.duty.NodeDutyEnum;
import com.ebay.magellan.tascreed.core.domain.schedule.Schedule;
import com.ebay.magellan.tascreed.core.infra.duty.DutyHelper;
import com.ebay.magellan.tascreed.core.infra.repo.JobDefineRepo;
import com.ebay.magellan.tascreed.core.infra.scheduleserver.help.ScheduleHelper;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.ScheduleBulletin;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleServer {
    private static final String THIS_CLASS_NAME = ScheduleServer.class.getSimpleName();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TcLogger logger;

    @Autowired
    private JobDefineRepo jobDefineRepo;

    @Autowired
    private ScheduleBulletin scheduleBulletin;

    @Autowired
    private ScheduleHelper scheduleHelper;

    @Autowired
    private DutyHelper dutyHelper;

    // -----

    public Schedule findScheduleByName(String scheduleName) {
        return scheduleHelper.readSchedule(scheduleName);
    }

    // -----

    void validateSchedule(Schedule sch) throws TcException {
        String err = sch.validate();
        if (StringUtils.isNotBlank(err)) {
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_FATAL_JOB_EXCEPTION,
                    String.format("validate schedule fails: %s", err));
        }

        String jobName = sch.getJobRequest().getJobName();
        JobDefine jd = jobDefineRepo.getDefine(jobName);
        if (jd == null) {
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_FATAL_JOB_EXCEPTION,
                    String.format("validate schedule fails: job define %s not found", jobName));
        }
    }

    public Schedule submitSchedule(Schedule sch) throws TcException {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.SCHEDULE_SERVER);

        if (sch == null) return null;
        boolean success = false;
        String name = sch.getScheduleName();
        Schedule schedule = findScheduleByName(name);
        if (schedule == null) {
            logger.info(THIS_CLASS_NAME, String.format("the schedule name %s is fresh, will create schedule", name));

            validateSchedule(sch);

            ScheduleBuilder.initSchedule(sch);
            schedule = sch;

            try {
                success = scheduleHelper.submitScheduleWithJobs(schedule, null);
            } catch (TcException e) {
                logger.error(THIS_CLASS_NAME, String.format("submit schedule fails: %s", e.getMessage()));
                throw e;
            }

            if (!success) {
                schedule = null;
                logger.warn(THIS_CLASS_NAME, String.format("create schedule %s fails", name));
            } else {
                logger.info(THIS_CLASS_NAME, String.format("create schedule %s success", name));
            }
        } else {
            logger.info(THIS_CLASS_NAME, String.format("the schedule name %s is duplicated", name));
        }
        return schedule;
    }

    public Schedule updateSchedule(Schedule sch) throws TcException {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.SCHEDULE_SERVER);

        if (sch == null) return null;
        boolean success = false;
        String name = sch.getScheduleName();

        // 1. find schedule
        Schedule schedule = findScheduleByName(name);
        if (schedule == null) {
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_FATAL_JOB_EXCEPTION,
                    String.format("schedule %s does not exist", name));
        }

        // 2. update schedule
        ScheduleBuilder.updateSchedule(schedule, sch);

        // 3. validate schedule
        validateSchedule(schedule);

        // 4. submit schedule
        logger.info(THIS_CLASS_NAME, String.format("update schedule %s", name));
        try {
            success = scheduleHelper.submitScheduleWithJobs(schedule, null);
        } catch (TcException e) {
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_FATAL_JOB_EXCEPTION,
                    String.format("update schedule fails: %s", e.getMessage()), e);
        }

        if (!success) {
            schedule = null;
            logger.warn(THIS_CLASS_NAME, String.format("update schedule %s fails", name));
        } else {
            logger.info(THIS_CLASS_NAME, String.format("update schedule %s success", name));
        }

        return schedule;
    }

    // -----

    public Schedule deleteSchedule(String scheduleName) throws Exception {
        dutyHelper.dutyEnableCheck(NodeDutyEnum.SCHEDULE_SERVER);

        return scheduleHelper.deleteSchedule(scheduleName);
    }

}
