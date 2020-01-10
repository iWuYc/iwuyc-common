package com.iwuyc.tools.commons.util.json.mapper;

public interface JsonMapper<SrcJsonType, TargetStruct> {
    /**
     * 将source按targetStruct的结构解析映射
     *
     * @param source       数据源
     * @param targetStruct 目标结构
     * @return 返回目标结构相同的
     */
    SrcJsonType mapper(SrcJsonType source, SrcJsonType targetStruct);
}
