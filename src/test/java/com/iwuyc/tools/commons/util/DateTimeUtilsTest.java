package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.basic.type.DateTimeTuple;
import com.iwuyc.tools.commons.util.time.DateTimeBuilder;
import com.iwuyc.tools.commons.util.time.DateTimeFormatterPattern;
import org.junit.Test;

import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateTimeUtilsTest {
    /**
     * 认识
     */
    private static final String begin = "2018-07-27";
    /**
     * 确立关系
     */
    private static final String makeRelation = "2018-08-18";

    @Test
    public void test() {
        Date beginTime = DateTimeUtils.parse(makeRelation, DateTimeFormatterPattern.TIMEZONE_SHORT_DATE_YEAR_FORMATTER);
        DateTimeBuilder builder = DateTimeBuilder.withTime(beginTime);
        builder.after(DateTimeTuple.create(100, ChronoUnit.DAYS));
        System.out.println(builder.format());
    }
}