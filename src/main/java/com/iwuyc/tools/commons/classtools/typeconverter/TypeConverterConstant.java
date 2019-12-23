package com.iwuyc.tools.commons.classtools.typeconverter;

import com.iwuyc.tools.commons.util.collection.MultiMap;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public class TypeConverterConstant {
    public static final MultiMap<Class<?>, TypeConverter<?, ?>> DEFAULT_CONVERTERS = new MultiMap<>();

    static {
        DEFAULT_CONVERTERS.add(String.class, new String2NumberConverter());
        DEFAULT_CONVERTERS.add(String.class, new String2TimeTupleConverter());
        DEFAULT_CONVERTERS.add(String.class, new String2DateConverter());
        DEFAULT_CONVERTERS.add(Object.class, new Object2StringConverter());
    }
}
