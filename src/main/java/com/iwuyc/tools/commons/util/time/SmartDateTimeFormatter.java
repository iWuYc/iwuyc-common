package com.iwuyc.tools.commons.util.time;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.iwuyc.tools.commons.basic.type.DateTimeFormatterTuple;
import com.iwuyc.tools.commons.util.string.RegexUtils;
import com.iwuyc.tools.commons.util.time.converter.TemporalConverterCache;

import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartDateTimeFormatter {

    public static final Integer YEAR_MOD = 1;
    public static final Integer MONTH_MOD = 1 << 1;
    public static final Integer DAY_MOD = 1 << 2;
    public static final Integer HOURS_MOD = 1 << 3;
    public static final Integer MINUTES_MOD = 1 << 4;
    public static final Integer SECONDS_MOD = 1 << 5;
    public static final Integer MILLISECONDS_MOD = 1 << 6;
    public static final Integer ZONE_DATE_MOD = 1 << 7;

    private static final Pattern ZONE_DATE_PATTERN = RegexUtils.getPattern(".*'T'.*((Z)|(\\+[0-9]{4}))");

    private static final LoadingCache<DateTimeFormatterTuple, SmartDateTimeFormatter> DATE_TIME_SMART_FORMATTER_CACHE;

    private static final Map<Integer, Pattern> DATE_TIME_PATTERN_CACHE = new HashMap<>();

    static{
        DATE_TIME_PATTERN_CACHE.put(YEAR_MOD, RegexUtils.getPattern("([y]+)"));
        DATE_TIME_PATTERN_CACHE.put(MONTH_MOD, RegexUtils.getPattern("([M]+)"));
        DATE_TIME_PATTERN_CACHE.put(DAY_MOD, RegexUtils.getPattern("([d]+)"));
        DATE_TIME_PATTERN_CACHE.put(HOURS_MOD, RegexUtils.getPattern("([Hh]+)"));
        DATE_TIME_PATTERN_CACHE.put(MINUTES_MOD, RegexUtils.getPattern("([m]+)"));
        DATE_TIME_PATTERN_CACHE.put(SECONDS_MOD, RegexUtils.getPattern("([s]+)"));
        DATE_TIME_PATTERN_CACHE.put(MILLISECONDS_MOD, RegexUtils.getPattern("([S]+)"));

        CacheLoader<DateTimeFormatterTuple, SmartDateTimeFormatter> smartCacheLoader = new SmartDateTimeFormatterLoader();
        CacheBuilder<Object, Object> smartCacheBuilder = CacheBuilder.newBuilder();
        smartCacheBuilder.expireAfterAccess(10, TimeUnit.MILLISECONDS);
        DATE_TIME_SMART_FORMATTER_CACHE = smartCacheBuilder.build(smartCacheLoader);
    }

    private final DateTimeFormatter formatter;
    /**
     * 日期格式的模式，左起算：第一位是表示是否为时区时间；第二位表示是时间格式；第三位为日期格式
     */
    private final int modFlag;

    SmartDateTimeFormatter(String pattern, Locale locale){
        int modTemp = 0;
        if(isZoneDate(pattern)){
            modTemp = ZONE_DATE_MOD;
        }else {
            // 计算pattern的模式
            for(Map.Entry<Integer, Pattern> item : DATE_TIME_PATTERN_CACHE.entrySet()){
                Pattern patternItem = item.getValue();
                Matcher matcher = patternItem.matcher(pattern);
                if(matcher.find()){
                    modTemp |= item.getKey();
                }
            }
        }
        modFlag = modTemp;
        this.formatter = DateTimeFormatter.ofPattern(pattern, locale);
    }

    public static SmartDateTimeFormatter create(String pattern, Locale locale){
        return create(DateTimeFormatterTuple.create(pattern, locale));
    }

    public static SmartDateTimeFormatter create(DateTimeFormatterTuple tuple){
        return DATE_TIME_SMART_FORMATTER_CACHE.getUnchecked(tuple);
    }

    private boolean isZoneDate(String pattern){
        Matcher matcher = ZONE_DATE_PATTERN.matcher(pattern);
        return matcher.matches();
    }

    public String format(TemporalAccessor time){
        if(this.modFlag == 1){
            if(ChronoLocalDateTime.class.isAssignableFrom(time.getClass())){
                ChronoLocalDateTime localDateTime = (ChronoLocalDateTime)time;
                ChronoZonedDateTime zonedDateTime = localDateTime.atZone(DateFormatterConstants.DEFAULT_ZONE_OFFSET);
                return zonedDateTime.format(this.formatter);
            }
        }
        return this.formatter.format(time);
    }

    public ZonedDateTime parse(String time){
        TemporalConverterCache.TemporalConverter converter = TemporalConverterCache.getTemporalConverter(this.modFlag);
        Temporal temporal = converter.parse(time, this.formatter);
        ZonedDateTime zonedDateTimeTemplates = ZonedDateTime.now();

        ChronoField[] chronoFields = ChronoField.values();
        for(ChronoField item : chronoFields){

            if(temporal.isSupported(item)){
                zonedDateTimeTemplates.with(item, item.getFrom(temporal));
            }
        }

        return zonedDateTimeTemplates;

    }

    TemporalAccessor parse(String time, DateParser parser){
        return parser.parse(time, this.formatter);
    }

    interface DateParser {

        TemporalAccessor parse(String time, DateTimeFormatter formatter);
    }

    private static final class SmartDateTimeFormatterLoader extends CacheLoader<DateTimeFormatterTuple, SmartDateTimeFormatter> {

        @Override
        public SmartDateTimeFormatter load(DateTimeFormatterTuple tuple) throws Exception{
            SmartDateTimeFormatter formatter = new SmartDateTimeFormatter(tuple.getPattern(), tuple.getLocale());
            return formatter;
        }
    }
}
