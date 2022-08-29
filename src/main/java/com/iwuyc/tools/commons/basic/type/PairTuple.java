package com.iwuyc.tools.commons.basic.type;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用于存储键-值对的场景
 *
 * @param <K> key的实际类型
 * @param <V> val的实际类型
 */
@Data
@ToString
public class PairTuple<K extends Serializable, V extends Serializable> implements Serializable {
    private static final long serialVersionUID = 8796738503406673563L;
    private final K key;
    private final V val;

    public PairTuple(K key, V val) {
        this.key = key;
        this.val = val;
    }
}
