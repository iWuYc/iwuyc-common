package com.iwuyc.tools.commons.classtools;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.iwuyc.tools.commons.basic.MultiMap;
import com.iwuyc.tools.commons.classtools.typeconverter.TypeConverter;

public class ClassUtilsTest
{

    @Test
    public void test()
    {
        Parameter1 p1 = new Parameter1();
        Parameter2 p2 = new Parameter2();
        TestClass tc = ClassUtils.instance(TestClass.class, TestClass.class.getName(), p1, p2);
        System.out.println(tc.print());
    }

    public static class TestClass
    {
        private Parameter1 p1;
        private Parameter2 P2;

        public TestClass(Parameter1 p1, Parameter2 p2)
        {
            this.p1 = p1;
            P2 = p2;
        }

        public String print()
        {
            return this.p1.get() + " : " + this.P2.get();
        }

    }

    public static class Parameter1
    {
        public String get()
        {
            return "parameter1";
        }
    }

    public static class Parameter2
    {
        public String get()
        {
            return "parameter2";
        }

    }

    @Test
    public void testModifiers()
    {
        Field nameField = ClassUtils.findField(TestClassCase.class, "name");
        System.out.println(nameField);
        TestClassCase testClazz = new TestClassCase("Tom");
        Map<String, Object> fieldAndVal = new HashMap<>();
        fieldAndVal.put("name", "Jack");

        MultiMap<Class<? extends Object>, TypeConverter<? extends Object, ? extends Object>> typeConverters = new MultiMap<>();

        ClassUtils.injectFields(testClazz, fieldAndVal, typeConverters);
        System.out.println(testClazz);
    }

    public static class TestClassCase
    {
        public final String name;

        public TestClassCase(String name)
        {
            super();
            this.name = name;
        }

        public String getName()
        {
            return name;
        }

    }

}
