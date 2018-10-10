package com.iwuyc.tools.commons.basic;

import org.junit.Before;
import org.junit.Test;

public class MultiMapTest {
    private MultiMap<String, String> map = new MultiMap<>();

    @Before
    public void setUp() {
        map.add("1", "10");
    }

    @Test
    public void add() {
        map.add("1", "11");
        System.out.println(map);
    }

    @Test
    public void addFirst() {
        map.addFirst("1", "12");
        System.out.println(map);
    }
}