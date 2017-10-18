package com.iwuyc.tools.commons.classtools.typeconverter;

import com.iwuyc.tools.commons.basic.MultiMap;
import com.iwuyc.tools.commons.thread.conf.typeconverter.String2TimeTupleConverter;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public class TypeConverterConstant {
    public static final MultiMap<Class<?>, TypeConverter<?, ?>> DEFAULT_CONVERTERS = new MultiMap<>();
    static {
        DEFAULT_CONVERTERS.add(String.class, new String2Number());
        DEFAULT_CONVERTERS.add(String.class, new String2TimeTupleConverter());
        DEFAULT_CONVERTERS.add(String.class, new String2Date());
        DEFAULT_CONVERTERS.add(Object.class, new Object2String());
    }
}
