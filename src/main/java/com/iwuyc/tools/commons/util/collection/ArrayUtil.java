package com.iwuyc.tools.commons.util.collection;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public abstract class ArrayUtil {

    public static <T> boolean isEmpty(T[] arrays) {
        return arrayLength(arrays) == 0;
    }

    public static <T> boolean isNotEmpty(T[] arrays) {
        return !isEmpty(arrays);
    }

    public static boolean isEmpty(long[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    public static boolean isNotEmpty(long[] arrays) {
        return !isEmpty(arrays);
    }

    public static boolean isEmpty(int[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    public static boolean isNotEmpty(int[] arrays) {
        return !isEmpty(arrays);
    }

    public static boolean isEmpty(short[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    public static boolean isNotEmpty(short[] arrays) {
        return !isEmpty(arrays);
    }

    public static boolean isEmpty(byte[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    public static boolean isNotEmpty(byte[] arrays) {
        return !isEmpty(arrays);
    }

    public static boolean isEmpty(float[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    public static boolean isNotEmpty(float[] arrays) {
        return !isEmpty(arrays);
    }

    public static boolean isEmpty(double[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    public static boolean isNotEmpty(double[] arrays) {
        return !isEmpty(arrays);
    }

    public static boolean isEmpty(char[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    public static boolean isNotEmpty(char[] arrays) {
        return !isEmpty(arrays);
    }

    public static boolean isEmpty(boolean[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    public static boolean isNotEmpty(boolean[] arrays) {
        return !isEmpty(arrays);
    }

    public static <T> int arrayLength(T[] arrays) {
        return isEmpty(arrays) ? 0 : arrays.length;
    }
    public static <T> int arrayLength(long[] arrays) {
        return isEmpty(arrays) ? 0 : arrays.length;
    }
}
