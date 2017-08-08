package com.iwuyc.tools.commons.basic;

import java.util.Collection;

public abstract class CollectionUtil {

    public static boolean isEmpty(Collection<?> coll) {
        return null == coll || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

}
