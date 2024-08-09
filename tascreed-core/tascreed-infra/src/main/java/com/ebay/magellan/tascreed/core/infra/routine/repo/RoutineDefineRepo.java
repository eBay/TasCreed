package com.ebay.magellan.tascreed.core.infra.routine.repo;

import com.ebay.magellan.tascreed.core.domain.routine.RoutineDefine;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RoutineDefineRepo {

    private static final String THIS_CLASS_NAME = RoutineDefineRepo.class.getSimpleName();

    @Autowired
    private RoutineRepo routineRepo;

    @Autowired
    private Environment environment;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TcLogger logger;

    private Map<String, RoutineDefine> defines = new LinkedHashMap<>();

    // -----

    public RoutineDefine getRoutineDefine(String name) {
        return defines.get(name);
    }

    // -----

    public void addRoutineDefine(RoutineDefine routineDefine) {
        if (routineDefine == null) return;
        if (StringUtils.isBlank(routineDefine.getRoutineName())) return;
        defines.put(routineDefine.getRoutineName(), routineDefine);
    }

    public void endRegisterRoutineDefine() {
        // 1. update routine params by config
        defines.values().forEach(this::updateParamsOfRoutineDefine);

        // 2. rebuild routine repo
        routineRepo.refresh(defines.values());
    }

    // -----

    // sample: tumbler.routine.param.job-watcher.scale=1
    private static final String ROUTINE_DEFINE_PARAM_PREFIX = "tumbler.routine.param";

    private String buildParamKey(String routineName, String paramName) {
        return String.format("%s.%s.%s", ROUTINE_DEFINE_PARAM_PREFIX, routineName, paramName);
    }

    private void updateParamsOfRoutineDefine(RoutineDefine rd) {
        if (rd == null) return;

        // scale
        Integer scale = tryGetParam(
                buildParamKey(rd.getRoutineName(), "scale"), Integer.class);
        if (scale != null && scale >= 0) {
            rd.setScale(scale);
        }
        // priority
        Integer priority = tryGetParam(
                buildParamKey(rd.getRoutineName(), "priority"), Integer.class);
        if (priority != null && priority > 0) {
            rd.setPriority(priority);
        }
        // interval
        Long interval = tryGetParam(
                buildParamKey(rd.getRoutineName(), "interval"), Long.class);
        if (interval != null && interval > 0) {
            rd.setInterval(interval);
        }
    }

    private <T> T tryGetParam(String key, Class<T> targetType) {
        try {
            return environment.getProperty(key, targetType);
        } catch (Exception e) {
            logger.warn(THIS_CLASS_NAME, String.format(
                    "get param of key %s error: %s", key, e.getMessage()));
        }
        return null;
    }

    // -----

}
