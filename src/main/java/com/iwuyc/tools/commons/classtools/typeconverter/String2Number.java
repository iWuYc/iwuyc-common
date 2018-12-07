package com.iwuyc.tools.commons.classtools.typeconverter;

import com.iwuyc.tools.commons.util.NumberUtils;

/**
 * 字符串转数字类型
 *
 * @author Neil
 */
public class String2Number extends AbstractStringConverter<Number> {
    @Override
    protected Number converterData(String data, Class<? extends Number> targetType) {
        Number result = NumberUtils.parse(data, targetType);
        return result;
    }

    @Override
    protected boolean isSupport(Class<?> target) {
        return NumberUtils.isNumberClass(target);
    }

}
