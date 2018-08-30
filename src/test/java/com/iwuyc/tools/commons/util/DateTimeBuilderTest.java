package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.util.time.DateTimeBuilder;
import org.junit.Test;

public class DateTimeBuilderTest {

    @Test
    public void nextDayOfMonth() {
        DateTimeBuilder builder = DateTimeBuilder.withTime("2018-08-25T00:26:20+0800");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.getLocalDateTime());

        builder = DateTimeBuilder.withTime("2018-08-26T00:26:20+0800");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.getLocalDateTime());

        builder = DateTimeBuilder.withTime("2018-08-27T00:26:20+0800");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.format());

    }

    @Test
    public void testLocale() {
        DateTimeBuilder builder = DateTimeBuilder.withTime("2018-08-25T00:26:20+0800");
        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.toDate());
    }
}