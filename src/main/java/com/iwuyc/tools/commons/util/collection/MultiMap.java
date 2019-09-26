package com.iwuyc.tools.commons.util.collection;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Non Thread-Safe.复合map。
 *
 * @param <K> key
 * @param <V> value
 * @author Neil
 */
public class MultiMap<K, V> extends HashMap<K, LinkedList<V>> {

    private static final long serialVersionUID = 889155975950159878L;

    public void add(K key, V val) {
        LinkedList<V> valContainer = getCollection(key);
        valContainer.add(val);
    }

    public void addFirst(K key, V val) {
        LinkedList<V> valContainer = getCollection(key);
        valContainer.addFirst(val);
    }

    private LinkedList<V> getCollection(K key) {
        LinkedList<V> result = this.get(key);
        if (null == result) {
            result = new LinkedList<>();
            this.put(key, result);
        }
        return result;
    }
}
