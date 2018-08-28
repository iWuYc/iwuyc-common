package com.iwuyc.tools.commons.util.time;

import com.iwuyc.tools.commons.basic.type.DateTimeTuple;
import com.iwuyc.tools.commons.util.DateTimeUtils;
import lombok.Data;

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
@Data
public class DateTimeBuilder {
    private LocalDateTime localDateTime;
    private DateTimeFormatter formatter;

    public DateTimeBuilder(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public static DateTimeBuilder withTime(Date time) {
        Instant instant = time.toInstant();
        return withTime(LocalDateTime.from(instant));
    }

    public static DateTimeBuilder withTime(LocalDateTime time) {
        return new DateTimeBuilder(time);
    }

    public static DateTimeBuilder withTime(String time, DateTimeFormatter formatter) {
        TemporalAccessor temporalAccessor = formatter.parse(time);
        LocalDateTime dateTime = LocalDateTime.from(temporalAccessor);
        DateTimeBuilder builder = withTime(dateTime);
        builder.setFormatter(formatter);
        return builder;
    }

    public static DateTimeBuilder withTime(String time, String pattern, Locale locale) {
        DateTimeFormatter formatter = DateTimeUtils.getDateTimeFormatter(pattern, locale);
        return withTime(time, formatter);
    }

    public static DateTimeBuilder withTime(String time, String pattern) {
        return withTime(time, pattern, DateFormatterPattern.DEFAULT_LOCALE);
    }

    public static DateTimeBuilder withTime(String time) {
        return withTime(time, DateFormatterPattern.DEFAULT, DateFormatterPattern.DEFAULT_LOCALE);
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

    /**
     * 根据timeTuple计算过了多久时间
     *
     * @param timeTuple 时间的元组，包含时间长度跟时间单位
     * @return
     */
    public DateTimeBuilder after(DateTimeTuple timeTuple) {
        this.localDateTime = this.localDateTime.plus(timeTuple.getTime(), timeTuple.getTemporalUnit());
        return this;
    }

    public DateTimeBuilder nextDayOfMonth(int dayOfMonth) {
        LocalDateTime now = this.localDateTime;
        LocalDateTime next = now.withDayOfMonth(1).plusDays(dayOfMonth - 1);
        LocalDate nowDate = now.toLocalDate();
        LocalDate nextDate = next.toLocalDate();
        if (nextDate.isAfter(nowDate)) {
            this.localDateTime = next;
        } else {
            this.localDateTime = next.plusMonths(1);
        }
        return this;
    }

    public String format(DateTimeFormatter formatter) {
        return this.localDateTime.format(formatter);
    }

    public String format(String pattern, Locale locale) {
        return format(DateTimeUtils.getDateTimeFormatter(pattern, locale));
    }

    public String format(String pattern) {
        return format(pattern, DateFormatterPattern.DEFAULT_LOCALE);
    }

    public String format() {
        if (null == this.formatter) {
            this.formatter =
                DateTimeUtils.getDateTimeFormatter(DateFormatterPattern.DEFAULT, DateFormatterPattern.DEFAULT_LOCALE);
        }
        return format(this.formatter);
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public void setFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }
}
