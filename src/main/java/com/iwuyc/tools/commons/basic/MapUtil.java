package com.iwuyc.tools.commons.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auth iWuYc
 * @since JDK 8
 * @time 2017-08-04 14:31
 */
public abstract class MapUtil
{

    /**
     * 根据map的值获取map中的key
     * 
     * @param map
     * @param val
     * @return
     */
    public static <K, V> Collection<K> findKeyByVal(Map<K, V> map, Object val)
    {
        if (isEmpty(map))
        {
            return Collections.emptyList();
        }
        final Collection<K> result = new ArrayList<>();

        map.entrySet().stream().filter((item) ->
        {
            // 查找的目标是null，map里的值也是null。即查找map里val为null的key值。
            if (item.getValue() == val)
            {
                return true;
            }
            // 其他不为null的情况使用equal方法进行比较。
            return null != item.getValue() && item.getValue().equals(val);
        }).forEach((item) ->
        {
            result.add(item.getKey());
        });
        return result;
    }

    public static <K, V> Map<K, V> findEntryByPrefixKey(Map<K, V> map, String prefixKey)
    {
        final Map<K, V> result = new HashMap<>();
        map.entrySet().stream().filter((item) ->
        {
            return String.valueOf(item.getKey()).startsWith(prefixKey);
        }).forEach((item) ->
        {
            result.put(item.getKey(), item.getValue());
        });
        return result;
    }

    public static boolean isEmpty(Map<?, ?> map)
    {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map)
    {
        return !isEmpty(map);
    }
}
