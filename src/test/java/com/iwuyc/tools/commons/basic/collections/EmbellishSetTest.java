package com.iwuyc.tools.commons.basic.collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.ConcurrentHashMap;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmbellishSetTest {

    private EmbellishSet<String> stringSet = new EmbellishSet<>(new ConcurrentHashMap<>());

    @Before
    public void setUp() {
        Assert.assertTrue(stringSet.add("one"));
        Assert.assertTrue(stringSet.add("two"));
        Assert.assertFalse(stringSet.add("one"));
    }

    @Test
    @Order(1)
    public void size() {
        Assert.assertEquals(2, stringSet.size());
    }

    @Test
    @Order(2)
    public void contains() {
        Assert.assertTrue(stringSet.contains("one"));
        Assert.assertFalse(stringSet.contains("three"));
    }

    @Test
    @Order(3)
    public void iterator() {
        for (String item : stringSet) {
            Assert.assertNotNull(item);
        }
    }

    @Test
    @Order(Integer.MIN_VALUE)
    public void add() {
        stringSet.clear();
        Assert.assertTrue(stringSet.add("one"));
        Assert.assertTrue(stringSet.add("two"));
        Assert.assertFalse(stringSet.add("one"));
    }

    @Test
    @Order(4)
    public void remove() {
        Assert.assertFalse(stringSet.remove("three"));
        Assert.assertTrue(stringSet.remove("two"));
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void clear() {
        Assert.assertFalse(stringSet.isEmpty());
        stringSet.clear();
    }
}