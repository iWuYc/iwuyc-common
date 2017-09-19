package com.iwuyc.tools.commons.thread.conf.typeconverter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.iwuyc.tools.commons.basic.StringUtils;
import com.iwuyc.tools.commons.basic.type.TimeTuple;
import com.iwuyc.tools.commons.classtools.typeconverter.AbstractStringConverter;

public class String2TimeTupleConverter extends AbstractStringConverter<TimeTuple> {
    public static final Map<String, TimeUnit> MAPPING = new HashMap<>();

    static {
        MAPPING.put("h", TimeUnit.HOURS);
        MAPPING.put("m", TimeUnit.MINUTES);
        MAPPING.put("s", TimeUnit.SECONDS);
        MAPPING.put("ms", TimeUnit.MILLISECONDS);
        MAPPING.put("ns", TimeUnit.NANOSECONDS);
    }

    @Override
    protected TimeTuple converterData(String from, Class<? extends TimeTuple> targetType) {
        from = from.trim();

        String numStr = from.replaceAll("[A-Za-z]*", "").trim();
        long num = Long.parseLong(numStr);

        String unitStr = from.replaceAll("[0-9]*", "").trim();
        TimeUnit timeUnit = null;
        if (StringUtils.isEmpty(unitStr)) {
            timeUnit = TimeUnit.SECONDS;
        }
        else {
            timeUnit = MAPPING.get(unitStr);
            if (null == timeUnit) {
                throw new IllegalArgumentException("Can't find unit for [" + unitStr + "]");
            }
        }
        return TimeTuple.create(num, timeUnit);
    }

    @Override
    protected boolean isSupport(Class<?> target) {
        return TimeTuple.class.equals(target);
    }

}
