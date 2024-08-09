package com.ebay.magellan.tascreed.core.infra.routine.repo;

import com.ebay.magellan.tascreed.core.domain.builder.RoutineBuilder;
import com.ebay.magellan.tascreed.core.domain.routine.Routine;
import com.ebay.magellan.tascreed.core.domain.routine.RoutineDefine;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Component
public class RoutineRepo {

    private static final RoutineBuilder routineBuilder = new RoutineBuilder();

    private Map<String, Routine> routines = new LinkedHashMap<>();

    // -----

    public void refresh(Collection<RoutineDefine> defines) {
        routines.clear();
        for (RoutineDefine define : defines) {
            List<Routine> rs = routineBuilder.buildRoutines(define);
            for (Routine r : rs) {
                routines.put(r.getFullName(), r);
            }
        }
    }

    // -----

    public Routine getRoutine(String fullName) {
        return routines.get(fullName);
    }
}
