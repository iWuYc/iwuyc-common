package com.iwuyc.tools.commons.util.time;

import com.iwuyc.tools.commons.util.time.converter.TemporalConverterCache;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.regex.Pattern;

public class SmartDateTimeFormatterUtilTest {

    @Test
    public void test() throws Exception {
        Pattern pattern = Pattern.compile(".*([yMd]+.*)+");
        System.out.println(pattern.matcher("y").matches());
        System.out.println(pattern.matcher("M").matches());
        System.out.println(pattern.matcher("d").matches());
        System.out.println(pattern.matcher("yyyyMMdd").matches());
        System.out.println(pattern.matcher("yyyy-MM-ddMM").matches());
        System.out.println(pattern.matcher("hh:mm:ss.SSS").matches());
        System.out.println(pattern.matcher("yyyy-MM-dd hh:mm:ss.SSS").matches());

        System.out.println("*************************************************");

        pattern = Pattern.compile(".*([HhmsS]+.*)+");
        System.out.println(pattern.matcher("h").matches());
        System.out.println(pattern.matcher("H").matches());
        System.out.println(pattern.matcher("m").matches());
        System.out.println(pattern.matcher("s").matches());
        System.out.println(pattern.matcher("S").matches());
        System.out.println(pattern.matcher("Hh").matches());
        System.out.println(pattern.matcher("yyyyMMdd").matches());
        System.out.println(pattern.matcher("yyyy-MM-ddMM").matches());
        System.out.println(pattern.matcher("hh:mm:ss.SSS").matches());
        System.out.println(pattern.matcher("yyyy-MM-dd hh:mm:ss.SSS").matches());
        //        System.out.println(pattern.matcher("HHHhhh").matches());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy HH");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse("2018-09-02 13:28:27");
        LocalDateTime localDateTime =
            LocalDateTime.ofInstant(date.toInstant(), DateFormatterConstants.DEFAULT_ZONE_OFFSET);
        System.out.println(localDateTime.format(formatter));
    }

    @Test
    public void testIsDateTime() {
        Year year = Year.now();
        System.out.println(year.isSupported(ChronoField.YEAR));
        System.out.println(year.isSupported(ChronoField.MONTH_OF_YEAR));

        Temporal result = new TemporalConverterCache.MonthConverter().parse("1", null);
        System.out.println(result.isSupported(ChronoField.YEAR));
        System.out.println(result.isSupported(ChronoField.MONTH_OF_YEAR));
    }
}