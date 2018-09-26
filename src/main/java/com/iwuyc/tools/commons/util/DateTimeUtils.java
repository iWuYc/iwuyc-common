package com.iwuyc.tools.commons.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.iwuyc.tools.commons.basic.type.DateTimeFormatterTuple;
import com.iwuyc.tools.commons.util.time.DateFormatterPattern;
import com.iwuyc.tools.commons.util.time.SmartDateTimeFormatter;

import java.time.format.DateTimeFormatter;
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
        return getDateTimeFormatter(DateTimeFormatterTuple.create(pattern, DateFormatterPattern.DEFAULT_LOCALE));
    }


}
