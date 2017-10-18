package com.iwuyc.tools.commons.classtools.typeconverter;

import java.util.Date;

import com.iwuyc.tools.commons.util.TimeUtil;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public class String2Date extends AbstractStringConverter<Date> {

    @Override
    protected Date converterData(String data, Class<? extends Date> targetType) {
        return TimeUtil.parser(data);
    }

    @Override
    protected boolean isSupport(Class<?> target) {
        return target.isAssignableFrom(Date.class);
    }

}
