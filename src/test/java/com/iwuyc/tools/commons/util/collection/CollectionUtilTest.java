package com.iwuyc.tools.commons.util.collection;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CollectionUtilTest {

    @SuppressWarnings("All")
    @Test
    public void isEmpty() {
        Collection<Object> coll = null;
        assertTrue(CollectionUtil.isEmpty(coll));
        coll = new ArrayList<>();
        assertTrue(CollectionUtil.isEmpty(coll));
        coll.add(new Object());
        assertFalse(CollectionUtil.isEmpty(coll));

        assertTrue(CollectionUtil.isNotEmpty(coll));

        System.out.println(CollectionUtil.class.getName());
    }

    @Test
    public void join() {
        final List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        String joinStr = CollectionUtil.join(integers);
        assertEquals("1,2,3,4,5,6,7", joinStr);

        joinStr = CollectionUtil.join(integers, ";");
        assertEquals("1;2;3;4;5;6;7", joinStr);

        joinStr = CollectionUtil.join(Collections.emptyList());
        assertEquals("", joinStr);

    }

    @Test
    public void sizeOf() {
        final List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        final int size = CollectionUtil.sizeOf(integers);
        assertEquals(7, size);
        assertEquals(0, CollectionUtil.sizeOf(null));
        assertEquals(0, CollectionUtil.sizeOf(Collections.emptyList()));
    }

    @Test
    public void split() {
        final List<Integer> integers = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        List<List<Integer>> split = CollectionUtil.split(integers, 5);
        assertEquals(2, split.size());
        split = CollectionUtil.split(integers, 3);
        assertEquals(4, split.size());

        split = CollectionUtil.split(Collections.emptyList(), 3);
        assertEquals(0, split.size());
    }
}