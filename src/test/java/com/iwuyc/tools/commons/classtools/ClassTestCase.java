package com.iwuyc.tools.commons.classtools;

import org.junit.Test;

import java.lang.reflect.Constructor;

public class ClassTestCase {

    static class TestClass {

        public TestClass(Object obj) {
            System.out.println("Object");
        }

        public TestClass(Parameter2 obj) {
            System.out.println("Parameter2");
        }

        public TestClass(Parameter obj) {
        }

        public TestClass(Parameter1 obj) {
            System.out.println("Parameter1");
        }

        // public TestClass(Parameter3 obj) {
        // System.out.println("Parameter3");
        // }
        public static void staticMethod() {
            System.out.println("staticMethod:none Parameter");
        }

        public static void staticMethod(Object parameter) {
            System.out.println("staticMethod Object:" + parameter);
        }

        public static void staticMethod(Parameter parameter) {
            System.out.println("staticMethod Parameter:" + parameter);
        }

        public static void staticMethod(Parameter1 parameter) {
            System.out.println("staticMethod Parameter1:" + parameter);
        }

        public static void staticMethod(Parameter2 parameter) {
            System.out.println("staticMethod Parameter2:" + parameter);
        }
    }

    static class Parameter {
    }

    static class Parameter1 extends Parameter {

    }

    static class Parameter2 extends Parameter1 {

    }

    static class Parameter3 extends Parameter2 {

    }

    @Test
    public void test() {
        TestClass clazz = new TestClass(new Parameter3());
        clazz = new TestClass(new Parameter2());
        clazz = new TestClass(new Parameter1());
        clazz = new TestClass(new Parameter());
        clazz = new TestClass(new Object());
        System.out.println(clazz);
    }

    @Test
    public void testInstance() {
        AbstractClassUtils.instance(TestClass.class, new Parameter3());
        AbstractClassUtils.instance(TestClass.class, new Parameter2());
        AbstractClassUtils.instance(TestClass.class, new Parameter1());
        AbstractClassUtils.instance(TestClass.class, new Parameter());
        AbstractClassUtils.instance(TestClass.class, new Object());
    }

    @Test(expected = NoSuchMethodException.class)
    public void testConstructor() throws Exception {
        Class<TestClass> clazz = TestClass.class;
        Constructor<TestClass> constructor = clazz.getDeclaredConstructor(Parameter3.class);
        System.out.println(constructor);
    }

    @Test
    public void testCallStaticMethod() {
        AbstractClassUtils.callMethod(TestClass.class, "staticMethod", "String");
        AbstractClassUtils.callMethod(TestClass.class, "staticMethod", new Parameter3());
        AbstractClassUtils.callMethod(TestClass.class, "staticMethod", new Parameter1());
        AbstractClassUtils.callMethod(TestClass.class, "staticMethod", new Parameter2());
        AbstractClassUtils.callMethod(TestClass.class, "staticMethod", new Parameter());
        AbstractClassUtils.callMethod(TestClass.class, "staticMethod");
    }
}
