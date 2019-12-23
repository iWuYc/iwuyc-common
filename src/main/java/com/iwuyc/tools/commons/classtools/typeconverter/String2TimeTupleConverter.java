package com.iwuyc.tools.commons.classtools.typeconverter;

import com.iwuyc.tools.commons.basic.type.TimeTuple;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
@Slf4j
public class String2TimeTupleConverter extends AbstractStringConverter<TimeTuple> {
    private static final Pattern UNIT_PATTERN = Pattern.compile("[A-Za-z]+");
    private static final Map<String, TimeUnit> MAPPING = new HashMap<>();

    static {
        MAPPING.put("d", TimeUnit.DAYS);
        MAPPING.put("h", TimeUnit.HOURS);
        MAPPING.put("m", TimeUnit.MINUTES);
        MAPPING.put("s", TimeUnit.SECONDS);
        MAPPING.put("ms", TimeUnit.MILLISECONDS);
        MAPPING.put("ns", TimeUnit.NANOSECONDS);
    }

    /**
     * 将时间表达式转换为时间元组.示例:1h,1m,1s,1ms,1ns,分别表示:1小时,1分钟,1秒钟,1毫秒,1纳秒
     *
     * @param from 源数据
     * @return 转换后的实例
     */
    public static TimeTuple converter(String from) {
        return converter(from, TimeTuple.class);
    }

    /**
     * 将时间表达式转换为时间元组.示例:1h,1m,1s,1ms,1ns,分别表示:1小时,1分钟,1秒钟,1毫秒,1纳秒
     *
     * @param from       源数据
     * @param targetType 目标类型
     * @return 转换后的实例
     */
    public static TimeTuple converter(String from, Class<? extends TimeTuple> targetType) {
        from = from.trim();

        String numStr = from.replaceAll("[A-Za-z]*", "").trim();
        long num = Long.parseLong(numStr);
        Matcher unitMatcher = UNIT_PATTERN.matcher(from);
        TimeUnit timeUnit;
        if (unitMatcher.find()) {
            String unitStr = unitMatcher.group();
            log.debug("转换的字符中存在单位，单位为：[{}]", unitStr);
            timeUnit = MAPPING.get(unitStr);
            if (null == timeUnit) {
                log.warn("转换的字符中存在不认识的单位：[{}]。单位列表为：[{}]", unitStr, MAPPING.keySet());
                throw new IllegalArgumentException("Can't find unit for [" + unitStr + "]");
            }
        } else {
            log.debug("转换的字符中不存在单位，使用默认的单位：s");
            timeUnit = TimeUnit.SECONDS;
        }
        return TimeTuple.create(num, timeUnit);
    }

    @Override
    public TimeTuple converterData(String from, Class<? extends TimeTuple> targetType) {
        return converter(from, targetType);
    }

    @Override
    protected boolean isSupport(Class<?> target) {
        return TimeTuple.class.equals(target);
    }

}
