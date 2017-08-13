package com.iwuyc.tools.commons.classtools.typeconverter;

public class Object2String implements TypeConverter<Object, String>
{

    @Override
    public String convert(Object from)
    {
        return String.valueOf(from);
    }

    @Override
    public boolean support(Class<?> target)
    {
        return String.class.equals(target);
    }

}
