package com.iwuyc.tools.commons.classtools;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

public class AbstractClassUtilsTest {
    @Test
    public void test() {
        Field field = AbstractClassUtils.findField(String.class, "valueOf");
        System.out.println(field);
    }

    @Test
    public void compareType() {
        Assert.assertTrue(AbstractClassUtils.compareType(B.class, I.class, true));
        Assert.assertFalse(AbstractClassUtils.compareType(null, I.class, true));
        Assert.assertFalse(AbstractClassUtils.compareType(B.class, null, true));
        Assert.assertTrue(AbstractClassUtils.compareType(null, null, true));

        Assert.assertTrue(AbstractClassUtils.compareType(int.class, Integer.class, true));
        Assert.assertTrue(AbstractClassUtils.compareType(Integer.class, int.class, true));

    }

    interface I {
    }

    static class A {
    }

    static class B extends A implements I {
    }
}