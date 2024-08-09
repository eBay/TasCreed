package com.ebay.magellan.tumbler.core.domain.job;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * job instance key, to identify a unique job instance
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class JobInstKey {
    private String name;
    private String trigger;

    @Override
    public String toString() {
        return String.format("%s.%s", name, trigger);
    }
}
