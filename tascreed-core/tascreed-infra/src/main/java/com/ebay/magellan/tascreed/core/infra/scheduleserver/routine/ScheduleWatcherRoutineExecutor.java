package com.ebay.magellan.tascreed.core.infra.scheduleserver.routine;

import com.ebay.magellan.tascreed.core.domain.schedule.Schedule;
import com.ebay.magellan.tascreed.core.infra.routine.annotation.RoutineExec;
import com.ebay.magellan.tascreed.core.infra.routine.execute.NormalRoutineExecutor;
import com.ebay.magellan.tascreed.core.infra.scheduleserver.help.ScheduleHelper;
import com.ebay.magellan.tascreed.core.schedule.time.TimeWheel;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RoutineExec(routine="schedule-watcher", scale = 1, priority = 80, interval = 60 * 1000L)
@Component
@Scope("prototype")
public class ScheduleWatcherRoutineExecutor extends NormalRoutineExecutor {

    private static final String THIS_CLASS_NAME = ScheduleWatcherRoutineExecutor.class.getSimpleName();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TcLogger logger;

    @Autowired
    private ScheduleHelper scheduleHelper;

    @Autowired
    private TimeWheel timeWheel;

    @Autowired
    private ApplicationContext context;

    // -----

    @Override
    protected void initImpl() throws TcException {
        timeWheel.init();
        logger.info(THIS_CLASS_NAME, String.format(
                "schedule watcher routine [%s] init done", routine.getFullName()));
    }

    @Override
    protected void executeRoundImpl() throws TcException {
        try {
            triggerScheduleJobs();
        } catch (Exception e) {
            TcExceptionBuilder.throwTumblerException(
                    TcErrorEnum.TUMBLER_NON_RETRY_EXCEPTION, e.getMessage());
        }
    }

    @Override
    protected void closeImpl() throws TcException {
        timeWheel.stop();
        logger.info(THIS_CLASS_NAME, String.format(
                "schedule watcher routine [%s] close done", routine.getFullName()));
    }

    // -----

    private static final int DURATION_TIMES_INTERVAL = 5;

    private volatile long lastExecTime = 0L;
    long getCalcStartTime(long now) {
        long ret = lastExecTime;
        if (lastExecTime < now) {
            lastExecTime = now;
        }
        return ret > 0L ? ret : lastExecTime;
    }

    void triggerScheduleJobs() throws TcException {
        // 1. update last exec time, prepare calculate time range
        long now = System.currentTimeMillis();
        long startTime = getCalcStartTime(now);
        if (routine == null) return;
        long duration = routine.getInterval() * DURATION_TIMES_INTERVAL;

        // 2. read all the schedules
        List<Schedule> schedules = scheduleHelper.readAllSchedules();
        if (CollectionUtils.isEmpty(schedules)) return;

        // 3. calc the next trigger infos within a period
        List<TriggerInfo> triggerInfos = calcTriggerInfos(schedules, startTime, duration);

        // 4. schedule triggers into time wheel
        scheduleTriggers(triggerInfos);
    }

    List<TriggerInfo> calcTriggerInfos(List<Schedule> schedules, long startTime, long duration) {
        List<TriggerInfo> triggerInfos = new ArrayList<>();
        for (Schedule schedule : schedules) {
            if (schedule != null && schedule.getConf() != null) {
                List<Long> ts = schedule.getConf().nextTriggerTimestamps(startTime, duration);
                for (Long t : ts) {
                    Date triggerTime = new Date(t);
                    if (schedule.canTrigger(triggerTime)) {
                        triggerInfos.add(new TriggerInfo(schedule.getScheduleName(), triggerTime));
                    }
                }
            }
        }
        return triggerInfos;
    }

    void scheduleTriggers(List<TriggerInfo> triggerInfos) {
        for (TriggerInfo triggerInfo : triggerInfos) {
            ScheduleTimerTask scheduleTimerTask = createScheduleTimerTask(triggerInfo);
            timeWheel.insert(scheduleTimerTask);
        }
    }

    ScheduleTimerTask createScheduleTimerTask(TriggerInfo triggerInfo) {
        if (triggerInfo == null) return null;
        ScheduleTimerTask stt = context.getBean(ScheduleTimerTask.class);
        stt.setScheduleName(triggerInfo.scheduleName);
        stt.setTriggerTime(triggerInfo.triggerTime.getTime());
        stt.setKey(triggerInfo.triggerKey);
        return stt;
    }

    static class TriggerInfo {
        String scheduleName;
        Date triggerTime;
        String triggerKey;
        public TriggerInfo(String scheduleName, Date triggerTime) {
            this.scheduleName = scheduleName;
            this.triggerTime = triggerTime;
            this.triggerKey = String.format("%s-%d", scheduleName, triggerTime.getTime());
        }
    }

    // -----

}
