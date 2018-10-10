package com.iwuyc.tools.commons.basic;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public abstract class AbstractStringUtils {

    /**
     * @param str 待判断的字符
     * @return 如果字符为null或者为""空字符，则返回true，否则返回false
     */
    public static boolean isEmpty(String str) {
        return null == str || str.isEmpty();
    }

    /**
     * isEmpty的取反
     *
     * @see com.iwuyc.tools.commons.basic.AbstractStringUtils#isEmpty(String)
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
