package com.iwuyc.tools.commons.util.time;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.iwuyc.tools.commons.basic.type.DateTimeFormatterTuple;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SmartDateTimeFormatter {

    private static final LoadingCache<DateTimeFormatterTuple, SmartDateTimeFormatter> DATE_TIME_SMART_FORMATTER_CACHE;

    static{

        CacheLoader<DateTimeFormatterTuple, SmartDateTimeFormatter> smartCacheLoader = new SmartDateTimeFormatterLoader();
        CacheBuilder<Object, Object> smartCacheBuilder = CacheBuilder.newBuilder();
        smartCacheBuilder.expireAfterAccess(10, TimeUnit.MILLISECONDS);
        DATE_TIME_SMART_FORMATTER_CACHE = smartCacheBuilder.build(smartCacheLoader);
    }

    private final DateTimeFormatter formatter;
    private final DateTimeFormatterTuple tuple;

    public SmartDateTimeFormatter(DateTimeFormatterTuple tuple){
        this.tuple = tuple;
        this.formatter = DateTimeFormatter.ofPattern(tuple.getPattern(), tuple.getLocale());
    }

    public static SmartDateTimeFormatter create(String pattern, Locale locale){
        return create(DateTimeFormatterTuple.create(pattern, locale));
    }

    public static SmartDateTimeFormatter create(DateTimeFormatterTuple tuple){
        if(tuple == null){
            throw new NullPointerException("tuple can't null.");
        }
        return DATE_TIME_SMART_FORMATTER_CACHE.getUnchecked(tuple);
    }

    public String format(TemporalAccessor time){
        return this.formatter.format(time);
    }

    public ZonedDateTime parse(String time){
        log.debug("Parse method,pattern:{},time:{}", this.tuple.getPattern(), time);
        TemporalAccessor temporal = this.formatter.parse(time, temporalAccessor -> temporalAccessor);
        ZonedDateTime zonedDateTimeTemplates;
        if(this.tuple.getBaseTime() == null){
            zonedDateTimeTemplates = ZonedDateTime.now();
        }else {
            zonedDateTimeTemplates = ZonedDateTime.ofInstant(this.tuple.getBaseTime().toInstant(), DateFormatterConstants.DEFAULT_ZONE_ID);
        }

        ChronoField[] chronoFields = ChronoField.values();
        for(ChronoField item : chronoFields){
            if(temporal.isSupported(item)){
                zonedDateTimeTemplates = zonedDateTimeTemplates.with(item, item.getFrom(temporal));
            }
        }

        return zonedDateTimeTemplates;

    }

    private static final class SmartDateTimeFormatterLoader extends CacheLoader<DateTimeFormatterTuple, SmartDateTimeFormatter> {

        @Override
        public SmartDateTimeFormatter load(@ParametersAreNonnullByDefault DateTimeFormatterTuple tuple) throws Exception{
            return new SmartDateTimeFormatter(tuple);
        }
    }
}
