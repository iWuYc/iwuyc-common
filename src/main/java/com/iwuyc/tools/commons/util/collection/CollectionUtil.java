package com.iwuyc.tools.commons.util.collection;

import com.iwuyc.tools.commons.util.string.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public interface CollectionUtil {
    static boolean isEmpty(Collection<?> coll) {
        return null == coll || coll.isEmpty();
    }

    static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    static String join(Collection<?> data) {
        return join(data, ',');
    }

    /**
     * 按指定的splitChar 分隔符，对集合进行拼接。
     * <pre>
     *     example:
     *       input:
     *         data:[0,1,2,3]
     *         splitChar: ';'
     *       output:
     *         "0;1;2;3"
     * </pre>
     *
     * @param data      待拼接的集合
     * @param splitChar 分隔符
     * @return 拼接后的字符串
     */
    static String join(Collection<?> data, Character splitChar) {
        if (CollectionUtil.isEmpty(data)) {
            return StringUtils.NIL_STRING;
        }
        final String splitStr;
        if (null == splitChar) {
            splitStr = ",";
        } else {
            splitStr = String.valueOf(splitChar);
        }
        StringJoiner joiner = new StringJoiner(splitStr);
        for (Object item : data) {
            joiner.add(String.valueOf(item));
        }
        return joiner.toString();
    }

    /**
     * 计算collection总共有多少元素，避免空指针问题，如果collection为null，则返回0
     *
     * @param collection 待计算的集合
     * @return 集合所含元素大小
     */
    static int sizeOf(Collection<?> collection) {
        if (isEmpty(collection)) {
            return 0;
        }
        return collection.size();
    }

    /**
     * 根据指定的 splitSize 分割集合。结果集是按照遍历coll的顺序进行分割的。
     *
     * @param coll      待分割的集合
     * @param splitSize 分割大小
     * @param <T>       集合元素的类型
     * @return 返回结果
     */
    static <T> List<List<T>> split(Collection<T> coll, int splitSize) {
        if (isEmpty(coll)) {
            return Collections.emptyList();
        }
        List<List<T>> result = new ArrayList<>();
        List<T> container = null;
        for (T item : coll) {
            if (null == container) {
                container = new ArrayList<>(splitSize);
                result.add(Collections.unmodifiableList(container));
            }

            container.add(item);
            if (container.size() == splitSize) {
                container = null;
            }
        }
        return Collections.unmodifiableList(result);
    }
}
