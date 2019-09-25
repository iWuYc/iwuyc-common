package com.iwuyc.tools.commons.util.file;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class IgnorableProperties extends Properties {

    private final Set<Object> ignoreKey = new HashSet<>();
    private final Set<Object> ignoreVal = new HashSet<>();
    private Object fillVal;

    /**
     * @param keys 增加忽略的key值，增加该值后，所有put到该properties，将忽略这些指定的key。
     * @return 当前properties实例
     */
    public IgnorableProperties addIgnoreKey(Object... keys){
        this.ignoreKey.addAll(Arrays.asList(keys));
        return this;
    }

    /**
     * @param vals 增加忽略的val值，增加该值后，所有put到该properties，将忽略这些指定的val。
     * @return 当前properties实例
     */
    public IgnorableProperties addIgnoreVal(Object... vals){
        this.ignoreVal.addAll(Arrays.asList(vals));
        return this;
    }

    @Override
    public synchronized Object put(Object key, Object value){
        if(ignoreKey.contains(key) || ignoreVal.contains(value)){
            return value;
        }
        if(null != fillVal){
            return super.put(key, fillVal);
        }
        return super.put(key, value);
    }

}
