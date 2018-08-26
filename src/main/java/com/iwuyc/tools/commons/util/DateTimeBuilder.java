package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.basic.type.DateTimeTuple;
import com.iwuyc.tools.commons.util.time.DateFormatterPattern;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

/**
 * 日期时间构建类
 *
 * @author Neil
 */
public class DateTimeBuilder {
    public static final Locale DEFAULT_LOCALE = Locale.PRC;
    private LocalDateTime localDateTime;

    public DateTimeBuilder(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public static DateTimeBuilder withTime(LocalDateTime time) {
        return new DateTimeBuilder(time);
    }

    public static DateTimeBuilder withTime(String time, String pattern, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);
        return withTime(time, formatter);
    }

    public static DateTimeBuilder withTime(String time, String pattern) {
        return withTime(time, pattern, DEFAULT_LOCALE);
    }

    public static DateTimeBuilder withTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateFormatterPattern.DEFAULT, DEFAULT_LOCALE);
        TemporalAccessor temporalAccessor = formatter.parse(time);
        return withTime(LocalDateTime.from(temporalAccessor));
    }

    public static DateTimeBuilder withTime(Date time) {
        Instant instant = time.toInstant();
        return withTime(LocalDateTime.from(instant));
    }

    public static DateTimeBuilder withTime(String time, DateTimeFormatter pattern) {
        TemporalAccessor temporalAccessor = pattern.parse(time);
        LocalDateTime dateTime = LocalDateTime.from(temporalAccessor);
        return withTime(dateTime);
    }

    public DateTimeBuilder withYears(int years) {
        this.localDateTime = this.localDateTime.withYear(years);
        return this;
    }

    public DateTimeBuilder withMonthOfYear(long months) {
        this.localDateTime = this.localDateTime.withMonth(1).plusMonths(months);
        return this;
    }

    public DateTimeBuilder withDayOfMonth(long day) {
        this.localDateTime = this.localDateTime.withDayOfMonth(1).plusDays(day);
        return this;
    }

    public DateTimeBuilder withHourOfDay(long hour) {
        this.localDateTime = this.localDateTime.withHour(0).plusHours(hour);
        return this;
    }

    public DateTimeBuilder withMinuteOfHour(long minute) {
        this.localDateTime = this.localDateTime.withMinute(0).plusMinutes(minute);
        return this;
    }

    public DateTimeBuilder withSecondOfMinute(long second) {
        this.localDateTime = this.localDateTime.withSecond(0).plusSeconds(second);
        return this;
    }

    public DateTimeBuilder withNanosecondOfSecond(long nanosecond) {
        this.localDateTime = this.localDateTime.withNano(0).plusNanos(nanosecond);
        return this;
    }

    public DateTimeBuilder after(DateTimeTuple timeTuple) {
        this.localDateTime = this.localDateTime.plus(timeTuple.getTime(), timeTuple.getTemporalUnit());
        return this;
    }

    public DateTimeBuilder nextDayOfMonth(int dayOfMonth) {
        LocalDateTime now = this.localDateTime;
        LocalDateTime next = now.withDayOfMonth(1).plusDays(dayOfMonth - 1);
        LocalDate nowDate = now.toLocalDate();
        LocalDate nextDate = next.toLocalDate();
        if (nowDate.isAfter(nextDate)) {
            this.localDateTime = next.plusMonths(1);
        } else {
            this.localDateTime = next;
        }
        return this;
    }

    public String format(DateTimeFormatter formatter) {
        return this.localDateTime.format(formatter);
    }

    public String format(String pattern, Locale locale) {
        return format(DateTimeFormatter.ofPattern(pattern, locale));
    }

    public String format(String pattern) {
        return format(pattern, DEFAULT_LOCALE);
    }

    public String format() {
        return format(DateFormatterPattern.DEFAULT);
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
