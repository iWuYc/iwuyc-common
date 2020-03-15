package com.iwuyc.tools.commons.util.string;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public abstract class StringUtils {

    public static final String NIL_STRING = "";
    public static final String EMPTY_STRING = " ";
    public static final char BLANK_CHAR = ' ';
    public static final String SYSTEM_NEW_LINE = System.getProperty("line.separator");
    public static final String LINUX_NEW_LINE = "\n";
    public static final String WINDOWS_NEW_LINE = "\r\n";

    /**
     * 回车
     */
    public static final String CR = "\r";
    /**
     * 换行
     */
    public static final String LF = "\n";

    /**
     * @param str 待判断的字符
     * @return 如果字符为null或者为""空字符，则返回true，否则返回false
     */
    public static boolean isEmpty(CharSequence str) {
        return null == str || str.length() == 0;
    }

    /**
     * isEmpty的取反
     *
     * @see StringUtils#isEmpty(CharSequence)
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    public static boolean equals(CharSequence primaryStr, CharSequence slaveStr) {
        if (primaryStr == null || slaveStr == null) {
            return primaryStr == slaveStr;
        }
        return primaryStr.equals(slaveStr);
    }

    public static boolean notEquals(CharSequence primaryStr, CharSequence slaveStr) {
        return !equals(primaryStr, slaveStr);
    }

    public static String turnFirstCharToLowerCase(String source) {
        if (isEmpty(source)) {
            return source;
        }
        char firstChar = source.charAt(0);
        if (firstChar < 'A' || firstChar > 'Z') {
            return source;
        }
        firstChar = (char) (firstChar - 'A' + 'a');
        return source.length() > 1 ? firstChar + source.substring(1) : String.valueOf(firstChar);
    }

    /**
     * 判断字符串是否为空字符串
     * ex:
     * str = ""; => true
     * str = " "; => true
     * str = null; => true
     * str = " a "; => false
     * <p>
     *
     * @param str 待检测的字符串
     * @return 如果为空字符串，则返回true，否则返回false
     */
    public static boolean isBlank(CharSequence str) {
        if (isEmpty(str)) {
            return true;
        }
        return str.chars().allMatch(item -> item == ' ');
    }

    /**
     * 判断字符串是否为空字符串，与 {@link #isBlank(CharSequence)} 方法结果相反
     *
     * @param str 待检测的字符串
     * @return 如果不为空字符串，则返回true，否则返回false
     */
    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    /**
     * 计算字符串的长度
     *
     * @param str 待计算的字符串
     * @return 返回字符串的长度，如果str为null则返回为0
     */
    public static int length(String str) {
        return str == null ? 0 : str.length();
    }

    /**
     * 判断字符串数组中是否存在空的字符串 {{@link #isEmpty(CharSequence)}}
     *
     * @param strs 待验证的字符串数组
     * @return 如果字符串数值中存在空字符串，则返回true，否则返回false
     */
    public static boolean isAnyEmpty(CharSequence... strs) {
        for (CharSequence str : strs) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串数组中是否不存在空的字符串 !{@link #isAnyEmpty(CharSequence...)}
     *
     * @param strs 待验证的字符串数组
     * @return 如果字符串数组中存在空字符串，则返回false，否则返回true
     */
    public static boolean isNonEmpty(CharSequence... strs) {
        return !isAnyEmpty(strs);
    }
}
