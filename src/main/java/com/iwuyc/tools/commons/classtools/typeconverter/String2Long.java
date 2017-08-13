package com.iwuyc.tools.commons.classtools.typeconverter;

public class String2Long extends AbstractStringConverter<Long>
{

    @Override
    protected Long converterData(String from)
    {
        try
        {
            return Long.parseLong(from);
        }
        catch (NumberFormatException e)
        {
            LOG.error("The number format was wrong,can't be translate it to long.Data[{}]", from);
        }
        return null;
    }

    @Override
    protected boolean isSupport(Class<?> target)
    {
        return Long.class.equals(target) || long.class.equals(target);
    }

}
