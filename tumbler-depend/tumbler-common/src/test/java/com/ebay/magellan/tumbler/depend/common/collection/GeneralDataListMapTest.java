package com.ebay.magellan.tumbler.depend.common.collection;

import org.junit.Before;
import org.junit.Test;
import java.util.*;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class GeneralDataListMapTest {

    GeneralDataListMap listMap = new GeneralDataListMap<String, String>();

    @Before
    public void init() {
        listMap.clear();
    }

    @Test
    public void testAppend() {
        listMap.append("k1", "v11");
        listMap.append("k1", "v12");
        listMap.append("k1", "v12");
        listMap.append("k2", "v21");
        listMap.append("k2", "v22");

        Set<String> ks = listMap.keySet();
        assertThat(ks, hasItems("k1", "k2"));
        assertThat(listMap.get("k1"), is(Arrays.asList("v11", "v12", "v12")));
        assertThat(listMap.get("k2"), is(Arrays.asList("v21", "v22")));
    }

    @Test
    public void testAppendAll() {
        listMap.appendAll("k1", Arrays.asList("v11", "v12"));
        listMap.appendAll("k1", Arrays.asList("v11", "v13"));

        Set<String> ks = listMap.keySet();
        assertThat(ks, hasItems("k1"));
        assertThat(listMap.get("k1"), is(Arrays.asList("v11", "v12", "v11", "v13")));
    }

    @Test
    public void testPutAndRemove() {
        listMap.put("k1", Arrays.asList("v11", "v12"));
        Set<String> ks = listMap.keySet();
        assertThat(ks, hasItems("k1"));
        assertThat(listMap.get("k1"), is(Arrays.asList("v11", "v12")));

        listMap.remove("k1");
        ks = listMap.keySet();
        assertThat(ks, not(hasItems("k1")));
        assertThat(listMap.get("k1"), nullValue());
    }

    @Test
    public void testGetFlatList() {
        listMap.append("k1", "v11");
        listMap.append("k1", "v12");
        listMap.append("k2", "v21");
        listMap.append("k2", "v22");

        List<String> list = listMap.getFlatList();
        assertThat(list, hasItems("v11", "v12", "v21", "v22"));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(listMap.isEmpty());

        listMap.append("k1", "v11");
        assertFalse(listMap.isEmpty());
    }

}
