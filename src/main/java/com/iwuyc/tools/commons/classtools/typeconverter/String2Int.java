package com.iwuyc.tools.commons.classtools.typeconverter;

public class String2Int extends AbstractStringConverter<Integer>
{

    @Override
    protected Integer converterData(String from)
    {
        try
        {
            return Integer.parseInt(from);
        }
        catch (NumberFormatException e)
        {
            LOG.error("The number format was wrong,can't be translate it to integer.Data[{}]", from);
        }
        return null;
    }

    @Override
    protected boolean isSupport(Class<?> target)
    {
        return Integer.class.equals(target) || int.class.equals(target);
    }

}
