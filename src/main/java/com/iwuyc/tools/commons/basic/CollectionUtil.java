package com.iwuyc.tools.commons.basic;

import java.util.Collection;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public abstract class CollectionUtil {

    public static boolean isEmpty(Collection<?> coll) {
        return null == coll || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    public static String join(Collection<?> data) {
        return join(data, null);
    }

    public static String join(Collection<?> data, Character splitChar) {
        StringBuilder sb = new StringBuilder();
        for (Object item : data) {
            if (null != splitChar) {
                sb.append(splitChar);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    public static int sizeOf(Collection<?> collection) {
        if (isEmpty(collection)) {
            return 0;
        }
        return collection.size();
    }
}
