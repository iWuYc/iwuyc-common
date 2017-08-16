package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.classtools.ClassUtils;

public abstract class NumberUtil
{
    public static boolean isByteClass(Class<?> target)
    {
        return Byte.class.equals(target) || byte.class.equals(target);
    }

    public static boolean isIntegerClass(Class<?> target)
    {
        return Integer.class.equals(target) || int.class.equals(target);
    }

    public static boolean isLongClass(Class<?> target)
    {
        return Long.class.equals(target) || long.class.equals(target);
    }

    public static boolean isFloatClass(Class<?> target)
    {
        return Float.class.equals(target) || float.class.equals(target);
    }

    public static boolean isDoubleClass(Class<?> target)
    {
        return Double.class.equals(target) || double.class.equals(target);
    }

    public static boolean isNumberClass(Class<?> target)
    {
        return target.isAssignableFrom(Number.class) || (target.isPrimitive() && target != Void.TYPE);
    }

    /**
     * 将字符串类型转换成目标的数字类型
     * 
     * @param numberFormat
     * @param target
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T parse(String numberFormat, Class<T> target)
    {
        Number result = null;
        if (isByteClass(target))
        {
            result = Byte.parseByte(numberFormat);
        }
        else if (isIntegerClass(target))
        {
            result = Integer.parseInt(numberFormat);
        }
        else if (isLongClass(target))
        {
            result = Long.parseLong(numberFormat);
        }
        else if (isFloatClass(target))
        {
            result = Float.parseFloat(numberFormat);
        }
        else if (isDoubleClass(target))
        {
            result = Double.parseDouble(numberFormat);
        }
        else
        {
            result = ClassUtils.instance(Number.class, target, numberFormat);
            if (null == result)
                throw new UnsupportedOperationException("The target type unsupport.Target type:" + target);
        }
        return (T) result;
    }
}
