package com.ebay.magellan.tascreed.core.infra.executor;

import com.ebay.magellan.tascreed.core.infra.executor.annotation.TaskExec;
import com.ebay.magellan.tascreed.core.infra.executor.annotation.TaskExecs;
import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
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
public class TaskExecutorRegistry {

    private static final String THIS_CLASS_NAME = TaskExecutorRegistry.class.getSimpleName();

    @Autowired
    private ApplicationContext context;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    private Map<JobStepKey, Class> map = new HashMap<>();

    // -----

    private JobStepKey buildJobStepKey(String jn, String sn) {
        return new JobStepKey(jn, sn);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    private static final class JobStepKey {
        private String jobName;
        private String stepName;
    }

    // -----

    public <T extends TaskExecutor> Class<T> getTaskExecutor(String jobName, String stepName) {
        return map.get(buildJobStepKey(jobName, stepName));
    }

    // -----

    public <T extends TaskExecutor> void registerTaskExecutor(String jobName, String stepName, Class<T> clz) {
        Class oldClz = map.put(buildJobStepKey(jobName, stepName), clz);
        String overwrite = oldClz != null ? String.format(" (overwrite %s)", oldClz.getSimpleName()) : "";
        logger.info(THIS_CLASS_NAME, String.format(
                "register taskExecutor [%s]%s for job [%s] step [%s]",
                clz.getSimpleName(), overwrite, jobName, stepName));
    }

    // -----

    /**
     * load task executor beans from spring context,
     * register task executors one by one in the order of bean name.
     * the latter one will overwrite previous one.
     */
    public void registerTaskExecutors() {
        logger.info(THIS_CLASS_NAME, "register task executors by annotation start");
        try {
            Map<String, TaskExecutor> beans = context.getBeansOfType(TaskExecutor.class);
            beans.entrySet().stream()
                    .sorted(Comparator.comparing(e ->  e.getKey()))
                    .forEach(e -> registerTaskExecutor(e.getValue()));
        } catch (BeansException e) {
            logger.error(THIS_CLASS_NAME, String.format(
                    "register task executors failed: %s", e.getMessage()));
            throw e;
        }
        logger.info(THIS_CLASS_NAME, "register task executors by annotation done");
    }

    private <T extends TaskExecutor> void registerTaskExecutor(T taskExecutor) {
        if (taskExecutor == null) return;
        Class clz = taskExecutor.getClass();

        TaskExec annotation = AnnotationUtils.getAnnotation(clz, TaskExec.class);
        registerTaskExecutorByAnnotation(annotation, clz);

        TaskExecs annotations = AnnotationUtils.getAnnotation(clz, TaskExecs.class);
        registerTaskExecutorByAnnotation(annotations, clz);
    }

    private void registerTaskExecutorByAnnotation(TaskExec a, Class clz) {
        if (a == null || clz == null) return;
        if (StringUtils.isBlank(a.job()) || a.step() == null) return;
        for (String s : a.step()) {
            registerTaskExecutor(a.job(), s, clz);
        }
    }
    private void registerTaskExecutorByAnnotation(TaskExecs as, Class clz) {
        if (as == null || clz == null) return;
        Arrays.stream(as.value()).forEach(a -> registerTaskExecutorByAnnotation(a, clz));
    }

}
