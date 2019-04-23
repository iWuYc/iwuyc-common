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

    public static int sizeOf(Collection<?> collection) {
        if (isEmpty(collection)) {
            return 0;
        }
        return collection.size();
    }
}
