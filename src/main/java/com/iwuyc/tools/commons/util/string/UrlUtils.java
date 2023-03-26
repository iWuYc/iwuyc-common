package com.iwuyc.tools.commons.util.string;

/**
 * url地址工具类
 */
public class UrlUtils {

    private static final char OBLIQUE_LINE = '/';
    private static final String SCHEMA_SPLIT = "://";

    /**
     * 规范输出host的路径。<br/>
     * <pre>
     * Example:
     * 1、http://www.baidu.com -> http://www.baidu.com
     * 2、http://www.baidu.com/ -> http://www.baidu.com
     * 3、http://www.baidu.com/// -> http://www.baidu.com
     * </pre>
     *
     * @param host 待处理的路径
     * @return 规范化后的路径
     */
    public static String hostFix(String host) {
        final int colonIndex = host.indexOf(SCHEMA_SPLIT);
        StringBuilder hostBuilder = new StringBuilder(host.length());
        final int splitIndex;
        if (colonIndex > 0) {
            hostBuilder.append(host, 0, colonIndex + SCHEMA_SPLIT.length());
            splitIndex = colonIndex + SCHEMA_SPLIT.length();
        } else {
            splitIndex = 0;
        }
        final String obliqueLineFix = obliqueLineFix(host.substring(splitIndex));
        final int obliqueLineLength = obliqueLineFix.length();
        if (obliqueLineFix.charAt(obliqueLineLength - 1) == OBLIQUE_LINE) {
            hostBuilder.append(obliqueLineFix, 0, obliqueLineLength - 1);
        } else {
            hostBuilder.append(obliqueLineFix);
        }
        return hostBuilder.toString();
    }

    /**
     * 规范输出uri的路径。<br/>
     * <pre>
     * Example:
     * 1、/product/list -> /product/list
     * 2、product/list -> /product/list
     * 3、//product/list -> /product/list
     * 4、/product/list/ -> /product/list/
     * 4、///product///list/// -> /product/list/
     * 5、/product/list -> /product/list
     * 5、///product////list -> /product/list
     * </pre>
     *
     * @param path 待处理的uri路径
     * @return 规范化后的路径
     */
    public static String pathFix(String path) {
        final String obliqueLineFix = obliqueLineFix(path);
        final char firstChar = obliqueLineFix.charAt(0);
        if (firstChar == OBLIQUE_LINE) {
            return obliqueLineFix;
        }
        return OBLIQUE_LINE + obliqueLineFix;
    }

    /**
     * 去除多余的斜线
     *
     * @param str 待处理的字符串
     * @return 处理后的字符串
     */
    private static String obliqueLineFix(String str) {
        final int length = str.length();
        StringBuilder uriBuilder = new StringBuilder(length);
        boolean lastIsObliqueLine = true;
        for (int i = 0; i < length; i++) {
            final char charCursor = str.charAt(i);
            if (OBLIQUE_LINE != charCursor) {
                uriBuilder.append(charCursor);
                lastIsObliqueLine = false;
            } else if (!lastIsObliqueLine) {
                uriBuilder.append(OBLIQUE_LINE);
                lastIsObliqueLine = true;
            }
        }

        return uriBuilder.toString();
    }

    /**
     * 处理url
     *
     * @param host 主机地址
     * @param path 资源路径
     * @return url地址修复
     */
    public static String urlFix(String host, String path) {
        return hostFix(host) + pathFix(path);
    }
}
