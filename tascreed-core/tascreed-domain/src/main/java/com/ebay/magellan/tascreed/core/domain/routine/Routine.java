package com.ebay.magellan.tascreed.core.domain.routine;

import com.ebay.magellan.tascreed.core.domain.occupy.OccupyInfo;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * routine instance with unique name
 */
@Getter
@Setter
@ToString
public class Routine {
    @JsonProperty("routineName")
    @JsonAlias({"name"})
    private String routineName;

    @JsonProperty("index")
    private int index;
    @JsonProperty("scale")
    private int scale;

    @JsonProperty("priority")
    private int priority;

    @JsonProperty("interval")
    private long interval;

    @JsonIgnore
    private OccupyInfo occupyInfo;
    @JsonIgnore
    private String checkpointValue; // checkpoint value of routine

    // -----

    public static Routine newRoutine(RoutineDefine rd, int index) {
        Routine r = new Routine();
        r.setRoutineName(rd.getRoutineName());
        r.setIndex(index);
        r.setScale(rd.getScale());
        r.setPriority(rd.getPriority());
        r.setInterval(rd.getInterval());
        return r;
    }

    // -----

    public String getFullName() {
        if (scale > 1) {
            return String.format("%s-%d.%d", routineName, scale, index);
        } else {
            return String.format("%s", routineName);
        }
    }

    // -----

    public void occupy(OccupyInfo occupyInfo, String checkpointValue) {
        this.occupyInfo = occupyInfo;
        this.checkpointValue = checkpointValue;
    }

    public void revoke() {
        this.occupyInfo = null;
        this.checkpointValue = null;
    }
}
