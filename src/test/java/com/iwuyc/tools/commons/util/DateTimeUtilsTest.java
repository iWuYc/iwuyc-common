package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.basic.type.DateTimeFormatterTuple;
import com.iwuyc.tools.commons.basic.type.DateTimeTuple;
import com.iwuyc.tools.commons.util.time.DateTimeBuilder;
import com.iwuyc.tools.commons.util.time.DateTimeFormatterPattern;
import org.junit.Before;
import org.junit.Test;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DateTimeUtilsTest {

    @Before
    public void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    /**
     *
     */
    private static final String makeRelation = "2018-08-18";

    @Test
    public void test() {
        Date beginTime = DateTimeUtils.parse(makeRelation, DateTimeFormatterPattern.TIMEZONE_SHORT_DATE_YEAR_FORMATTER);
        DateTimeBuilder builder = DateTimeBuilder.withTime(beginTime);
        builder.after(DateTimeTuple.create(100, ChronoUnit.DAYS));
        System.out.println(builder.format());
    }

    @Test
    public void testDatetime() {
        Date now = new Date();
        DateTimeBuilder dateTimeBuilder = DateTimeBuilder.withTime(now);
        String pattern = "yyyy-MM-dd HH:mm:ss";
        String dateTimeStr = dateTimeBuilder.format(pattern);
        System.out.println(dateTimeStr);

        dateTimeBuilder = DateTimeBuilder.withTime(dateTimeStr, pattern);
        System.out.println(dateTimeBuilder.toDate());
    }

    @Test
    public void getDateTimeFormatter() {
        DateTimeFormatterTuple tuple = DateTimeFormatterTuple.create("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.CHINA);

        DateTimeFormatter dateTimeFormatter = DateTimeUtils.getDateTimeFormatter(tuple);
        TemporalAccessor temporalAccessor = dateTimeFormatter.parse("1991-05-31T08:59:30.010+0800");
        assertEquals(1991, temporalAccessor.get(ChronoField.YEAR));
        assertEquals(5, temporalAccessor.get(ChronoField.MONTH_OF_YEAR));
        assertEquals(31, temporalAccessor.get(ChronoField.DAY_OF_MONTH));
        assertEquals(8, temporalAccessor.get(ChronoField.HOUR_OF_DAY));
        assertEquals(59, temporalAccessor.get(ChronoField.MINUTE_OF_HOUR));
        assertEquals(30, temporalAccessor.get(ChronoField.SECOND_OF_MINUTE));
        assertEquals(10, temporalAccessor.get(ChronoField.MILLI_OF_SECOND));
        assertEquals(8 * 60 * 60, temporalAccessor.get(ChronoField.OFFSET_SECONDS));


        dateTimeFormatter = DateTimeUtils.getDateTimeFormatter("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.CHINA);
        temporalAccessor = dateTimeFormatter.parse("1991-05-31T08:59:30.010+0800");
        assertEquals(1991, temporalAccessor.get(ChronoField.YEAR));
        assertEquals(5, temporalAccessor.get(ChronoField.MONTH_OF_YEAR));
        assertEquals(31, temporalAccessor.get(ChronoField.DAY_OF_MONTH));
        assertEquals(8, temporalAccessor.get(ChronoField.HOUR_OF_DAY));
        assertEquals(59, temporalAccessor.get(ChronoField.MINUTE_OF_HOUR));
        assertEquals(30, temporalAccessor.get(ChronoField.SECOND_OF_MINUTE));
        assertEquals(10, temporalAccessor.get(ChronoField.MILLI_OF_SECOND));
        assertEquals(8 * 60 * 60, temporalAccessor.get(ChronoField.OFFSET_SECONDS));

        dateTimeFormatter = DateTimeUtils.getDateTimeFormatter("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        temporalAccessor = dateTimeFormatter.parse("1991-05-31T08:59:30.010+0800");
        assertEquals(1991, temporalAccessor.get(ChronoField.YEAR));
        assertEquals(5, temporalAccessor.get(ChronoField.MONTH_OF_YEAR));
        assertEquals(31, temporalAccessor.get(ChronoField.DAY_OF_MONTH));
        assertEquals(8, temporalAccessor.get(ChronoField.HOUR_OF_DAY));
        assertEquals(59, temporalAccessor.get(ChronoField.MINUTE_OF_HOUR));
        assertEquals(30, temporalAccessor.get(ChronoField.SECOND_OF_MINUTE));
        assertEquals(10, temporalAccessor.get(ChronoField.MILLI_OF_SECOND));
        assertEquals(8 * 60 * 60, temporalAccessor.get(ChronoField.OFFSET_SECONDS));
    }

    @Test
    public void parse() {

        Date date = DateTimeUtils.parse("1991-05-31T08:59:30+0800");
        //1991-05-31 09:59:30 CST
        Date targetTime = new Date(675651570000L);
        assertEquals(targetTime, date);

        date = DateTimeUtils.parse("1991-05-31T08:59:30.123+0800", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        targetTime = new Date(675651570123L);
        assertEquals(targetTime, date);

        date = DateTimeUtils.parse("2022-05-31T08:59:30.123+0800", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        targetTime = new Date(1653958770123L);
        assertEquals(targetTime, date);
    }

    @Test
    public void format() {
        Date targetTime = new Date(675651570123L);

        String format = DateTimeUtils.format(targetTime);
        assertEquals("1991-05-31T09:59:30+0900", format);

        format = DateTimeUtils.format(targetTime, "yyyy-MM-dd HH:mm:ss.SSS");
        assertEquals("1991-05-31 09:59:30.123", format);
    }

    @Test
    public void now() {
        assertNotNull(DateTimeUtils.now());
    }
}