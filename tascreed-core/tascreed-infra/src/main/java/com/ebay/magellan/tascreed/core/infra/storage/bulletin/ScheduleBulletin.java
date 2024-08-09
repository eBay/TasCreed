package com.ebay.magellan.tascreed.core.infra.storage.bulletin;

import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.domain.schedule.Schedule;

import java.util.List;
import java.util.Map;

public interface ScheduleBulletin extends BaseBulletin {

    /**
     * submit schedule together with new triggered jobs
     * @param schedule the schedule to be updated
     * @param newJobs the new jobs to be submitted if any
     * @return success or not
     * @throws Exception
     */
    boolean submitScheduleAndJobs(Schedule schedule, List<Job> newJobs) throws Exception;

    // -----

    Map<String, String> readAllSchedules() throws Exception;
    String readSchedule(String scheduleName);

    // -----

    String deleteSchedule(String scheduleName) throws Exception;

}
