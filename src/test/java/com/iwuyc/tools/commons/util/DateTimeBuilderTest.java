package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.basic.type.DateTimeTuple;
import com.iwuyc.tools.commons.util.time.DateTimeBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class DateTimeBuilderTest {

    @Test
    public void nextDayOfMonth(){
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
    public void name(){
    }

    @Test
    public void testTime(){
        // 1970-01-01 23:59:59.999
        Date sourceTime = new Date(2735999999L);

        String timeStr = "23";
        DateTimeBuilder dateTimeBuilder = DateTimeBuilder.withTime(timeStr, "HH");
        Date result = dateTimeBuilder.toDate();
        Assert.assertTrue("格式化[HH]的数据异常。", equal(sourceTime, result, Calendar.HOUR_OF_DAY));
        Assert.assertEquals(timeStr, dateTimeBuilder.format());

        timeStr = "11 PM";
        dateTimeBuilder = DateTimeBuilder.withTime(timeStr, "hh a");
        result = dateTimeBuilder.toDate();
        Assert.assertTrue("格式化[hh a]的数据异常。", equal(sourceTime, result, Calendar.HOUR_OF_DAY, Calendar.AM_PM));
        Assert.assertEquals(timeStr, dateTimeBuilder.format());

        timeStr = "11:59 PM";
        dateTimeBuilder = DateTimeBuilder.withTime(timeStr, "hh:mm a");
        result = dateTimeBuilder.toDate();
        Assert.assertTrue("格式化[hh:mm a]的数据异常。", equal(sourceTime, result, Calendar.HOUR_OF_DAY, Calendar.AM_PM, Calendar.MINUTE));
        Assert.assertEquals(timeStr, dateTimeBuilder.format());

        timeStr = "11:59:59 PM";
        dateTimeBuilder = DateTimeBuilder.withTime(timeStr, "hh:mm:ss a");
        result = dateTimeBuilder.toDate();
        Assert.assertTrue("格式化[hh:mm:ss a]的数据异常。",
                equal(sourceTime, result, Calendar.HOUR_OF_DAY, Calendar.AM_PM, Calendar.MINUTE, Calendar.SECOND));
        Assert.assertEquals(timeStr, dateTimeBuilder.format());

        timeStr = "11:59:59.999 PM";
        dateTimeBuilder = DateTimeBuilder.withTime(timeStr, "hh:mm:ss.SSS a");
        result = dateTimeBuilder.toDate();
        Assert.assertTrue("格式化[hh:mm:ss.SSS a]的数据异常。",
                equal(sourceTime, result, Calendar.HOUR_OF_DAY, Calendar.AM_PM, Calendar.MINUTE, Calendar.SECOND));
        Assert.assertEquals(timeStr, dateTimeBuilder.format());

        timeStr = "59";
        dateTimeBuilder = DateTimeBuilder.withTime(timeStr, "mm");
        result = dateTimeBuilder.toDate();
        Assert.assertTrue("格式化[mm]的数据异常。", equal(sourceTime, result, Calendar.MINUTE));
        Assert.assertEquals(timeStr, dateTimeBuilder.format());

        timeStr = "59:59";
        dateTimeBuilder = DateTimeBuilder.withTime(timeStr, "mm:ss");
        result = dateTimeBuilder.toDate();
        Assert.assertTrue("格式化[mm:ss]的数据异常。", equal(sourceTime, result, Calendar.MINUTE, Calendar.SECOND));
        Assert.assertEquals(timeStr, dateTimeBuilder.format());

        timeStr = "59";
        dateTimeBuilder = DateTimeBuilder.withTime(timeStr, "ss");
        result = dateTimeBuilder.toDate();
        Assert.assertTrue("格式化[mm]的数据异常。", equal(sourceTime, result, Calendar.SECOND));
        Assert.assertEquals(timeStr, dateTimeBuilder.format());

        timeStr = "999";
        dateTimeBuilder = DateTimeBuilder.withTime(timeStr, "SSS");
        result = dateTimeBuilder.toDate();
        Assert.assertTrue("格式化[SSS]的数据异常。", equal(sourceTime, result, Calendar.MILLISECOND));
        Assert.assertEquals(timeStr, dateTimeBuilder.format());
    }

    @Test
    public void testYearMonthDay(){
        Date sourceDate = new Date(0);

        String dateStr = "1970";
        DateTimeBuilder dateTimeBuilder = DateTimeBuilder.withTime(dateStr, "yyyy");
        Date result = dateTimeBuilder.toDate();
        Assert.assertTrue("格式化[yyyy]的数据异常。", equal(sourceDate, result, Calendar.YEAR));
        Assert.assertEquals(dateStr, dateTimeBuilder.format());

        dateStr = "1970/01";
        dateTimeBuilder = DateTimeBuilder.withTime(dateStr, "yyyy/MM");
        result = dateTimeBuilder.toDate();
        Assert.assertTrue("格式化[yyyy/MM]的数据异常。", equal(sourceDate, result, Calendar.YEAR, Calendar.MONTH));
        Assert.assertEquals(dateStr, dateTimeBuilder.format());

        dateStr = "1970/01/01";
        dateTimeBuilder = DateTimeBuilder.withTime(dateStr, "yyyy/MM/dd");
        result = dateTimeBuilder.toDate();
        Assert.assertTrue("格式化[yyyy/MM/dd]的数据异常。", equal(sourceDate, result, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH));
        Assert.assertEquals(dateStr, dateTimeBuilder.format());

        dateStr = "01/01";
        dateTimeBuilder = DateTimeBuilder.withTime(dateStr, "MM/dd");
        result = dateTimeBuilder.toDate();
        Assert.assertTrue("格式化[MM/dd]的数据异常。", equal(sourceDate, result, Calendar.MONTH, Calendar.DAY_OF_MONTH));
        Assert.assertEquals(dateStr, dateTimeBuilder.format());

        dateStr = "01";
        dateTimeBuilder = DateTimeBuilder.withTime(dateStr, "dd");
        result = dateTimeBuilder.toDate();
        Assert.assertTrue("格式化[dd]的数据异常。", equal(sourceDate, result, Calendar.DAY_OF_MONTH));
        Assert.assertEquals(dateStr, dateTimeBuilder.format());

        dateStr = "01";
        dateTimeBuilder = DateTimeBuilder.withTime(dateStr, "MM");
        result = dateTimeBuilder.toDate();
        Assert.assertTrue("格式化[MM]的数据异常。", equal(sourceDate, result, Calendar.MONTH));
        Assert.assertEquals(dateStr, dateTimeBuilder.format());
    }

    private boolean equal(Date date1, Date date2, int... calendarFlag){
        Calendar date1Calendar = Calendar.getInstance();
        date1Calendar.setTime(date1);

        Calendar date2Calendar = Calendar.getInstance();
        date2Calendar.setTime(date2);

        for(int i : calendarFlag){
            if(date1Calendar.get(i) != date2Calendar.get(i)){
                return false;
            }
        }
        return true;
    }
}