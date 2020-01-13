package com.iwuyc.tools.commons.util.json.mapper;

import com.iwuyc.tools.commons.util.collection.Sets;

import java.util.Collections;
import java.util.Set;

public interface JsonMapper<SrcJsonType, TargetStruct> {
    /**
     * 生成目标数组的数据源，必须是一个数组
     */
    String SOURCE_ARR = "$arrSource";
    /**
     * 数组如果是单值类型，使用该保留关键字来指定 {@link JsonMapper#SOURCE_ARR} 中对应的节点<br />
     * 对应的是源数组中的每一个元素的节点路径，而非从根路径开始计算。
     */
    String VALUE_XPATH = "$valXpath";

    /**
     * 保留字
     */
    Set<String> RESERVED_WORD = Collections.unmodifiableSet(Sets.asSet(SOURCE_ARR, VALUE_XPATH));

    /**
     * 将source按targetStruct的结构解析映射
     *
     * @param source       数据源
     * @param targetStruct 目标结构
     * @return 返回目标结构相同的
     */
    SrcJsonType mapper(SrcJsonType source, SrcJsonType targetStruct);
}
