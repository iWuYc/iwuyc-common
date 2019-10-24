package com.iwuyc.tools.commons.util.time;

import com.iwuyc.tools.commons.basic.type.DateTimeTuple;
import lombok.Data;

import java.time.*;
import java.util.Date;
import java.util.Locale;

/**
 * 日期时间构建类
 *
 * @author Neil
 */
@Data
public class DateTimeBuilder {
    private ZonedDateTime zonedDateTime;
    private SmartDateTimeFormatter formatter;

    public DateTimeBuilder(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    public static DateTimeBuilder withTime(Date time) {
        Instant instant = time.toInstant();
        return withTime(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()));
    }

    public static DateTimeBuilder withTime(ZonedDateTime time) {
        return new DateTimeBuilder(time);
    }

    public static DateTimeBuilder withTime(String time, SmartDateTimeFormatter formatter) {
        ZonedDateTime dateTime = formatter.parse(time);
        DateTimeBuilder builder = withTime(dateTime);
        builder.setFormatter(formatter);
        return builder;
    }

    public static DateTimeBuilder withTime(String time, String pattern, Locale locale) {
        SmartDateTimeFormatter formatter = SmartDateTimeFormatter.create(pattern, locale);
        return withTime(time, formatter);
    }

    public static DateTimeBuilder withTime(String time, String pattern) {
        return withTime(time, pattern, DateFormatterConstants.DEFAULT_LOCALE);
    }

    public static DateTimeBuilder withTime(String time) {
        return withTime(time, DateFormatterConstants.DEFAULT_PATTERN, DateFormatterConstants.DEFAULT_LOCALE);
    }

    public DateTimeBuilder withYears(int years) {
        this.zonedDateTime = this.zonedDateTime.withYear(years);
        return this;
    }

    public DateTimeBuilder withMonthOfYear(long months) {
        this.zonedDateTime = this.zonedDateTime.withMonth(1).plusMonths(months);
        return this;
    }

    public DateTimeBuilder withDayOfMonth(long day) {
        this.zonedDateTime = this.zonedDateTime.withDayOfMonth(1).plusDays(day);
        return this;
    }

    public DateTimeBuilder withHourOfDay(long hour) {
        this.zonedDateTime = this.zonedDateTime.withHour(0).plusHours(hour);
        return this;
    }

    public DateTimeBuilder withMinuteOfHour(long minute) {
        this.zonedDateTime = this.zonedDateTime.withMinute(0).plusMinutes(minute);
        return this;
    }

    public DateTimeBuilder withSecondOfMinute(long second) {
        this.zonedDateTime = this.zonedDateTime.withSecond(0).plusSeconds(second);
        return this;
    }

    public DateTimeBuilder withNanosecondOfSecond(long nanosecond) {
        this.zonedDateTime = this.zonedDateTime.withNano(0).plusNanos(nanosecond);
        return this;
    }

    /**
     * 根据timeTuple计算过了多久时间
     *
     * @param timeTuple 时间的元组，包含时间长度跟时间单位
     * @return 当前的构建实例
     */
    public DateTimeBuilder after(DateTimeTuple timeTuple) {
        this.zonedDateTime = this.zonedDateTime.plus(timeTuple.getTime(), timeTuple.getTemporalUnit());
        return this;
    }

    /**
     * 根据timeTuple计算在多久之前的时间
     *
     * @param timeTuple 时间的元组，包含时间长度跟时间单位
     * @return 当前的构建实例
     */
    public DateTimeBuilder before(DateTimeTuple timeTuple) {
        this.zonedDateTime = this.zonedDateTime.plus(-timeTuple.getTime(), timeTuple.getTemporalUnit());
        return this;
    }


    /**
     * 将时间设置为当天的起始时间
     *
     * @return 当前的构建实例
     */
    public DateTimeBuilder startWithDay() {
        this.zonedDateTime = zonedDateTime.with(LocalTime.MIN);
        return this;
    }

    /**
     * 将时间设置为当天最后的一纳秒
     *
     * @return 当前的构建实例
     */
    public DateTimeBuilder endWithDay() {
        this.startWithDay().after(DateTimeTuple.CHRONO_ONE_DAYS).before(DateTimeTuple.CHRONO_ONE_NANOSECOND);
        return this;
    }

    /**
     * 获取当前时间为起点，下一个指定“day”的时间。<br />
     * Example:<br />
     * this.zonedDateTime = 2018-08-26 13:14:00;<br />
     * input:dayOfMonth = 26; <br />
     * output:this.zonedDateTime = 2018-09-26 13:14:00;
     *
     * @param dayOfMonth 指定的“day”
     * @return 获取到的时间
     */
    public DateTimeBuilder nextDayOfMonth(int dayOfMonth) {
        ZonedDateTime originTime = this.zonedDateTime;
        ZonedDateTime next = originTime.withDayOfMonth(1).plusDays(dayOfMonth - 1);
        LocalDate nowDate = originTime.toLocalDate();
        LocalDate nextDate = next.toLocalDate();
        if (nextDate.isAfter(nowDate)) {
            this.zonedDateTime = next;
        } else {
            this.zonedDateTime = next.plusMonths(1);
        }
        return this;
    }

    public String format(SmartDateTimeFormatter formatter) {
        return formatter.format(this.zonedDateTime);
    }

    public String format(String pattern, Locale locale) {
        return format(SmartDateTimeFormatter.create(pattern, locale));
    }

    public String format(String pattern) {
        return format(pattern, DateFormatterConstants.DEFAULT_LOCALE);
    }

    public String format() {
        if (null == this.formatter) {
            this.formatter =
                    SmartDateTimeFormatter.create(DateFormatterConstants.DEFAULT_PATTERN, DateFormatterConstants.DEFAULT_LOCALE);
        }
        return format(this.formatter);
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public SmartDateTimeFormatter getFormatter() {
        return formatter;
    }

    public void setFormatter(SmartDateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public Date toDate() {
        return toDate(ZoneId.systemDefault());
    }

    public Date toDate(ZoneId zoneId) {
        ZonedDateTime zonedDateTime = this.zonedDateTime.withZoneSameInstant(zoneId);
        ZoneOffset zoneOffset = zonedDateTime.getOffset();
        return Date.from(zonedDateTime.toLocalDateTime().toInstant(zoneOffset));
    }
}
