package com.ebay.magellan.tumbler.core.domain.define.conf;

import org.junit.Test;

import static org.junit.Assert.*;

public class StepPackConfTest {

    @Test
    public void testClone() {
        StepPackConf conf = new StepPackConf();
        conf.setSize(10L);
        conf.setStart(0L);

        StepPackConf conf1 = StepPackConf.clone(conf);
        assertNotNull(conf1);
        assertTrue(conf != conf1);
        assertEquals(conf.getSize(), conf1.getSize());
        assertEquals(conf.getStart(), conf1.getStart());
        assertNull(conf1.getEnd());
    }

    @Test
    public void testMerge1() {
        StepPackConf conf1 = null;
        StepPackConf conf2 = null;

        StepPackConf conf3 = StepPackConf.merge(conf1, conf2);
        assertNull(conf3);
    }
    @Test
    public void testMerge2() {
        StepPackConf conf1 = null;
        StepPackConf conf2 = new StepPackConf();
        conf2.setSize(10L);
        conf2.setStart(0L);

        StepPackConf conf3 = StepPackConf.merge(conf1, conf2);
        assertNotNull(conf3);
        assertTrue(conf3 != conf1);
        assertTrue(conf3 != conf2);
        assertEquals(conf3.getSize(), conf2.getSize());
        assertEquals(conf3.getStart(), conf2.getStart());
        assertNull(conf3.getEnd());
    }
    @Test
    public void testMerge3() {
        StepPackConf conf1 = new StepPackConf();
        conf1.setSize(10L);
        conf1.setStart(0L);
        StepPackConf conf2 = null;

        StepPackConf conf3 = StepPackConf.merge(conf1, conf2);
        assertNotNull(conf3);
        assertTrue(conf3 != conf1);
        assertTrue(conf3 != conf2);
        assertEquals(conf3.getSize(), conf1.getSize());
        assertEquals(conf3.getStart(), conf1.getStart());
        assertNull(conf3.getEnd());
    }
    @Test
    public void testMerge4() {
        StepPackConf conf1 = new StepPackConf();
        conf1.setSize(10L);
        conf1.setStart(0L);
        StepPackConf conf2 = new StepPackConf();
        conf2.setSize(20L);
        conf2.setEnd(100L);

        StepPackConf conf3 = StepPackConf.merge(conf1, conf2);
        assertNotNull(conf3);
        assertTrue(conf3 != conf1);
        assertTrue(conf3 != conf2);
        assertEquals(conf3.getSize(), conf2.getSize());
        assertEquals(conf3.getStart(), conf1.getStart());
        assertEquals(conf3.getEnd(), conf2.getEnd());
    }
}
