package com.iwuyc.tools.commons.classtools.typeconverter;

public class String2Int implements TypeConverter<String, Integer>
{

    @Override
    public Integer convert(String from)
    {
        return Integer.parseInt(from);
    }

    @Override
    public boolean support(Class<?> target)
    {
        return Integer.class.equals(target) || int.class.equals(target);
    }

}
