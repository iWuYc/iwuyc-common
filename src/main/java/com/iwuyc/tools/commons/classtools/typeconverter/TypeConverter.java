package com.iwuyc.tools.commons.classtools.typeconverter;

/**
 * @author @Neil
 * @since @2017年10月15日
 * @param <F>
 * @param <T>
 */
public interface TypeConverter<F, T> {
    
    /**
     * 将原值转换为目标类型
     * @author @Neil
     * @param from 原值
     * @param targetType 目标类型
     * @return 返回转换后的实例
     */
    T convert(F from, Class<? extends T> targetType);

    /**
     * 判断是否支持转换为目标类型
     * @author @Neil
     * @param target
     * @return
     */
    boolean support(Class<?> target);

}
