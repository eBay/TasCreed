package com.ebay.magellan.tascreed.core.infra.executor.progression;

import com.ebay.magellan.tascreed.core.domain.state.partial.Progression;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TaskExecProgression {
    private long goal;
    private long current;

    public void finish() {
        this.current = this.goal;
    }

    public void init(long goal) {
        this.goal = Math.max(goal, 1L);
        this.current = 0L;
    }

    public void setCurrent(long current) {
        this.current = Math.min(Math.max(current, 0L), this.goal);
    }

    // -----

    public Progression toValue() {
        return Progression.buildProgression(current, goal);
    }
}
