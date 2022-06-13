package com.iwuyc.tools.commons.basic;

import com.iwuyc.tools.commons.util.collection.ArrayUtil;
import org.junit.Assert;
import org.junit.Test;

public class ArrayUtilTest {

    @Test
    public void isNotEmpty() {
        int[] arr = null;
        Assert.assertTrue(ArrayUtil.isEmpty(arr));
        arr = new int[0];
        Assert.assertTrue(ArrayUtil.isEmpty(arr));
        arr = new int[1];
        Assert.assertFalse(ArrayUtil.isEmpty(arr));
        Assert.assertTrue(ArrayUtil.isNotEmpty(arr));

        Integer[] integers = null;
        Assert.assertTrue(ArrayUtil.isEmpty(integers));
        integers = new Integer[0];
        Assert.assertTrue(ArrayUtil.isEmpty(integers));
        integers = new Integer[1];
        Assert.assertFalse(ArrayUtil.isEmpty(integers));
        Assert.assertTrue(ArrayUtil.isNotEmpty(integers));

        Assert.assertEquals(0, ArrayUtil.arrayLength((Object[]) null));
        Assert.assertEquals(0, ArrayUtil.arrayLength(new Integer[0]));
        Assert.assertEquals(1, ArrayUtil.arrayLength(new Integer[1]));
    }

}