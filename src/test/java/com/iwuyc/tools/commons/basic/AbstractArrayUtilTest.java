package com.iwuyc.tools.commons.basic;

import org.junit.Assert;
import org.junit.Test;

public class AbstractArrayUtilTest {

    @Test
    public void isNotEmpty() {
        int[] arr = null;
        Assert.assertTrue(AbstractArrayUtil.isEmpty(arr));
        arr = new int[0];
        Assert.assertTrue(AbstractArrayUtil.isEmpty(arr));
        arr = new int[1];
        Assert.assertFalse(AbstractArrayUtil.isEmpty(arr));
        Assert.assertTrue(AbstractArrayUtil.isNotEmpty(arr));

        Integer[] integers = null;
        Assert.assertTrue(AbstractArrayUtil.isEmpty(integers));
        integers = new Integer[0];
        Assert.assertTrue(AbstractArrayUtil.isEmpty(integers));
        integers = new Integer[1];
        Assert.assertFalse(AbstractArrayUtil.isEmpty(integers));
        Assert.assertTrue(AbstractArrayUtil.isNotEmpty(integers));

        Assert.assertEquals(0, AbstractArrayUtil.arrayLength(null));
        Assert.assertEquals(0, AbstractArrayUtil.arrayLength(new Integer[0]));
        Assert.assertEquals(1, AbstractArrayUtil.arrayLength(new Integer[1]));
    }

}