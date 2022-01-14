package com.iwuyc.tools.commons.util.collection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ConstantConditions")
public class ArrayUtilTest {

    @Test
    public void isNotEmpty() {
        int[] arr = null;
        assertTrue(ArrayUtil.isEmpty(arr));
        arr = new int[0];
        assertTrue(ArrayUtil.isEmpty(arr));
        arr = new int[1];
        assertFalse(ArrayUtil.isEmpty(arr));
        assertTrue(ArrayUtil.isNotEmpty(arr));

        Integer[] integers = null;
        assertTrue(ArrayUtil.isEmpty(integers));
        integers = new Integer[0];
        assertTrue(ArrayUtil.isEmpty(integers));
        integers = new Integer[1];
        assertFalse(ArrayUtil.isEmpty(integers));
        assertTrue(ArrayUtil.isNotEmpty(integers));

        assertEquals(0, ArrayUtil.arrayLength((Object[]) null));
        assertEquals(0, ArrayUtil.arrayLength(new Integer[0]));
        assertEquals(1, ArrayUtil.arrayLength(new Integer[1]));
    }

    @Test
    public void longArr() {
        assertFalse(ArrayUtil.isNotEmpty((long[]) null));
        assertFalse(ArrayUtil.isNotEmpty(new long[]{}));
        assertTrue(ArrayUtil.isNotEmpty(new long[]{1, 2, 3}));
        assertEquals(3, ArrayUtil.arrayLength(new long[]{1, 2, 3}));
    }

    @Test
    public void shortArr() {
        assertFalse(ArrayUtil.isNotEmpty((short[]) null));
        assertFalse(ArrayUtil.isNotEmpty(new short[]{}));
        assertTrue(ArrayUtil.isNotEmpty(new short[]{1, 2, 3}));
    }

    @Test
    public void byteArr() {
        assertFalse(ArrayUtil.isNotEmpty((byte[]) null));
        assertFalse(ArrayUtil.isNotEmpty(new byte[]{}));
        assertTrue(ArrayUtil.isNotEmpty(new byte[]{1, 2, 3}));
    }

    @Test
    public void floatArr() {
        assertFalse(ArrayUtil.isNotEmpty((float[]) null));
        assertFalse(ArrayUtil.isNotEmpty(new float[]{}));
        assertTrue(ArrayUtil.isNotEmpty(new float[]{1, 2, 3}));
    }

    @Test
    public void doubleArr() {
        assertFalse(ArrayUtil.isNotEmpty((double[]) null));
        assertFalse(ArrayUtil.isNotEmpty(new double[]{}));
        assertTrue(ArrayUtil.isNotEmpty(new double[]{1, 2, 3}));
    }

    @Test
    public void charArr() {
        assertFalse(ArrayUtil.isNotEmpty((char[]) null));
        assertFalse(ArrayUtil.isNotEmpty(new char[]{}));
        assertTrue(ArrayUtil.isNotEmpty(new char[]{1, 2, 3}));
    }

    @Test
    public void booleanArr() {
        assertFalse(ArrayUtil.isNotEmpty((boolean[]) null));
        assertFalse(ArrayUtil.isNotEmpty(new boolean[]{}));
        assertTrue(ArrayUtil.isNotEmpty(new boolean[]{true, true, false}));
    }
}