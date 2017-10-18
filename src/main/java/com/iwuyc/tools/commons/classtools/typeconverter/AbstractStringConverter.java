package com.iwuyc.tools.commons.classtools.typeconverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwuyc.tools.commons.basic.AbstractStringUtils;

/**
 * @author @Neil
 * @since @2017年10月15日
 * @param <T>
 */
public abstract class AbstractStringConverter<T> implements TypeConverter<String, T> {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractStringConverter.class);

    /**
     * 进行数据转换
     * 
     * @author @Neil
     * @param data
     *            待转换的数据
     * @param targetType
     *            目标类型
     * @return 返回值
     */
    protected abstract T converterData(String data, Class<? extends T> targetType);

    /**
     * 是否支持转换为目标类型
     * 
     * @author @Neil
     * @param target
     *            目标类型
     * @return 如果支持则返回true
     */
    protected abstract boolean isSupport(Class<?> target);

    @Override
    public final boolean support(Class<?> target) {
        return null != target && isSupport(target);
    }

    @Override
    public T convert(String from, Class<? extends T> targetType) {
        if (AbstractStringUtils.isEmpty(from)) {
            throw new IllegalArgumentException("Argument can't be null.");
        }
        return converterData(from, targetType);
    }

}
