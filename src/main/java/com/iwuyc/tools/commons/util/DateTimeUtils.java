package com.iwuyc.tools.commons.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.iwuyc.tools.commons.basic.type.DateTimeFormatterTuple;
import com.iwuyc.tools.commons.util.time.DateFormatterConstants;
import com.iwuyc.tools.commons.util.time.DateTimeBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 日期时间工具类
 *
 * @author Neil
 */
public class DateTimeUtils {
    private static final LoadingCache<DateTimeFormatterTuple, DateTimeFormatter> DATE_TIME_FORMATTER_CACHE;


    static {
        CacheLoader<DateTimeFormatterTuple, DateTimeFormatter> cacheLoader =
                new CacheLoader<DateTimeFormatterTuple, DateTimeFormatter>() {
                    @Override
                    public DateTimeFormatter load(DateTimeFormatterTuple tuple) {
                        return DateTimeFormatter.ofPattern(tuple.getPattern(), tuple.getLocale());
                    }
                };
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        cacheBuilder.expireAfterAccess(10, TimeUnit.MINUTES);
        DATE_TIME_FORMATTER_CACHE = cacheBuilder.build(cacheLoader);
    }

    private static DateTimeFormatter getDateTimeFormatter(DateTimeFormatterTuple tuple) {
        return DATE_TIME_FORMATTER_CACHE.getUnchecked(tuple);
    }

    public static DateTimeFormatter getDateTimeFormatter(String pattern, Locale locale) {
        return getDateTimeFormatter(DateTimeFormatterTuple.create(pattern, locale));
    }

    public static DateTimeFormatter getDateTimeFormatter(String pattern) {
        return getDateTimeFormatter(pattern, DateFormatterConstants.DEFAULT_LOCALE);
    }

    /**
     * 使用指定的locale、pattern将字符串格式的日期转换为日期类型。
     *
     * @param dateStr 待转换的日期类型
     * @param pattern 指定的pattern
     * @param locale  指定的locale
     * @return 转换后的日期类型
     * @see DateFormatterConstants#DEFAULT_LOCALE
     */
    public static Date parse(String dateStr, String pattern, Locale locale) {
        return DateTimeBuilder.withTime(dateStr, pattern, locale).toDate();
    }

    /**
     * 使用默认的locale跟指定的pattern将字符串格式的日期转换为日期类型。
     *
     * @param dateStr 待转换的日期类型
     * @param pattern 指定的pattern
     * @return 转换后的日期类型
     * @see DateFormatterConstants#DEFAULT_LOCALE
     */
    public static Date parse(String dateStr, String pattern) {
        return parse(dateStr, pattern, DateFormatterConstants.DEFAULT_LOCALE);
    }

    /**
     * 使用默认的pattern、locale将字符串格式的日期转换为日期类型。
     *
     * @param dateStr 待转换的日期类型
     * @return 转换后的日期类型
     * @see DateFormatterConstants#DEFAULT
     * @see DateFormatterConstants#DEFAULT_LOCALE
     */
    public static Date parse(String dateStr) {
        return parse(dateStr, DateFormatterConstants.DEFAULT);
    }
}
