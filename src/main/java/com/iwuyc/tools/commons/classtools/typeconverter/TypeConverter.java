package com.iwuyc.tools.commons.classtools.typeconverter;

/**
 * @param <F>
 * @param <T>
 * @author @Neil
 * @since @2017年10月15日
 */
public interface TypeConverter<F, T> {

    /**
     * 将原值转换为目标类型
     *
     * @param from       原值
     * @param targetType 目标类型
     * @return 返回转换后的实例
     * @author @Neil
     */
    T convert(F from, Class<? extends T> targetType);

    /**
     * 判断是否支持转换为目标类型
     *
     * @param target
     * @return
     * @author @Neil
     */
    boolean support(Class<?> target);

}
