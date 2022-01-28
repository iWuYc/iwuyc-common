package com.iwuyc.tools.commons.util.time;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.iwuyc.tools.commons.basic.type.DateTimeFormatterTuple;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SmartDateTimeFormatter {

    private static final LoadingCache<DateTimeFormatterTuple, SmartDateTimeFormatter> DATE_TIME_SMART_FORMATTER_CACHE;
    private static final Collection<ChronoField> CHRONO_FIELDS;

    static {

        CacheLoader<DateTimeFormatterTuple, SmartDateTimeFormatter> smartCacheLoader = new SmartDateTimeFormatterLoader();
        CacheBuilder<Object, Object> smartCacheBuilder = CacheBuilder.newBuilder();
        smartCacheBuilder.expireAfterAccess(10, TimeUnit.MILLISECONDS);
        DATE_TIME_SMART_FORMATTER_CACHE = smartCacheBuilder.build(smartCacheLoader);
        ArrayList<ChronoField> chronoFields = new ArrayList<>();
        chronoFields.add(ChronoField.YEAR);
        chronoFields.add(ChronoField.MONTH_OF_YEAR);
        chronoFields.add(ChronoField.DAY_OF_MONTH);
        chronoFields.add(ChronoField.HOUR_OF_DAY);
        chronoFields.add(ChronoField.MINUTE_OF_HOUR);
        chronoFields.add(ChronoField.SECOND_OF_MINUTE);
        chronoFields.add(ChronoField.MILLI_OF_SECOND);
        chronoFields.add(ChronoField.NANO_OF_SECOND);
        CHRONO_FIELDS = Collections.unmodifiableList(chronoFields);
    }

    private final DateTimeFormatter formatter;
    private final DateTimeFormatterTuple tuple;

    public SmartDateTimeFormatter(DateTimeFormatterTuple tuple) {
        this.tuple = tuple;
        this.formatter = DateTimeFormatter.ofPattern(tuple.getPattern(), tuple.getLocale());
    }

    public static SmartDateTimeFormatter create(String pattern, Locale locale) {
        return create(DateTimeFormatterTuple.create(pattern, locale));
    }

    public static SmartDateTimeFormatter create(DateTimeFormatterTuple tuple) {
        if (tuple == null) {
            throw new NullPointerException("tuple can't null.");
        }
        return DATE_TIME_SMART_FORMATTER_CACHE.getUnchecked(tuple);
    }

    public String format(TemporalAccessor time) {
        return this.formatter.format(time);
    }

    public ZonedDateTime parse(String time) {
        log.debug("Parse method,pattern:{},time:{}", this.tuple.getPattern(), time);
        TemporalAccessor dateTime = this.formatter.parse(time, temporal -> temporal);
        ZoneId zoneId = this.formatter.getZone() == null ? DateFormatterConstants.DEFAULT_ZONE_ID : this.formatter.getZone();
        if (dateTime.isSupported(ChronoField.OFFSET_SECONDS)) {
            return ZonedDateTime.ofInstant(Instant.from(dateTime), zoneId);
        }
        ZonedDateTime result = ZonedDateTime.now(zoneId);
        for (ChronoField chronoField : CHRONO_FIELDS) {
            if (!dateTime.isSupported(chronoField)) {
                continue;
            }
            result = result.with(chronoField, dateTime.get(chronoField));
        }
        return result;
    }

    private static final class SmartDateTimeFormatterLoader extends CacheLoader<DateTimeFormatterTuple, SmartDateTimeFormatter> {

        @Override
        public SmartDateTimeFormatter load(@ParametersAreNonnullByDefault @Nonnull DateTimeFormatterTuple tuple) {
            return new SmartDateTimeFormatter(tuple);
        }
    }
}
