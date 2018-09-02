package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.basic.type.DateTimeTuple;
import com.iwuyc.tools.commons.util.time.DateFormatterPattern;
import com.iwuyc.tools.commons.util.time.DateTimeBuilder;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class DateTimeBuilderTest {

    @Test
    public void nextDayOfMonth() {
        DateTimeBuilder builder = DateTimeBuilder.withTime("2020-10-25T00:26:20+0800");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.getLocalDateTime());

        builder = DateTimeBuilder.withTime("2020-10-26T00:26:20+0800");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.getLocalDateTime());

        builder = DateTimeBuilder.withTime("2020-10-27T00:26:20+0800");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.getLocalDateTime());

        builder = DateTimeBuilder.withTime("2020-10-27T00:26:20+0800");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.startWithDay().getLocalDateTime());
        System.out.println(builder.after(DateTimeTuple.create(-1, ChronoUnit.NANOS)).getLocalDateTime());

        builder = DateTimeBuilder.withTime("00:26:20","HH:mm:ss");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.getLocalDateTime());
    }

    @Test
    public void testLocale() {
        DateTimeBuilder builder = DateTimeBuilder.withTime("2018-08-25T00:26:20+0100");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.toDate());

        System.out.println(builder.format(DateFormatterPattern.DEFAULT));
    }

}