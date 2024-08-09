package com.ebay.magellan.tascreed.core.infra.scheduleserver.help;

import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.schedule.Schedule;
import com.ebay.magellan.tascreed.core.domain.util.JsonUtil;
import com.ebay.magellan.tascreed.core.infra.constant.TumblerKeys;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.ScheduleBulletin;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import com.ebay.magellan.tascreed.depend.ext.etcd.lock.EtcdLock;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class ScheduleHelper {

    @Autowired
    private TumblerKeys tumblerKeys;

    @Autowired
    private ScheduleBulletin scheduleBulletin;

    public boolean submitScheduleWithJobs(Schedule schedule, List<Job> newJobs) throws TcException {
        boolean ret = false;
        if (schedule == null) return ret;

        schedule.getMidState().setModifyTime(new Date());

        String scheduleUpdateLock = tumblerKeys.getScheduleUpdateLockKey(schedule.getScheduleName());
        EtcdLock lock = null;
        try {
            lock = scheduleBulletin.lock(scheduleUpdateLock);

            ret = scheduleBulletin.submitScheduleAndJobs(schedule, newJobs);

        } catch (JsonProcessingException e) {
            TcExceptionBuilder.throwTumblerException(
                    TcErrorEnum.TUMBLER_FATAL_VALIDATION_EXCEPTION, e.getMessage());
        } catch (Exception e) {
            TcExceptionBuilder.throwTumblerException(
                    TcErrorEnum.TUMBLER_RETRY_EXCEPTION, e.getMessage());
        } finally {
            try {
                scheduleBulletin.unlock(lock);
            } catch (Exception e) {
                TcExceptionBuilder.throwTumblerException(
                        TcErrorEnum.TUMBLER_RETRY_EXCEPTION, e.getMessage());
            }
        }
        return ret;
    }

    // -----

    public List<Schedule> readAllSchedules() throws TcException {
        List<Schedule> ret = new ArrayList<>();
        try {
            Map<String, String> pairs = scheduleBulletin.readAllSchedules();
            ret = JsonUtil.parseSchedules(pairs.values());
        } catch (Exception e) {
            TcExceptionBuilder.throwEtcdRetryableException(e);
        }
        return ret;
    }

    public Schedule readSchedule(String scheduleName) {
        if (StringUtils.isBlank(scheduleName)) return null;
        return JsonUtil.parseSchedule(scheduleBulletin.readSchedule(scheduleName));
    }

    // -----

    public Schedule deleteSchedule(String scheduleName) throws Exception {
        if (StringUtils.isBlank(scheduleName)) return null;
        return JsonUtil.parseSchedule(scheduleBulletin.deleteSchedule(scheduleName));
    }

}
