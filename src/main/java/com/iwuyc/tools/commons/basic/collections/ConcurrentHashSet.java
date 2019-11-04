package com.iwuyc.tools.commons.basic.collections;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<T> implements Set<T> {
    private final ConcurrentHashMap<T, Boolean> container;

    public ConcurrentHashSet() {
        this(8);
    }

    public ConcurrentHashSet(int initialCapacity) {
        this.container = new ConcurrentHashMap<>(initialCapacity);
    }


    @Override
    public int size() {
        return this.container.size();
    }

    @Override
    public boolean isEmpty() {
        return this.container.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.container.contains(o);
    }

    @Override
    @Nonnull
    public Iterator<T> iterator() {
        return this.container.keySet().iterator();
    }

    @Override
    @Nonnull
    public Object[] toArray() {
        return this.container.keySet().toArray();
    }

    @Override
    @Nonnull
    public <E> E[] toArray(E[] a) {
        return this.container.keySet().toArray(a);
    }

    @Override
    public boolean add(T t) {
        this.container.put(t, Boolean.TRUE);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Boolean old = this.container.remove(o);
        return old != null;
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        return this.container.keySet().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (T item : c) {
            this.container.put(item, Boolean.TRUE);
        }
        return false;
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        return this.container.keySet().removeIf(item -> !c.contains(item));
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        return this.container.keySet().removeIf(c::contains);
    }

    @Override
    public void clear() {
        this.container.clear();
    }
}
