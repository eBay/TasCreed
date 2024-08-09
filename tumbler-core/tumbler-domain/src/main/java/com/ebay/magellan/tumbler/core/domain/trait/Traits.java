package com.ebay.magellan.tumbler.core.domain.trait;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
public class Traits {
    private Trait.TraitType traitType;
    private Set<Trait> traitsSet = new LinkedHashSet<>();

    public Traits(Trait.TraitType traitType) {
        this.traitType = traitType;
    }

    // -----

    public boolean containsTrait(Trait trait) {
        if (trait == null) return false;
        return traitsSet.contains(trait);
    }

    // -----

    private void tryAddConfTrait(Trait trait) {
        if (trait != null && trait.isConfigurable() && trait.getType().covers(traitType)) {
            traitsSet.add(trait);
        }
    }
    private void tryRemoveConfTrait(Trait trait) {
        if (trait != null && trait.isConfigurable() && trait.getType().covers(traitType)) {
            traitsSet.remove(trait);
        }
    }

    private void tryAddTrait(Trait trait) {
        if (trait != null && trait.getType().covers(traitType)) {
            traitsSet.add(trait);
        }
    }

    public void trySetTrait(Trait trait, boolean enable) {
        if (trait != null && !trait.isConfigurable() && trait.getType().covers(traitType)) {
            if (enable) {
                traitsSet.add(trait);
            } else {
                traitsSet.remove(trait);
            }
        }
    }

    // -----

    // from manual config
    public void genTraitSet(List<String> traitStrList) {
        traitsSet.clear();
        if (CollectionUtils.isNotEmpty(traitStrList)) {
            for (String traitStr : traitStrList) {
                tryAddConfTrait(Trait.findByName(traitStr));
            }
        }
    }
    // from internal data
    public void genTraitSetDirectly(List<String> traitStrList) {
        traitsSet.clear();
        if (CollectionUtils.isNotEmpty(traitStrList)) {
            for (String traitStr : traitStrList) {
                tryAddTrait(Trait.findByName(traitStr));
            }
        }
    }

    public List<String> genTraitStrList() {
        if (CollectionUtils.isEmpty(traitsSet)) return null;
        List<String> list = new ArrayList<>();
        for (Trait trait : traitsSet) {
            list.add(trait.name());
        }
        return list;
    }

    // -----

    public void copyFromTraits(Traits traits) {
        if (traits == null) return;
        traitsSet.clear();
        for (Trait trait : traits.getTraitsSet()) {
            tryAddConfTrait(trait);
        }
    }

    public void amendTraits(TraitsAmend traitsAmend) {
        if (traitsAmend == null) return;
        for (Trait trait : traitsAmend.getEnableTraits()) {
            tryAddConfTrait(trait);
        }
        for (Trait trait : traitsAmend.getDisableTraits()) {
            tryRemoveConfTrait(trait);
        }
    }

}
