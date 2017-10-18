package com.iwuyc.tools.commons.classtools.typeconverter;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public class Object2String implements TypeConverter<Object, String> {

    @Override
    public String convert(Object from, Class<? extends String> target) {
        return String.valueOf(from);
    }

    @Override
    public boolean support(Class<?> target) {
        return String.class.equals(target);
    }

}
