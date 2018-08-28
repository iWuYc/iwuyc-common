package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.util.time.DateTimeBuilder;
import org.junit.Test;

import java.time.LocalDateTime;

public class DateTimeBuilderTest {

    @Test
    public void nextDayOfMonth() {
        DateTimeBuilder builder = DateTimeBuilder.withTime("2018-08-27T00:26:20+0800");

        builder = builder.nextDayOfMonth(26);
        System.out.println(builder.getLocalDateTime());

        builder = DateTimeBuilder.withTime("2018-08-27T00:26:20+0800");
        builder = builder.nextDayOfMonth(27);
        System.out.println(builder.getLocalDateTime());

        builder = DateTimeBuilder.withTime("2018-08-27T00:26:20+0800");
        builder = builder.nextDayOfMonth(28);
        System.out.println(builder.getLocalDateTime());

        builder = DateTimeBuilder.withTime("2018-08-27 00:00:00", "yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = builder.getLocalDateTime();
        System.out.println(localDateTime);
    }
}