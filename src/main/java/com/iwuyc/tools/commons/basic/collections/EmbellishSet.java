package com.iwuyc.tools.commons.basic.collections;

import com.google.common.base.Preconditions;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

/**
 * 可修饰的set实现，由实际传入的container的特性决定该set是否为线程安全的。
 * @author 吴宇春
 */
public class EmbellishSet<T> extends AbstractSet<T> {
    protected final Map<T, Boolean> container;

    public EmbellishSet(Map<T, Boolean> container) {
        this.container = Preconditions.checkNotNull(container);
    }

    @Override
    public int size() {
        return container.size();
    }

    @Override
    public boolean contains(Object o) {
        return container.containsKey(o);
    }

    @Override
    public Iterator<T> iterator() {
        return container.keySet().iterator();
    }

    @Override
    public boolean add(T o) {
        return container.put(o, Boolean.TRUE) == null;
    }

    @Override
    public boolean remove(Object o) {
        return container.remove(o) != null;
    }

    @Override
    public void clear() {
        container.clear();
    }
   
}
