package com.ebay.magellan.tascreed.core.domain.trait;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TraitsTest {

    @Test
    public void trySetTrait() {
        Traits traits = new Traits(Trait.TraitType.JOB);
        traits.trySetTrait(Trait.DELETED, true);
        assertTrue(traits.containsTrait(Trait.DELETED));
        traits.trySetTrait(Trait.DELETED, false);
        assertFalse(traits.containsTrait(Trait.DELETED));
    }

    @Test
    public void genTraitSet() {
        Traits traits = new Traits(Trait.TraitType.STEP_DEFINE);
        List<String> list = new ArrayList<>();
        list.add(Trait.CAN_IGNORE.name());
        list.add(Trait.DELETED.name());
        list.add(Trait.CAN_FAIL.name());
        list.add(Trait.ARCHIVE.name());

        traits.genTraitSet(list);
        assertTrue(traits.containsTrait(Trait.CAN_IGNORE));
        assertFalse(traits.containsTrait(Trait.DELETED));
        assertTrue(traits.containsTrait(Trait.CAN_FAIL));
        assertTrue(traits.containsTrait(Trait.ARCHIVE));
        assertEquals(3, traits.genTraitStrList().size());

        System.out.println(traits);
    }

    @Test
    public void genTraitSetDirectly() {
        Traits traits = new Traits(Trait.TraitType.JOB);
        List<String> list = new ArrayList<>();
        list.add(Trait.CAN_IGNORE.name());
        list.add(Trait.DELETED.name());
        list.add(Trait.CAN_FAIL.name());
        list.add(Trait.ARCHIVE.name());

        traits.genTraitSetDirectly(list);
        assertFalse(traits.containsTrait(Trait.CAN_IGNORE));
        assertTrue(traits.containsTrait(Trait.DELETED));
        assertFalse(traits.containsTrait(Trait.CAN_FAIL));
        assertFalse(traits.containsTrait(Trait.ARCHIVE));
        assertEquals(1, traits.genTraitStrList().size());
    }

    @Test
    public void copyFromTraits() {
        Traits traits1 = new Traits(Trait.TraitType.STEP_DEFINE);
        List<String> list = new ArrayList<>();
        list.add(Trait.CAN_IGNORE.name());
        list.add(Trait.DELETED.name());
        list.add(Trait.CAN_FAIL.name());
        list.add(Trait.ARCHIVE.name());
        traits1.genTraitSet(list);

        Traits traits2 = new Traits(Trait.TraitType.STEP);
        traits2.copyFromTraits(traits1);

        assertFalse(traits2.containsTrait(Trait.CAN_IGNORE));
        assertFalse(traits2.containsTrait(Trait.DELETED));
        assertTrue(traits2.containsTrait(Trait.CAN_FAIL));
        assertTrue(traits2.containsTrait(Trait.ARCHIVE));
        assertEquals(2, traits2.genTraitStrList().size());
    }

    @Test
    public void amendTraits() {
        Traits traits1 = new Traits(Trait.TraitType.TASK);
        TraitsAmend ta1 = new TraitsAmend();
        List<String> list = new ArrayList<>();
        list.add(Trait.CAN_IGNORE.name());
        list.add(Trait.DELETED.name());
        list.add(Trait.CAN_FAIL.name());
        list.add(Trait.ARCHIVE.name());
        ta1.setEnableTraitStrList(list);

        traits1.amendTraits(ta1);
        assertFalse(traits1.containsTrait(Trait.CAN_IGNORE));
        assertFalse(traits1.containsTrait(Trait.DELETED));
        assertFalse(traits1.containsTrait(Trait.CAN_FAIL));
        assertTrue(traits1.containsTrait(Trait.ARCHIVE));
        assertEquals(1, traits1.genTraitStrList().size());

        TraitsAmend ta2 = new TraitsAmend();
        ta2.setDisableTraitStrList(list);

        traits1.amendTraits(ta2);
        assertFalse(traits1.containsTrait(Trait.CAN_IGNORE));
        assertFalse(traits1.containsTrait(Trait.DELETED));
        assertFalse(traits1.containsTrait(Trait.CAN_FAIL));
        assertFalse(traits1.containsTrait(Trait.ARCHIVE));
        assertNull(traits1.genTraitStrList());
    }

}
