package com.iwuyc.tools.commons.thread.conf.typeconverter;

import com.iwuyc.tools.commons.basic.type.TimeTuple;
import com.iwuyc.tools.commons.classtools.typeconverter.AbstractStringConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public class String2TimeTupleConverter extends AbstractStringConverter<TimeTuple> {
    private static final Pattern UNIT_PATTERN = Pattern.compile("[A-Za-z]+");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");
    private static final Map<String, TimeUnit> MAPPING = new HashMap<>();

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
        Matcher unitMatcher = UNIT_PATTERN.matcher(from);
        TimeUnit timeUnit;
        if (unitMatcher.find()) {
            String unitStr = unitMatcher.group();
            timeUnit = MAPPING.get(unitStr);
            if (null == timeUnit) {
                throw new IllegalArgumentException("Can't find unit for [" + unitStr + "]");
            }
        } else {
            timeUnit = TimeUnit.SECONDS;
        }
        return TimeTuple.create(num, timeUnit);
    }

    @Override
    protected boolean isSupport(Class<?> target) {
        return TimeTuple.class.equals(target);
    }

}
