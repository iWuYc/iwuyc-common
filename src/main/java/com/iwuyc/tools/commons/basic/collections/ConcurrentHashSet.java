package com.iwuyc.tools.commons.basic.collections;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 线程安全的hash set实现
 * @author 吴宇春
 */
public class ConcurrentHashSet<T> extends EmbellishSet<T> {
    public ConcurrentHashSet() {
        super(new ConcurrentHashMap<>());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean add(T o) {
        Object previousVal = ((ConcurrentHashMap) this.container).putIfAbsent(o, Boolean.TRUE);
        return previousVal == null;
    }
}
