package com.ebay.magellan.tascreed.core.infra.storage.bulletin;

import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;

import java.util.Map;

public interface TaskBulletin extends BaseOccupyBulletin {

    Map<String, String> readAllTodoTasks() throws Exception;
    Map<String, String> readAllDoneTasks() throws Exception;
    Map<String, String> readAllErrorTasks() throws Exception;

    // -----

    String getTaskAdoptionKey(Task task);

    String checkTaskAdoption(Task task) throws TumblerException;

    Map<String, String> readAllTaskAdoptions() throws Exception;

    // -----

    boolean moveTodoTask2DoneTask(Task task, String adoptionValue,
                                  boolean withError) throws TumblerException;

    // -----

    boolean updateTodoTask(Task task, String adoptionValue) throws TumblerException;

}
