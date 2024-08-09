package com.ebay.magellan.tascreed.core.infra.controller;

import com.ebay.magellan.tascreed.core.domain.schedule.Schedule;
import com.ebay.magellan.tascreed.core.infra.scheduleserver.ScheduleServer;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleServer scheduleServer;

    // -----

    @PostMapping("")
    public Schedule submitSchedule(@Valid @RequestBody Schedule sch) throws TcException {
        Schedule schedule = scheduleServer.submitSchedule(sch);
        return schedule;
    }

    @GetMapping("/{scheduleName}")
    public Schedule querySchedule(@NotBlank @PathVariable("scheduleName") String scheduleName) {
        Schedule schedule = scheduleServer.findScheduleByName(scheduleName);
        return schedule;
    }

    // -----

    @PutMapping("")
    public Schedule updateSchedule(@Valid @RequestBody Schedule sch) throws TcException {
        Schedule schedule = scheduleServer.updateSchedule(sch);
        return schedule;
    }

    @DeleteMapping("/{scheduleName}")
    public Schedule deleteSchedule(
            @NotBlank @PathVariable("scheduleName") String scheduleName) throws Exception {
        Schedule schedule = scheduleServer.deleteSchedule(scheduleName);
        return schedule;
    }

}
