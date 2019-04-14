package com.iwuyc.tools.commons.basic;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public abstract class StringUtils {
    public static final String NIL_STRING = "";
    public static final String EMPTY_STRING = " ";
    public static final char BLANK_CHAR = ' ';
    public static final String NEW_LINE = System.getProperty("line.separator");

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
     * @see StringUtils#isEmpty(String)
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean equals(String primaryStr, String slaveStr) {
        if (primaryStr == null || slaveStr == null) {
            return false;
        }
        return primaryStr.equals(slaveStr);
    }

    public static String turnFirstCharToLowerCase(String source) {
        if (isEmpty(source)) {
            return source;
        }
        char firstChar = source.charAt(0);
        if (firstChar < 'A' || firstChar > 'Z') {
            return source;
        }
        firstChar = (char)(firstChar - 'A' + 'a');
        return source.length() > 1 ? firstChar + source.substring(1) : String.valueOf(firstChar);
    }
}
