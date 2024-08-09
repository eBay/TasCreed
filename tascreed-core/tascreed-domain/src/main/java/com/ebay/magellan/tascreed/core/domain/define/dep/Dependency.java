package com.ebay.magellan.tascreed.core.domain.define.dep;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * dependency of step, only these dependent conditions passes, the step can create tasks
 */
@Getter
@Setter
@ToString
public class Dependency {
    @JsonProperty("phase")
    private Integer phase;

    @JsonProperty("doneSteps")
    @JsonAlias({"steps"})
    private List<String> doneSteps;    // dependent done steps name list

    // -----

    public int phaseValue() {
        return phase != null ? phase : 0;
    }

    // -----

    public List<String> doneSteps() {
        return CollectionUtils.isNotEmpty(doneSteps) ?
                doneSteps : new ArrayList<>();
    }
}
