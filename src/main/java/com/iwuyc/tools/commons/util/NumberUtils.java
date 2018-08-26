package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.classtools.AbstractClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public abstract class NumberUtils {

    public static boolean isByteClass(Class<?> target) {
        return Byte.class.equals(target) || byte.class.equals(target);
    }

    public static boolean isIntegerClass(Class<?> target) {
        return Integer.class.equals(target) || int.class.equals(target);
    }

    public static boolean isLongClass(Class<?> target) {
        return Long.class.equals(target) || long.class.equals(target);
    }

    public static boolean isFloatClass(Class<?> target) {
        return Float.class.equals(target) || float.class.equals(target);
    }

    public static boolean isDoubleClass(Class<?> target) {
        return Double.class.equals(target) || double.class.equals(target);
    }

    /**
     * 判断类是否为数字类型
     * 
     * @param target
     *            待判断的类对象
     * @return 如果是数字类型，则返回true，否则返回false。
     */
    public static boolean isNumberClass(Class<?> target) {
        if (null == target) {
            return false;
        }
        return target.isAssignableFrom(Number.class) || (target.isPrimitive() && target != Void.TYPE);
    }

    /**
     * 判断是否是数字。
     * 
     * @param object
     *            待判断的对象。
     * @return 如果是数字，则返回true，否则返回false。
     */
    public static boolean isNumber(Object object) {
        return isNumberClass(object.getClass());
    }

    /**
     * 将字符串类型转换成目标的数字类型
     * 
     * @param numberFormat
     * @param target
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T parse(String numberFormat, Class<T> target) {
        Number result = null;
        if (isByteClass(target)) {
            result = Byte.parseByte(numberFormat);
        }
        else if (isIntegerClass(target)) {
            result = Integer.parseInt(numberFormat);
        }
        else if (isLongClass(target)) {
            result = Long.parseLong(numberFormat);
        }
        else if (isFloatClass(target)) {
            result = Float.parseFloat(numberFormat);
        }
        else if (isDoubleClass(target)) {
            result = Double.parseDouble(numberFormat);
        }
        else {
            // 如果是其他类型，则该类型必须要有String作为入参的构造函数。如：BigDecimal、BigInteger
            result = AbstractClassUtils.instance(Number.class, target, numberFormat);
            if (null == result) {
                throw new UnsupportedOperationException("The target type unsupport.Target type:" + target);
            }
        }
        return (T) result;
    }

}
