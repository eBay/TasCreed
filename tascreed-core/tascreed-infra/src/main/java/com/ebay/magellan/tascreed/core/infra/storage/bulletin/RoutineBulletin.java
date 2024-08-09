package com.ebay.magellan.tascreed.core.infra.storage.bulletin;

import com.ebay.magellan.tascreed.core.domain.routine.Routine;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;

import java.util.Map;

public interface RoutineBulletin extends BaseOccupyBulletin {

    String getRoutineAdoptionKey(Routine routine);

    String checkRoutineAdoption(Routine routine) throws TumblerException;

    Map<String, String> readAllRoutineAdoptions() throws Exception;

    // -----

    String readRoutineCheckpoint(Routine routine) throws TumblerException;

    boolean updateRoutineCheckpoint(Routine routine, String adoptionValue) throws TumblerException;

}
