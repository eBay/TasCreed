package com.ebay.magellan.tumbler.core.domain.builder;

import com.ebay.magellan.tumbler.core.domain.routine.Routine;
import com.ebay.magellan.tumbler.core.domain.routine.RoutineDefine;

import java.util.ArrayList;
import java.util.List;

public class RoutineBuilder {

    public List<Routine> buildRoutines(RoutineDefine routineDefine) {
        List<Routine> routines = new ArrayList<>();
        if (routineDefine != null) {
            for (int i = 0; i < routineDefine.getScale(); i++) {
                routines.add(Routine.newRoutine(routineDefine, i));
            }
        }
        return routines;
    }
}
