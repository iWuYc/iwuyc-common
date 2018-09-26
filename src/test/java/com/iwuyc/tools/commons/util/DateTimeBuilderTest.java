package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.basic.type.DateTimeTuple;
import com.iwuyc.tools.commons.util.time.DateFormatterPattern;
import com.iwuyc.tools.commons.util.time.DateTimeBuilder;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

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

        builder = DateTimeBuilder.withTime("00:26:20", "HH:mm:ss");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.getLocalDateTime());
    }

    @Test
    public void testLocale() {
        DateTimeBuilder builder =
                DateTimeBuilder.withTime("2018-08-25T00:26:20+0000", DateFormatterPattern.DEFAULT, Locale.US);
        builder = builder.nextDayOfMonth(26);
        LocalDateTime localDateTime = builder.getLocalDateTime();
        ZonedDateTime zoneTime = localDateTime.atZone(ZoneId.of("+08"));
        System.out.println(zoneTime);
        System.out.println(zoneTime.withZoneSameInstant(ZoneId.of("+18")));
        System.out.println(builder.toDate());

        System.out.println(builder.format(DateFormatterPattern.DEFAULT));
    }

    @Test
    public void testZoneDateTime() {
        // TODO Neil zone time turn to localtime
        ZonedDateTime time = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("+09"));
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        System.out.println(time.format(pattern));
        System.out.println(time.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
    }

    @Test
    public void testTimeCalculator() {
        DateTimeBuilder builder = DateTimeBuilder.withTime("2018-07-27T00:00:00+0000", "yyyy-MM-dd'T'HH:mm:ssZ");
        builder.after(DateTimeTuple.create(1, ChronoUnit.DAYS));
        System.out.println(builder.format());
        System.out.println(builder.toDate(ZoneId.of("+07")));
        System.out.println(builder.toDate());
    }

}