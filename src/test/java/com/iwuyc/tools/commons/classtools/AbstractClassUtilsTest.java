package com.iwuyc.tools.commons.classtools;

import com.iwuyc.tools.commons.basic.AbstractStringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.util.Optional;

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
        private String name;

        public String getName() {
            return this.name;
        }
    }

    static class B extends A implements I {
    }

    interface Compare extends Serializable {
        boolean compareType(Class<?> firstType, Class<?> another, boolean isAssignale);
    }

    @Test(expected = Exception.class)
    public void getLambdaMethodName() {
        System.out.println(AbstractClassUtils.getLambdaMethodName(A::getName));
        Compare compare = AbstractClassUtils::compareType;
        Optional<SerializedLambda> serializedLambda = AbstractClassUtils.getSerializedLambda(compare);
        System.out.println(serializedLambda);
    }
}