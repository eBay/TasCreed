package com.ebay.magellan.tumbler.core.infra.routine.execute;

import com.ebay.magellan.tumbler.core.domain.routine.Routine;
import com.ebay.magellan.tumbler.core.infra.routine.alive.RoutineOccupation;
import com.ebay.magellan.tumbler.core.infra.storage.bulletin.RoutineBulletin;
import com.ebay.magellan.tumbler.depend.common.logger.TumblerLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RoutineExecutorFactory {

    private static final String THIS_CLASS_NAME = RoutineExecutorFactory.class.getSimpleName();

    @Autowired
    private RoutineExecutorRegistry routineExecutorRegistry;

    @Autowired
    private ApplicationContext context;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    public RoutineExecutor buildRoutineExecutor(Routine routine) {
        if (routine == null) return null;
        String routineFullName = routine.getFullName();
        Class<? extends RoutineExecutor> clz = getRoutineExecutorClass(routine);
        if (clz == null) {
            logger.error(THIS_CLASS_NAME, String.format("failed to find routineExecutor for routine [%s]", routineFullName));
            return null;
        }
        logger.info(THIS_CLASS_NAME, String.format("will build routineExecutor [%s] for routine [%s]", clz.getSimpleName(), routineFullName));
        return context.getBean(clz);
    }

    public RoutineOccupation buildRoutineOccupation(Routine routine, String workerThreadName, RoutineBulletin bulletin) {
        if (routine == null || bulletin == null) return null;
        return new RoutineOccupation(routine, workerThreadName, bulletin);
    }

    public boolean routineExecutorExists(Routine routine) {
        return getRoutineExecutorClass(routine) != null;
    }

    // -----

    Class<? extends RoutineExecutor> getRoutineExecutorClass(Routine routine) {
        if (routine == null) return null;
        return getRoutineExecutorClassFromRegistry(routine.getRoutineName());
    }

    Class<? extends RoutineExecutor> getRoutineExecutorClassFromRegistry(String routineName) {
        Class<? extends RoutineExecutor> clz = routineExecutorRegistry.getRoutineExecutor(routineName);
        if (clz == null) {
            logger.warn(THIS_CLASS_NAME, String.format(
                    "routine executor of [%s] is not found in registry", routineName));
        }
        return clz;
    }
}
