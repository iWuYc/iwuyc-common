package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.basic.type.DateTimeTuple;
import com.iwuyc.tools.commons.util.time.DateTimeBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class DateTimeBuilderTest {
    @Before
    public void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    @Test
    public void nextDayOfMonth() {
        DateTimeBuilder builder = DateTimeBuilder.withTime("2020-10-25T00:26:20+0800");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.getZonedDateTime());

        builder = DateTimeBuilder.withTime("2020-10-26T00:26:20+0800");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.getZonedDateTime());

        builder = DateTimeBuilder.withTime("2020-10-27T00:26:20+0800");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.getZonedDateTime());

        builder = DateTimeBuilder.withTime("2020-10-27T00:26:20+0800");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.startWithDay().getZonedDateTime());
        System.out.println(builder.after(DateTimeTuple.create(-1, ChronoUnit.NANOS)).getZonedDateTime());

        builder = DateTimeBuilder.withTime("00:26:20", "HH:mm:ss");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.getZonedDateTime());
    }

    @Test
    public void testYearMonthDay() {
        Date sourceDate = new Date(0);

        String dateStr = "1970";
        DateTimeBuilder dateTimeBuilder = DateTimeBuilder.withTime(dateStr, "yyyy");
        Date result = dateTimeBuilder.toDate();
        assertTrue("格式化[yyyy]的数据异常。", equal(sourceDate, result, Calendar.YEAR));
        assertEquals(dateStr, dateTimeBuilder.format());

        dateStr = "1970/01";
        dateTimeBuilder = DateTimeBuilder.withTime(dateStr, "yyyy/MM");
        result = dateTimeBuilder.toDate();
        assertTrue("格式化[yyyy/MM]的数据异常。", equal(sourceDate, result, Calendar.YEAR, Calendar.MONTH));
        assertEquals(dateStr, dateTimeBuilder.format());

        dateStr = "1970/01/01";
        dateTimeBuilder = DateTimeBuilder.withTime(dateStr, "yyyy/MM/dd");
        result = dateTimeBuilder.toDate();
        assertTrue("格式化[yyyy/MM/dd]的数据异常。", equal(sourceDate, result, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH));
        assertEquals(dateStr, dateTimeBuilder.format());

        dateStr = "01/01";
        dateTimeBuilder = DateTimeBuilder.withTime(dateStr, "MM/dd");
        result = dateTimeBuilder.toDate();
        assertTrue("格式化[MM/dd]的数据异常。", equal(sourceDate, result, Calendar.MONTH, Calendar.DAY_OF_MONTH));
        assertEquals(dateStr, dateTimeBuilder.format());

        dateStr = "01";
        dateTimeBuilder = DateTimeBuilder.withTime(dateStr, "dd");
        result = dateTimeBuilder.toDate();
        assertTrue("格式化[dd]的数据异常。", equal(sourceDate, result, Calendar.DAY_OF_MONTH));
        assertEquals(dateStr, dateTimeBuilder.format());

        dateStr = "01";
        dateTimeBuilder = DateTimeBuilder.withTime(dateStr, "MM");
        result = dateTimeBuilder.toDate();
        assertTrue("格式化[MM]的数据异常。", equal(sourceDate, result, Calendar.MONTH));
        assertEquals(dateStr, dateTimeBuilder.format());
    }

    private boolean equal(Date date1, Date date2, int... calendarFlag) {
        Calendar date1Calendar = Calendar.getInstance();
        date1Calendar.setTime(date1);

        Calendar date2Calendar = Calendar.getInstance();
        date2Calendar.setTime(date2);

        for (int i : calendarFlag) {
            if (date1Calendar.get(i) != date2Calendar.get(i)) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void builder() {
        final DateTimeBuilder dateTimeBuilder = DateTimeBuilder.withTime(new Date(0));
        assertEquals("1970-01-01T08:00:00+0800", dateTimeBuilder.format());
        assertEquals("1990-01-01T08:00:00+0800", dateTimeBuilder.withYears(1990).format());
        assertEquals("1991-04-01T08:00:00+0800", dateTimeBuilder.withMonthOfYear(16).format());
        assertEquals("1991-05-31T08:00:00+0900", dateTimeBuilder.withDayOfMonth(61).format());
        assertEquals("1991-06-01T01:00:00+0900", dateTimeBuilder.withHourOfDay(25).format());
        assertEquals("1991-06-01T01:30:00+0900", dateTimeBuilder.withMinuteOfHour(30).format());
        assertEquals("1991-06-01T01:30:33+0900", dateTimeBuilder.withSecondOfMinute(33).format());
        assertEquals("1991-06-01T01:30:33+0900", dateTimeBuilder.withNanosecondOfSecond(1024L).format());
        assertEquals("1991-05-31T01:30:33+0900", dateTimeBuilder.before(DateTimeTuple.create(1, ChronoUnit.DAYS)).format());
        assertNotNull(dateTimeBuilder.getFormatter());

        assertEquals("1970-01-01T23:59:59+0800", DateTimeBuilder.withTime(new Date(0)).endWithDay().format());
    }

    @Test
    public void format() {
        final DateTimeBuilder dateTimeBuilder = DateTimeBuilder.withTime(new Date(0));
        assertEquals("1970-01-01 08:00:00.000", dateTimeBuilder.format("yyyy-MM-dd HH:mm:ss.SSS"));
        assertEquals("1970-01-01 08:00:00.000", dateTimeBuilder.format("yyyy-MM-dd HH:mm:ss.SSS", Locale.UK));
    }
}