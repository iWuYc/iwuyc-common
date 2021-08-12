package com.iwuyc.tools.commons.basic.collections;

import org.junit.Assert;
import org.junit.Test;

public class ConcurrentHashSetTest {

    @Test
    public void add() {
        final ConcurrentHashSet<String> stringSet = new ConcurrentHashSet<>();
        Assert.assertTrue(stringSet.add("one"));
        Assert.assertTrue(stringSet.add("two"));
        Assert.assertFalse(stringSet.add("one"));

    }
}