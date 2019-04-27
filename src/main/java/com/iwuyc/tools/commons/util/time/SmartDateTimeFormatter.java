package com.iwuyc.tools.commons.util.time;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.iwuyc.tools.commons.basic.type.DateTimeFormatterTuple;

import java.time.*;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class SmartDateTimeFormatter {

    private static final Pattern DATE_PATTERN = Pattern.compile(".*([yMd]+.*)+");
    private static final Pattern TIME_PATTERN = Pattern.compile(".*([HhmsS]+.*)+");
    private static final Pattern ZONE_DATE_TIME_PATTERN = Pattern.compile(".*'T'.*((Z)|(\\+[0-9]{4}))");
    private static final LoadingCache<DateTimeFormatterTuple, SmartDateTimeFormatter> DATE_TIME_SMART_FORMATTER_CACHE;

    static {
        CacheLoader<DateTimeFormatterTuple, SmartDateTimeFormatter> smartCacheLoader =
            new SmartDateTimeFormatterLoader();
        CacheBuilder<Object, Object> smartCacheBuilder = CacheBuilder.newBuilder();
        smartCacheBuilder.expireAfterAccess(10, TimeUnit.MINUTES);
        DATE_TIME_SMART_FORMATTER_CACHE = smartCacheBuilder.build(smartCacheLoader);
    }

    private final String pattern;
    private final DateTimeFormatter formatter;
    /**
     * 日期格式的模式，左起算：第一位是表示是否为时区时间；第二位表示是时间格式；第三位为日期格式
     */
    private final int modFlag;
    SmartDateTimeFormatter(String pattern, Locale locale) {
        this.pattern = pattern;
        int modTemp = 0;
        if (isZoneDateTimePattern(pattern)) {
            modTemp |= 1;
        } else {
            if (isTimePattern(pattern)) {
                modTemp |= 2;
            }
            if (isDatePattern(pattern)) {
                modTemp |= 4;
            }
        }
        if (modTemp <= 0) {
            throw new IllegalArgumentException("Pattern was wrong.The pattern was [" + pattern + "}");
        }
        modFlag = modTemp;
        this.formatter = DateTimeFormatter.ofPattern(pattern, locale);
    }

    public static boolean isDatePattern(String pattern) {
        return DATE_PATTERN.matcher(pattern).find();
    }

    public static boolean isTimePattern(String pattern) {
        return TIME_PATTERN.matcher(pattern).find();
    }

    public static boolean isDateTimePattern(String pattern) {
        return isDatePattern(pattern) && isTimePattern(pattern);
    }

    public static boolean isZoneDateTimePattern(String pattern) {
        return ZONE_DATE_TIME_PATTERN.matcher(pattern).matches();
    }

    public static SmartDateTimeFormatter create(String pattern, Locale locale) {
        return create(DateTimeFormatterTuple.create(pattern, locale));
    }

    public static SmartDateTimeFormatter create(DateTimeFormatterTuple tuple) {
        return DATE_TIME_SMART_FORMATTER_CACHE.getUnchecked(tuple);
    }

    public String format(TemporalAccessor time) {
        if (this.modFlag == 1) {
            if (ChronoLocalDateTime.class.isAssignableFrom(time.getClass())) {
                ChronoLocalDateTime localDateTime = (ChronoLocalDateTime)time;
                ChronoZonedDateTime zonedDateTime = localDateTime.atZone(DateFormatterConstants.DEFAULT_ZONE_OFFSET);
                return zonedDateTime.format(this.formatter);
            }
        }
        return this.formatter.format(time);
    }

    public LocalDateTime parse(String time) {
        LocalDateTime result = null;
        switch (this.modFlag) {
            case 1:
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(time, this.formatter);
                result = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                break;
            case 2:
                LocalTime localTime = LocalTime.parse(time, this.formatter);
                result = localTime.atDate(LocalDate.now());
                break;
            case 4:
                LocalDate localDate = LocalDate.parse(time, this.formatter);
                result = localDate.atTime(LocalTime.MIN);
                break;
            case 6:
                result = LocalDateTime.parse(time, this.formatter);
                break;
            case 7:
                zonedDateTime = ZonedDateTime.parse(time, this.formatter);
                result = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                break;
            default:
                ZonedDateTime.parse(time, this.formatter);
                throw new IllegalArgumentException("Pattern was wrong.The pattern was [" + pattern + "]");
        }

        return result;
    }

    TemporalAccessor parse(String time, DateParser parser) {
        return parser.parse(time, this.formatter);
    }

    interface DateParser {
        TemporalAccessor parse(String time, DateTimeFormatter formatter);
    }

    private static final class SmartDateTimeFormatterLoader
        extends CacheLoader<DateTimeFormatterTuple, SmartDateTimeFormatter> {

        @Override
        public SmartDateTimeFormatter load(DateTimeFormatterTuple tuple) throws Exception {
            SmartDateTimeFormatter formatter = new SmartDateTimeFormatter(tuple.getPattern(), tuple.getLocale());
            return formatter;
        }
    }
}
