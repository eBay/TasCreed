package com.ebay.magellan.tumbler.core.domain.define;

import com.ebay.magellan.tumbler.core.domain.define.conf.StepAllConf;
import com.ebay.magellan.tumbler.core.domain.define.dep.Dependency;
import com.ebay.magellan.tumbler.core.domain.trait.Trait;
import com.ebay.magellan.tumbler.core.domain.trait.Traits;
import com.ebay.magellan.tumbler.depend.common.util.DefaultValueUtil;
import com.ebay.magellan.tumbler.depend.common.util.JsonParseUtil;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class StepDefine implements Define {
    @JsonProperty("stepName")
    @JsonAlias({"name"})
    private String stepName;

    @JsonProperty("exeClass")
    private String exeClass;

    @JsonProperty("stepType")
    @JsonAlias({"type"})
    private StepTypeEnum stepType;
    @JsonUnwrapped
    private StepAllConf stepAllConf = new StepAllConf();

    @JsonProperty("affinityRule")
    private String affinityRule;
    @JsonProperty("effort")
    private int effort = 1;

    @JsonProperty("traits")
    private List<String> traitStrList;
    @JsonIgnore
    private Traits traits = new Traits(Trait.TraitType.STEP_DEFINE);
    @Deprecated
    @JsonProperty("ignorable")
    private Boolean ignorable;      // indicate the step can be ignored
//    @JsonProperty("archive")
//    private Boolean archive;        // indicate the tasks of the step need to be archived or not

    // expect execute duration of each task, in milliseconds
    @JsonProperty("duration")
    private Long duration;

    @Deprecated
    @JsonProperty("dependentStep")
    private String dependentStep;   // dependent step name
    @JsonProperty("dependency")
    private Dependency dependency;  // step dependency

    @JsonProperty("params")
    private Map<String, String> params;

    // -----

    @Override
    public String name() {
        return stepName;
    }

    // -----

    public int phaseValue() {
        return dependency != null ? dependency.phaseValue() : 0;
    }

    // -----

    @JsonIgnore
    public boolean isSimple() {
        return (stepType == null || stepType == StepTypeEnum.SIMPLE);
    }
    @JsonIgnore
    public boolean isShard() {
        return stepType == StepTypeEnum.SHARD;
    }
    @JsonIgnore
    public boolean isPack() {
        return stepType == StepTypeEnum.PACK;
    }

    // -----

    public boolean canIgnore() {
        return traits.containsTrait(Trait.CAN_IGNORE)
                || DefaultValueUtil.booleanValue(ignorable);
    }

    // -----

    public void setTraitStrList(List<String> traitStrList) {
        this.traitStrList = traitStrList;
        traits.genTraitSet(traitStrList);
    }

    public List<String> getTraitStrList() {
        this.traitStrList = traits.genTraitStrList();
        return traitStrList;
    }

    // -----

    public List<String> dependencyDoneStepNames() {
        List<String> steps = dependency != null ?
                dependency.doneSteps() : new ArrayList<>();
        if (StringUtils.isNotEmpty(dependentStep)) {
            steps.add(dependentStep);
        }
        return steps.stream().filter(Objects::nonNull)
                .distinct().sorted().collect(Collectors.toList());
    }

    // -------------

    /**
     * json serialize and deserialize
     */
    private static ObjectReader reader = JsonParseUtil.getReader(StepDefine.class);
    private static ObjectWriter writer = JsonParseUtil.getWriter(StepDefine.class);

    public static StepDefine fromJson(String json) throws IOException {
        StepDefine pin = null;
        if (StringUtils.isNotBlank(json)) {
            pin = reader.readValue(json);
        }
        return pin;
    }
    public String toJson() throws JsonProcessingException {
        return writer.writeValueAsString(this);
    }
}
