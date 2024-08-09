package com.ebay.magellan.tumbler.core.domain.trait;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
public class TraitsAmend {
    @JsonProperty("enable")
    private List<String> enableTraitStrList;
    @JsonProperty("disable")
    private List<String> disableTraitStrList;

    @JsonIgnore
    private Set<Trait> enableTraits = new HashSet<>();
    @JsonIgnore
    private Set<Trait> disableTraits = new HashSet<>();

    // -----

    public void setEnableTraitStrList(List<String> enableTraitStrList) {
        this.enableTraitStrList = enableTraitStrList;
        genTraits(enableTraits, enableTraitStrList);
    }

    public void setDisableTraitStrList(List<String> disableTraitStrList) {
        this.disableTraitStrList = disableTraitStrList;
        genTraits(disableTraits, disableTraitStrList);
    }

    private void genTraits(Set<Trait> traits, List<String> traitStrList) {
        if (traits == null) return;
        traits.clear();
        if (CollectionUtils.isNotEmpty(traitStrList)) {
            for (String traitStr : traitStrList) {
                Trait trait = Trait.findByName(traitStr);
                if (trait != null) {
                    traits.add(trait);
                }
            }
        }
    }

}
