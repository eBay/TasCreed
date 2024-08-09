package com.ebay.magellan.tascreed.core.domain.builder;

import com.ebay.magellan.tascreed.core.domain.schedule.Schedule;
import com.ebay.magellan.tascreed.core.domain.schedule.mid.ScheduleMidState;

import java.util.Date;

public class ScheduleBuilder {

    public static void initSchedule(Schedule sch) {
        if (sch == null) return;
        sch.getMidState().setCreateTime(new Date());
    }

    public static void updateSchedule(Schedule schedule, Schedule sch) {
        if (schedule == null) return;
        schedule.setJobRequest(sch.getJobRequest());
        schedule.setConf(sch.getConf());
        schedule.setVariables(sch.getVariables());
        updateMidState(schedule.getMidState(), sch.getMidState());
    }

    static void updateMidState(ScheduleMidState s1, ScheduleMidState s2) {
        if (s1 == null || s2 == null) return;
        s1.setAfterTime(s2.getAfterTime());
    }

}
