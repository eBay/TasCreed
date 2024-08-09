package com.ebay.magellan.tascreed.core.infra.routine.execute.checkpoint;

public interface RoutineExecCheckpoint {

    /**
     * convert recorded checkpoint value to runtime checkpoint entity
     * @param cp recorded checkpoint
     */
    void fromValue(String cp);

    /**
     * convert runtime checkpoint entity to recorded checkpoint value
     * @return parsed checkpoint
     */
    String toValue();

}
