package com.ebay.magellan.tumbler.core.domain.state.partial;

import com.ebay.magellan.tumbler.depend.common.util.PercentageUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Progression {
    @JsonProperty(value = "progression")
    private String value;

    @JsonIgnore
    private long done;
    @JsonIgnore
    private long total = 1;

    // -----

    private Progression(long done, long total) {
        this.done = done;
        this.total = total;
        double pct = PercentageUtil.convertPercentage(done, total);
        this.value = String.format("%.2f%%", pct);
    }

    public static Progression buildProgression(long done, long total) {
        return new Progression(done, total);
    }
    public static Progression buildNonProgression() {
        return buildProgression(0, 1);
    }
    public static Progression buildFullProgression() {
        return buildProgression(1, 1);
    }

}
