package com.ebay.magellan.tascreed.core.domain.duty;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NodeDutyRule {
    /**
     * valid node condition
     */
    // condition of tc version
    @JsonProperty("minValidTcVersion")
    @JsonAlias("minValidVersion")
    private String minValidTcVersion;
    // condition of user app version
    @JsonProperty("minValidAppVersion")
    private String minValidAppVersion;

    @JsonProperty("validHostNameRegex")
    private String validHostNameRegex;

    /**
     * invalid node condition
     */
    @JsonProperty("invalidHostNameRegex")
    private String invalidHostNameRegex;

    /**
     * disable duties if invalid node
     */
    @JsonProperty("disableDutiesIfInvalid")
    private List<NodeDutyEnum> disableDutiesIfInvalid;
}
