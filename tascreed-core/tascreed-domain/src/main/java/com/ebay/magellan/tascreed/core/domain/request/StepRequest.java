package com.ebay.magellan.tascreed.core.domain.request;

import com.ebay.magellan.tascreed.core.domain.define.conf.StepAllConf;
import com.ebay.magellan.tascreed.core.domain.request.extra.ReqTime;
import com.ebay.magellan.tascreed.core.domain.trait.TraitsAmend;
import com.ebay.magellan.tascreed.depend.common.util.DefaultValueUtil;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@ToString
public class StepRequest {
    @JsonProperty("stepName")
    @JsonAlias({"name"})
    private String stepName;
    @JsonProperty("exeClass")
    private String exeClass;

    @JsonProperty("affinityRule")
    private String affinityRule;

    @JsonProperty("ignore")
    private Boolean ignore;

//    @JsonProperty("archive")
//    private Boolean archive;

    @JsonProperty("traits")
    private TraitsAmend traitsAmend = new TraitsAmend();

    @JsonUnwrapped
    private StepAllConf stepAllConf = new StepAllConf();
    @JsonProperty("params")
    private Map<String, String> params;

    // optional, task pick after a time, by default no limit
    @JsonProperty("after")
    private ReqTime after;

    // expect execute duration of each task, in milliseconds
    @JsonProperty("duration")
    private Long duration;

    public StepRequest(String stepName, Map<String, String> params) {
        this.stepName = stepName;
        this.params = params;
    }

    public StepRequest() {
    }

    // -----

    public boolean isIgnore() {
        return DefaultValueUtil.booleanValue(ignore);
    }

    // -----

    @JsonIgnore
    public Date getAfterTime() {
        if (after != null) {
            return after.getTime();
        }
        return null;
    }

}
