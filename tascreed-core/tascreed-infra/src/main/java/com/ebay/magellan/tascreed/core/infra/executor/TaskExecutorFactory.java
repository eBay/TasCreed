package com.ebay.magellan.tascreed.core.infra.executor;

import com.ebay.magellan.tascreed.core.infra.executor.alive.TaskOccupation;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.TaskBulletin;
import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tascreed.core.domain.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class TaskExecutorFactory {

    private static final String THIS_CLASS_NAME = TaskExecutorFactory.class.getSimpleName();

    private static final ClassLoader classLoader = TaskExecutorFactory.class.getClassLoader();

    @Autowired
    private TaskExecutorRegistry taskExecutorRegistry;

    @Autowired
    private ApplicationContext context;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    public TaskExecutor buildTaskExecutor(Task task) {
        if (task == null) return null;
        String jobName = task.getJobName();
        String stepName = task.getStepName();
        Class<? extends TaskExecutor> clz = getTaskExecutorClass(task);
        if (clz == null) {
            logger.error(THIS_CLASS_NAME, String.format("failed to find taskExecutor for job [%s] step [%s]", jobName, stepName));
            return null;
        }
        logger.info(THIS_CLASS_NAME, String.format("will build taskExecutor [%s] for job [%s] step [%s]", clz.getSimpleName(), jobName, stepName));
        return context.getBean(clz);
    }

    public TaskOccupation buildTaskOccupation(Task task, String workerThreadName, TaskBulletin bulletin) {
        if (task == null || bulletin == null) return null;
        return new TaskOccupation(task, workerThreadName, bulletin);
    }

    public boolean taskExecutorExists(Task task) {
        return getTaskExecutorClass(task) != null;
    }

    // -----

    Class<? extends TaskExecutor> getTaskExecutorClass(Task task) {
        if (task == null) return null;

        if (StringUtils.isNotBlank(task.getExeClass())) {
            return getTaskExecutorClassByName(task.getExeClass());
        } else {
            return getTaskExecutorClassFromRegistry(task.getJobName(), task.getStepName());
        }
    }

    Class<? extends TaskExecutor> getTaskExecutorClassByName(String className) {
        if (StringUtils.isBlank(className)) return null;
        try {
            Class clz = classLoader.loadClass(className);
            if (clz != null && TaskExecutor.class.isAssignableFrom(clz)) {
                return (Class<? extends TaskExecutor>) clz;
            } else {
                logger.warn(THIS_CLASS_NAME, String.format(
                        "task executor class [%s] can not be loaded", className));
                return null;
            }
        } catch (ClassNotFoundException e) {
            logger.warn(THIS_CLASS_NAME, String.format("class not found: %s", className));
            return null;
        }
    }

    Class<? extends TaskExecutor> getTaskExecutorClassFromRegistry(String jobName, String stepName) {
        Class<? extends TaskExecutor> clz = taskExecutorRegistry.getTaskExecutor(jobName, stepName);
        if (clz == null) {
            logger.warn(THIS_CLASS_NAME, String.format(
                    "task executor of [%s, %s] is not found in registry", jobName, stepName));
        }
        return clz;
    }

}
