package com.iwuyc.tools.commons.classtools.typeconverter;

import com.iwuyc.tools.beans.converter.string.StringConverter;
import com.iwuyc.tools.commons.basic.type.TimeTuple;
import com.iwuyc.tools.commons.util.collection.Sets;

import java.util.Set;

public class TimeTupleConverter extends StringConverter<TimeTuple> {
    @Override
    public TimeTuple convert(String source, Class<? extends TimeTuple> targetClass) {
        return String2TimeTupleConverter.converter(source, targetClass);
    }

    @Override
    public boolean support(Class<? extends TimeTuple> targetClass) {
        return TimeTuple.class.isAssignableFrom(targetClass);
    }

    @Override
    protected Set<Class<? extends TimeTuple>> getSupportClass() {
        return Sets.asSet(TimeTuple.class);
    }
}
