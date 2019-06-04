package com.iwuyc.tools.commons.basic;

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
    public static boolean isEmpty(CharSequence str){
        return null == str || str.length() == 0;
    }

    /**
     * isEmpty的取反
     *
     * @see StringUtils#isEmpty(CharSequence)
     */
    public static boolean isNotEmpty(CharSequence str){
        return !isEmpty(str);
    }

    public static boolean equals(CharSequence primaryStr, CharSequence slaveStr){
        if(primaryStr == null || slaveStr == null){
            return primaryStr == slaveStr;
        }
        return primaryStr.equals(slaveStr);
    }

    public static String turnFirstCharToLowerCase(String source){
        if(isEmpty(source)){
            return source;
        }
        char firstChar = source.charAt(0);
        if(firstChar < 'A' || firstChar > 'Z'){
            return source;
        }
        firstChar = (char)(firstChar - 'A' + 'a');
        return source.length() > 1 ? firstChar + source.substring(1) : String.valueOf(firstChar);
    }

    public static boolean isBlank(CharSequence str){
        if(isEmpty(str)){
            return true;
        }
        return str.chars().allMatch(item -> item == ' ');
    }

    public static boolean isNotBlank(CharSequence str){
        return !isBlank(str);
    }
}
