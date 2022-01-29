package com.iwuyc.tools.commons.classtools;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class ClassUtilsTest {

    @Test
    public void name() {
        Assert.assertTrue(ScheduledExecutorService.class.isAssignableFrom(ScheduledThreadPoolExecutor.class));
    }

    @Test
    public void test1() {
        Parameter1 p1 = new Parameter1();
        Parameter2 p2 = new Parameter2();
        TestClass tc = ClassUtils.instance(TestClass.class, TestClass.class.getName(), p1, p2);
        Assert.assertEquals(p1, tc.p1);
        Assert.assertEquals(p2, tc.p2);
    }

    @Test
    public void testInstance() {
        Object num = 10;
        TestInstanceCase obj = ClassUtils.instance(TestInstanceCase.class, num);
        Assert.assertEquals(num, obj.num);
        obj = ClassUtils.instance(TestInstanceCase.class, new Object[]{"String"});
        Assert.assertEquals(0, obj.num);
    }

    @Test
    public void testCallMethod() {
        final Object result = ClassUtils.callMethod(TestInstanceCase.class, "staticMethod");
        Assert.assertNull(result);
        Assert.assertTrue(TestInstanceCase.callStaticMethodWithoutParam.get());
        final String parameter = "parameter";
        ClassUtils.callMethod(TestInstanceCase.class, "staticMethod", parameter);
        Assert.assertEquals(parameter, TestInstanceCase.callStaticMethodWithString.get());

        final Object obj = new Object();
        ClassUtils.callMethod(TestInstanceCase.class, "staticMethod", obj);
        Assert.assertEquals(obj, TestInstanceCase.callStaticMethodWithObject.get());
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
    public void getLambdaMethodNamePerformance() {
        for (int i = 0; i < 10; i++) {
            System.out.println(ClassUtils.getLambdaMethodName(A::getName));
        }
    }

    @Test
    public void getLambdaMethodName() {
        Assert.assertEquals("getName", ClassUtils.getLambdaMethodName(A::getName));
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
        public static AtomicBoolean callStaticMethodWithoutParam = new AtomicBoolean(false);
        public static AtomicReference<String> callStaticMethodWithString = new AtomicReference<>();
        public static AtomicReference<Object> callStaticMethodWithObject = new AtomicReference<>();
        private final int num;

        private TestInstanceCase(int num) {
            this.num = num;
        }

        private TestInstanceCase(Object num) {
            this.num = 0;
        }

        public static void staticMethod() {
            callStaticMethodWithoutParam.set(true);
        }

        public static void staticMethod(String parameter) {
            callStaticMethodWithString.set(parameter);
        }

        public static void staticMethod(Object parameter) {
            callStaticMethodWithObject.set(parameter);
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
