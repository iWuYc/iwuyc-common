package com.iwuyc.tools.commons.util.collection;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public interface ArrayUtil {

    static <T> boolean isEmpty(T[] arrays) {
        return null == arrays || arrays.length == 0;
    }

    static <T> boolean isNotEmpty(T[] arrays) {
        return !isEmpty(arrays);
    }

    static boolean isEmpty(long[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    static boolean isNotEmpty(long[] arrays) {
        return !isEmpty(arrays);
    }

    static boolean isEmpty(int[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    static boolean isNotEmpty(int[] arrays) {
        return !isEmpty(arrays);
    }

    static boolean isEmpty(short[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    static boolean isNotEmpty(short[] arrays) {
        return !isEmpty(arrays);
    }

    static boolean isEmpty(byte[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    static boolean isNotEmpty(byte[] arrays) {
        return !isEmpty(arrays);
    }

    static boolean isEmpty(float[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    static boolean isNotEmpty(float[] arrays) {
        return !isEmpty(arrays);
    }

    static boolean isEmpty(double[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    static boolean isNotEmpty(double[] arrays) {
        return !isEmpty(arrays);
    }

    static boolean isEmpty(char[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    static boolean isNotEmpty(char[] arrays) {
        return !isEmpty(arrays);
    }

    static boolean isEmpty(boolean[] arrays) {
        return null == arrays || 0 == arrays.length;
    }

    static boolean isNotEmpty(boolean[] arrays) {
        return !isEmpty(arrays);
    }

    static <T> int arrayLength(T[] arrays) {
        return isEmpty(arrays) ? 0 : arrays.length;
    }

    static int arrayLength(long[] arrays) {
        return isEmpty(arrays) ? 0 : arrays.length;
    }
}
