package com.iwuyc.tools.commons.classtools.typeconverter;

import com.iwuyc.tools.commons.util.string.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <T>
 * @author @Neil
 * @since @2017年10月15日
 */
public abstract class AbstractStringConverter<T> implements TypeConverter<String, T> {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractStringConverter.class);

    /**
     * 进行数据转换
     *
     * @param data       待转换的数据
     * @param targetType 目标类型
     * @return 返回值
     * @author @Neil
     */
    protected abstract T converterData(String data, Class<? extends T> targetType);

    /**
     * 是否支持转换为目标类型
     *
     * @param target 目标类型
     * @return 如果支持则返回true
     * @author @Neil
     */
    protected abstract boolean isSupport(Class<?> target);

    @Override
    public final boolean support(Class<?> target) {
        return null != target && isSupport(target);
    }

    @Override
    public T convert(String from, Class<? extends T> targetType) {
        if (StringUtils.isEmpty(from)) {
            throw new IllegalArgumentException("Argument can't be null.");
        }
        return converterData(from, targetType);
    }

}
