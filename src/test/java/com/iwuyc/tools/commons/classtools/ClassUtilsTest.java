package com.iwuyc.tools.commons.classtools;

import lombok.Data;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ClassUtilsTest {

    @Test
    public void name() {
        System.out.println(ScheduledExecutorService.class.isAssignableFrom(ScheduledThreadPoolExecutor.class));
    }

    @Test
    public void test1() {
        Parameter1 p1 = new Parameter1();
        Parameter2 p2 = new Parameter2();
        TestClass tc = ClassUtils.instance(TestClass.class, TestClass.class.getName(), p1, p2);
        System.out.println(tc.print());
    }

    @Test
    public void testInstance() {
        Object num = 10;
        Object obj = ClassUtils.instance(TestInstanceCase.class, num);
        System.out.println(obj);
        obj = ClassUtils.instance(TestInstanceCase.class, new Object[]{"String"});
        System.out.println(obj);
    }

    @Test
    public void testCallMethod() {
        ClassUtils.callMethod(TestInstanceCase.class, "staticMethod");
        ClassUtils.callMethod(TestInstanceCase.class, "staticMethod", "parameter");

    }

    @Test
    public void testCompareType() {
        assert ClassUtils.compareType(Object.class, TestClassCase.class, true);
        assert !ClassUtils.compareType(Object.class, TestClassCase.class, false);
    }

    @Test
    public void compareType() {
        Assert.assertTrue(ClassUtils.compareType(B.class, I.class, true));
        Assert.assertFalse(ClassUtils.compareType(null, I.class, true));
        Assert.assertFalse(ClassUtils.compareType(B.class, null, true));
        Assert.assertTrue(ClassUtils.compareType(null, null, true));

        Assert.assertTrue(ClassUtils.compareType(int.class, Integer.class, true));
        Assert.assertTrue(ClassUtils.compareType(Integer.class, int.class, true));

    }

    @Test
    @Ignore("Performance Test skip it.")
    public void getLambdaMethodName() {
        for (int i = 0; i < 10; i++) {
            System.out.println(ClassUtils.getLambdaMethodName(A::getName));
        }
    }

    interface I {
    }

    public static class TestClass {
        private final Parameter1 p1;
        private final Parameter2 p2;

        public TestClass(Parameter1 p1, Parameter2 p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        public String print() {
            return this.p1.get() + " : " + this.p2.get();
        }

    }

    public static class Parameter1 {
        public String get() {
            return "parameter1";
        }
    }

    public static class Parameter2 {
        public String get() {
            return "parameter2";
        }

    }

    @Data
    public static class TestClassCase {
        public String name;

        public TestClassCase(String name) {
            super();
            this.name = name;
        }

        @Override
        public String toString() {
            return "TestClassCase [name=" + name + "]";
        }

    }

    static class TestInstanceCase {
        private final int num;

        private TestInstanceCase(int num) {
            this.num = num;
            System.out.println("Integer constructor.");
        }

        private TestInstanceCase(Object num) {
            this.num = 0;
            System.out.println("Object constructor.");
        }

        public static void staticMethod() {
            System.out.println("Static method without parameters.");
        }

        public static void staticMethod(String parameter) {
            System.out.println("Parameter:" + parameter);
        }

        public static void staticMethod(Object parameter) {
            System.out.println("Parameter object:" + parameter);
        }

        @Override
        public String toString() {
            return "TestInstanceCase [num=" + num + "]";
        }

        public String toString(String friend) {
            return "TestInstanceCase [num=" + num + ",friend=" + friend + "]";
        }
    }

    @Data
    public static class A {
        private String name;

        public String getName() {
            return this.name;
        }
    }

    static class B extends A implements I {
    }

}
