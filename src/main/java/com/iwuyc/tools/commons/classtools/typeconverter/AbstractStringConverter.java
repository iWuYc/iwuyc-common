package com.iwuyc.tools.commons.classtools.typeconverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwuyc.tools.commons.basic.StringUtils;

public abstract class AbstractStringConverter<T> implements TypeConverter<String, T> {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractStringConverter.class);

    protected abstract T converterData(String data, Class<? extends T> targetType);

    protected abstract boolean isSupport(Class<?> target);

    @Override
    public final boolean support(Class<?> target) {
        return null != target && isSupport(target);
    }

    @Override
    public T convert(String from, Class<? extends T> targetType) {
        if (StringUtils.isEmpty(from)) { throw new IllegalArgumentException("Argument can't be null."); }
        return converterData(from, targetType);
    }

}
