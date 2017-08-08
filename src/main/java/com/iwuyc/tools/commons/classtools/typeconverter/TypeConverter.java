package com.iwuyc.tools.commons.classtools.typeconverter;

public interface TypeConverter<F, T>
{
    T convert(F from);

    boolean support(Class<?> target);

}
