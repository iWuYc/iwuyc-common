package com.iwuyc.tools.commons.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.iwuyc.tools.commons.basic.type.DateTimeFormatterTuple;
import com.iwuyc.tools.commons.util.time.DateFormatterConstants;
import com.iwuyc.tools.commons.util.time.DateTimeBuilder;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 日期时间工具类
 * <pre>
 * DST：Daylight Saving Time中文名叫“夏令时”，一般在天亮早的夏季人为将时间调快一小时，可以使人早起早睡，减少照明量，以充分利用光照资源，从而节约照明用电。中国1986-1991年实行夏令时，1992年废除。
 * CST：China Standard Time（老外认为有其他含义，中国就这个缩写），中国标准时。
 * GMT：Greenwich Mean Time，格林威治标准时，地球每15°经度 被分为一个时区，共分为24个时区，相邻时区相差一小时；例: 中国北京位于东八区。
 * </pre>
 * 该类使用地区命名的地区标准时，在中国叫CST。在1986-1991年期间的夏天，会比冬天快一个小时。
 *
 * @author Neil
 */
public class DateTimeUtils {


    static DateTimeFormatter getDateTimeFormatter(DateTimeFormatterTuple tuple) {
        return InnerInitialize.DATE_TIME_FORMATTER_CACHE.getUnchecked(tuple);
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
     * @param zoneId  时区编码
     * @return 转换后的日期类型
     * @see DateFormatterConstants#DEFAULT_LOCALE
     */
    public static Date parse(String dateStr, String pattern, Locale locale, ZoneId zoneId) {
        return DateTimeBuilder.withTime(dateStr, pattern, locale).toDate(zoneId);
    }

    /**
     * 使用指定的locale、pattern将字符串格式的日期转换为日期类型。
     *
     * @param dateStr 待转换的日期类型
     * @param pattern 指定的pattern
     * @param locale  指定的locale
     * @return 转换后的日期类型
     * @see DateFormatterConstants#DEFAULT_LOCALE
     * @see ZoneOffset#systemDefault()
     */
    public static Date parse(String dateStr, String pattern, Locale locale) {
        return parse(dateStr, pattern, locale, ZoneOffset.systemDefault());
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
     * @see DateFormatterConstants#DEFAULT_PATTERN
     * @see DateFormatterConstants#DEFAULT_LOCALE
     */
    public static Date parse(String dateStr) {
        return parse(dateStr, DateFormatterConstants.DEFAULT_PATTERN);
    }

    public static String format(Date date, String pattern) {
        DateTimeBuilder builder = DateTimeBuilder.withTime(date);
        return builder.format(pattern);
    }

    public static String format(Date date) {
        return format(date, DateFormatterConstants.DEFAULT_PATTERN);
    }

    public static String now() {
        return DateTimeBuilder.withTime(new Date()).format();
    }

    private static class InnerInitialize {
        private static final LoadingCache<DateTimeFormatterTuple, DateTimeFormatter> DATE_TIME_FORMATTER_CACHE;

        static {
            CacheLoader<DateTimeFormatterTuple, DateTimeFormatter> cacheLoader = new CacheLoader<DateTimeFormatterTuple, DateTimeFormatter>() {

                @Override
                public DateTimeFormatter load(DateTimeFormatterTuple tuple) {
                    return DateTimeFormatter.ofPattern(tuple.getPattern(), tuple.getLocale());
                }
            };
            CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
            cacheBuilder.expireAfterAccess(10, TimeUnit.MINUTES);
            DATE_TIME_FORMATTER_CACHE = cacheBuilder.build(cacheLoader);
        }
    }
}
