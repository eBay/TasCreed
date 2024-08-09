package com.ebay.magellan.tascreed.core.domain.task;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class WeightLabelTest {

    @Test
    public void testCompare() {
        WeightLabel wl1 = new WeightLabel(1, 1);
        WeightLabel wl2 = new WeightLabel(1, 5);
        WeightLabel wl3 = new WeightLabel(5, 1);
        WeightLabel wl4 = new WeightLabel(5, 5);

        List<WeightLabel> list = new ArrayList<>();
        list.add(wl4);
        list.add(wl3);
        list.add(wl1);
        list.add(wl2);
        System.out.println(list);

        List<WeightLabel> sorted = list.stream().sorted().collect(Collectors.toList());
        System.out.println(sorted);

        assertEquals(4, sorted.size());
        assertEquals(wl1, sorted.get(0));
        assertEquals(wl2, sorted.get(1));
        assertEquals(wl3, sorted.get(2));
        assertEquals(wl4, sorted.get(3));
    }
}
