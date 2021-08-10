package com.iwuyc.tools.commons.classtools.typeconverter;

import com.iwuyc.tools.commons.basic.type.TimeTuple;

public class TimeTupleConverter extends AbstractStringConverter<TimeTuple> {
    @Override
    protected TimeTuple converterData(String source, Class<? extends TimeTuple> targetType) {
        return String2TimeTupleConverter.converter(source, targetType);
    }

    @Override
    protected boolean isSupport(Class<?> targetClass) {
        return TimeTuple.class.isAssignableFrom(targetClass);
    }

}
