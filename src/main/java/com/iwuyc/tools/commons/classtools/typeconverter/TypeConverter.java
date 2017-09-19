package com.iwuyc.tools.commons.classtools.typeconverter;

public interface TypeConverter<F, T> {
    T convert(F from, Class<? extends T> targetType);

    boolean support(Class<?> target);

}
