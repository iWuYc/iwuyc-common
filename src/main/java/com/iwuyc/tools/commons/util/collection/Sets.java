package com.iwuyc.tools.commons.util.collection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Sets {
    public static <T> Set<T> asSet(T... elements) {
        if (ArrayUtil.isEmpty(elements)) {
            return Collections.emptySet();
        }
        HashSet<T> result = new HashSet<>(elements.length);
        Collections.addAll(result, elements);
        return result;
    }
}
