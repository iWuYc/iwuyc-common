package com.iwuyc.tools.commons.util;

/**
 * 限定条件
 *
 * @author Neil
 */
public class Conditionals {

    public static <T> T notNull(T checkObj) {
        if (null == checkObj) {
            throw new IllegalArgumentException("Argument can't be null.");
        }
        return checkObj;
    }
}
