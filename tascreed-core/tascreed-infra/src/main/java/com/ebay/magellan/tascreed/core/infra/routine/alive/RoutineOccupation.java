package com.ebay.magellan.tascreed.core.infra.routine.alive;

import com.ebay.magellan.tascreed.core.domain.routine.Routine;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.RoutineBulletin;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;

@AllArgsConstructor
public class RoutineOccupation {
    private Routine occupiedRoutine;
    private String workerThreadName;

    private RoutineBulletin routineBulletin;

    // -----

    private static final int MAX_RETRY_TIMES = 5;

    public boolean routineStillOccupied() {
        for (int i = 0; i < MAX_RETRY_TIMES; i++) {
            try {
                return routineStillOccupiedImpl();
            } catch (Exception e) {
                // ignore
            }
        }
        return false;
    }

    // check if the routine still occupied by current thread
    private boolean routineStillOccupiedImpl() throws TumblerException {
        if (routineBulletin == null || occupiedRoutine == null) return false;
        String val = routineBulletin.checkRoutineAdoption(occupiedRoutine);
        return StringUtils.equals(val, workerThreadName);
    }
}
