package com.ebay.magellan.tascreed.core.domain.define.conf;

import org.junit.Test;

import static org.junit.Assert.*;

public class StepShardConfTest {

    @Test
    public void testClone() {
        StepShardConf conf = new StepShardConf();
        conf.setShard(12);

        StepShardConf conf1 = StepShardConf.clone(conf);
        assertNotNull(conf1);
        assertTrue(conf != conf1);
        assertEquals(conf.getShard(), conf1.getShard());
    }

    @Test
    public void testMerge1() {
        StepShardConf conf1 = null;
        StepShardConf conf2 = null;

        StepShardConf conf3 = StepShardConf.merge(conf1, conf2);
        assertNull(conf3);
    }
    @Test
    public void testMerge2() {
        StepShardConf conf1 = null;
        StepShardConf conf2 = new StepShardConf();
        conf2.setShard(4);

        StepShardConf conf3 = StepShardConf.merge(conf1, conf2);
        assertNotNull(conf3);
        assertTrue(conf3 != conf1);
        assertTrue(conf3 != conf2);
        assertEquals(conf3.getShard(), conf2.getShard());
    }
    @Test
    public void testMerge3() {
        StepShardConf conf1 = new StepShardConf();
        conf1.setShard(12);
        StepShardConf conf2 = null;

        StepShardConf conf3 = StepShardConf.merge(conf1, conf2);
        assertNotNull(conf3);
        assertTrue(conf3 != conf1);
        assertTrue(conf3 != conf2);
        assertEquals(conf3.getShard(), conf1.getShard());
    }
    @Test
    public void testMerge4() {
        StepShardConf conf1 = new StepShardConf();
        conf1.setShard(12);
        StepShardConf conf2 = new StepShardConf();
        conf2.setShard(4);

        StepShardConf conf3 = StepShardConf.merge(conf1, conf2);
        assertNotNull(conf3);
        assertTrue(conf3 != conf1);
        assertTrue(conf3 != conf2);
        assertEquals(conf3.getShard(), conf2.getShard());
    }
}
