package com.ebay.magellan.tascreed.core.domain.define;

import com.ebay.magellan.tascreed.core.domain.define.conf.StepPackConf;
import com.ebay.magellan.tascreed.core.domain.define.conf.StepShardConf;
import com.ebay.magellan.tascreed.core.domain.trait.Trait;
import com.ebay.magellan.tascreed.core.domain.trait.Traits;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class StepDefineTest {

    @Test
    public void fromJson1() throws Exception {
        String str = "{\"stepName\":\"sample-aggr-2\",\"affinityRule\":\"lvs\",\"dependentStep\":\"sample-calc\",\"params\":{\"p1\":\"2\",\"p2\":\"4\"}}";
        StepDefine sd = StepDefine.fromJson(str);

        assertEquals("sample-aggr-2", sd.getStepName());
        assertNull(sd.getStepType());
        assertEquals("lvs", sd.getAffinityRule());
        assertEquals("sample-calc", sd.getDependentStep());

        Map<String, String> params = sd.getParams();
        assertNotNull(params);
        assertEquals(2, params.size());
        assertEquals("2", params.get("p1"));
        assertEquals("4", params.get("p2"));

        assertTrue(sd.isSimple());
        assertFalse(sd.isShard());
        assertFalse(sd.isPack());

        System.out.println(sd.toJson());
    }

    @Test
    public void fromJson2() throws Exception {
        String str = "{\"shardConf\":{\"shard\":4},\"stepName\":\"sample-prep\",\"stepType\":\"SHARD\"}";
        StepDefine sd = StepDefine.fromJson(str);

        assertEquals("sample-prep", sd.getStepName());
        assertEquals(StepTypeEnum.SHARD, sd.getStepType());

        StepShardConf conf = sd.getStepAllConf().getShardConf();
        assertNotNull(conf);
        assertEquals(Integer.valueOf(4), conf.getShard());

        assertFalse(sd.isSimple());
        assertTrue(sd.isShard());
        assertFalse(sd.isPack());

        System.out.println(sd.toJson());
    }

    @Test
    public void fromJson3() throws Exception {
        String str = "{\"packConf\":{\"size\":100,\"start\":0,\"end\":1005},\"stepName\":\"sample-calc\",\"stepType\":\"PACK\",\"dependentStep\":\"sample-prep\"}";
        StepDefine sd = StepDefine.fromJson(str);

        assertEquals("sample-calc", sd.getStepName());
        assertEquals(StepTypeEnum.PACK, sd.getStepType());
        assertEquals("sample-prep", sd.getDependentStep());

        StepPackConf conf = sd.getStepAllConf().getPackConf();
        assertNotNull(conf);
        assertEquals(Long.valueOf(100), conf.getSize());
        assertEquals(Long.valueOf(0), conf.getStart());
        assertEquals(Long.valueOf(1005), conf.getEnd());

        assertFalse(sd.isSimple());
        assertFalse(sd.isShard());
        assertTrue(sd.isPack());

        System.out.println(sd.toJson());
    }

    @Test
    public void fromJson4() throws Exception {
        String str = "{\"stepName\":\"test\",\"traits\": [\"canIgnore\",\"canFail\",\"deleted\",\"ARCHIVE\",\"unknown\"]}";
        StepDefine sd = StepDefine.fromJson(str);

        assertEquals("test", sd.getStepName());

        Traits traits = sd.getTraits();
        assertNotNull(traits);
        assertEquals(Trait.TraitType.STEP_DEFINE, traits.getTraitType());
        System.out.println(sd.getTraitStrList());
        System.out.println(traits.getTraitsSet());

        System.out.println(traits);
        assertTrue(sd.canIgnore());

        System.out.println(sd.toJson());

        String str1 = "{\"stepName\":\"test\"}";
        StepDefine sd1 = StepDefine.fromJson(str1);
        assertNull(sd1.getTraitStrList());
        assertNotNull(sd1.getTraits());
    }

}
