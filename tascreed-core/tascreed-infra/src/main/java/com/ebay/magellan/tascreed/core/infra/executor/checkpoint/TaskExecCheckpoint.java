package com.ebay.magellan.tascreed.core.infra.executor.checkpoint;

import com.ebay.magellan.tascreed.core.domain.state.partial.TaskCheckpoint;

public interface TaskExecCheckpoint {

    /**
     * convert recorded checkpoint value to runtime checkpoint entity
     * @param cp recorded checkpoint
     */
    void fromValue(TaskCheckpoint cp);

    /**
     * convert runtime checkpoint entity to recorded checkpoint value
     * @return parsed checkpoint
     */
    TaskCheckpoint toValue();

}
