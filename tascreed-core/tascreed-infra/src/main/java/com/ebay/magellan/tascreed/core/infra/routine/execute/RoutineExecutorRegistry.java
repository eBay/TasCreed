package com.ebay.magellan.tascreed.core.infra.routine.execute;

import com.ebay.magellan.tascreed.core.domain.routine.RoutineDefine;
import com.ebay.magellan.tascreed.core.infra.executor.TaskExecutorRegistry;
import com.ebay.magellan.tascreed.core.infra.routine.annotation.RoutineExec;
import com.ebay.magellan.tascreed.core.infra.routine.annotation.RoutineExecs;
import com.ebay.magellan.tascreed.core.infra.routine.repo.RoutineDefineRepo;
import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Component
public class RoutineExecutorRegistry {

    private static final String THIS_CLASS_NAME = TaskExecutorRegistry.class.getSimpleName();

    @Autowired
    private ApplicationContext context;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    @Autowired
    private RoutineDefineRepo routineDefineRepo;

    // map of registered routine executors, routine name as key, executor class as value
    private Map<String, Class> map = new HashMap<>();

    // -----

    public <T extends RoutineExecutor> Class<T> getRoutineExecutor(String routineName) {
        if (StringUtils.isBlank(routineName)) return null;
        return map.get(routineName);
    }

    // -----

    /**
     * 1. load routine executor beans from spring context,
     * register routine executors one by one in the order of bean name.
     * the latter one will overwrite previous one.
     * 2. register routine define into routine define repo at the same time
     * 3. refresh routine repo
     */
    public void registerRoutineExecutors() {
        logger.info(THIS_CLASS_NAME, "register routine executors by annotation start");
        try {
            Map<String, RoutineExecutor> beans = context.getBeansOfType(RoutineExecutor.class);
            beans.entrySet().stream()
                    .sorted(Comparator.comparing(e ->  e.getKey()))
                    .forEach(e -> registerRoutineExecutor(e.getValue()));
        } catch (BeansException e) {
            logger.error(THIS_CLASS_NAME, String.format(
                    "register routine executors failed: %s", e.getMessage()));
            throw e;
        }
        logger.info(THIS_CLASS_NAME, "register routine executors by annotation done");

        // end register routine define
        routineDefineRepo.endRegisterRoutineDefine();
    }

    private <T extends RoutineExecutor> void registerRoutineExecutor(T routineExecutor) {
        if (routineExecutor == null) return;
        Class clz = routineExecutor.getClass();

        RoutineExec annotation = AnnotationUtils.getAnnotation(clz, RoutineExec.class);
        registerRoutineExecutorByAnnotation(annotation, clz);

        RoutineExecs annotations = AnnotationUtils.getAnnotation(clz, RoutineExecs.class);
        registerRoutineExecutorByAnnotation(annotations, clz);
    }

    private void registerRoutineExecutorByAnnotation(RoutineExec a, Class clz) {
        if (a == null || clz == null) return;
        if (StringUtils.isBlank(a.routine())) return;
        registerRoutineExecutor(a.routine(), clz);
        registerRoutineDefine(a);
    }
    private void registerRoutineExecutorByAnnotation(RoutineExecs as, Class clz) {
        if (as == null || clz == null) return;
        Arrays.stream(as.value()).forEach(a -> registerRoutineExecutorByAnnotation(a, clz));
    }

    private <T extends RoutineExecutor> void registerRoutineExecutor(String routineName, Class<T> clz) {
        if (StringUtils.isBlank(routineName)) return;
        Class oldClz = map.put(routineName, clz);
        String overwrite = oldClz != null ? String.format(" (overwrite %s)", oldClz.getSimpleName()) : "";
        logger.info(THIS_CLASS_NAME, String.format(
                "register routineExecutor [%s]%s for routine [%s]",
                clz.getSimpleName(), overwrite, routineName));
    }

    // register to routine define repo
    private void registerRoutineDefine(RoutineExec a) {
        if (a == null) return;
        RoutineDefine routineDefine = new RoutineDefine(a.routine(), a.scale(), a.priority(), a.interval());
        routineDefineRepo.addRoutineDefine(routineDefine);
    }
}
