package com.iwuyc.tools.commons.util.time;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Pattern;

public class SmartDateTimeFormatterUtilTest {

    @Test
    public void test() throws Exception {
        Pattern pattern = Pattern.compile(".*([yMd]+.*)+");
        Assert.assertTrue(pattern.matcher("y").matches());
        Assert.assertTrue(pattern.matcher("M").matches());
        Assert.assertTrue(pattern.matcher("d").matches());
        Assert.assertTrue(pattern.matcher("yyyyMMdd").matches());
        Assert.assertTrue(pattern.matcher("yyyy-MM-ddMM").matches());
        Assert.assertFalse(pattern.matcher("hh:mm:ss.SSS").matches());
        Assert.assertTrue(pattern.matcher("yyyy-MM-dd hh:mm:ss.SSS").matches());

        pattern = Pattern.compile(".*([HhmsS]+.*)+");
        Assert.assertTrue(pattern.matcher("h").matches());
        Assert.assertTrue(pattern.matcher("H").matches());
        Assert.assertTrue(pattern.matcher("m").matches());
        Assert.assertTrue(pattern.matcher("s").matches());
        Assert.assertTrue(pattern.matcher("S").matches());
        Assert.assertTrue(pattern.matcher("Hh").matches());
        Assert.assertFalse(pattern.matcher("yyyyMMdd").matches());
        Assert.assertFalse(pattern.matcher("yyyy-MM-ddMM").matches());
        Assert.assertTrue(pattern.matcher("hh:mm:ss.SSS").matches());
        Assert.assertTrue(pattern.matcher("yyyy-MM-dd hh:mm:ss.SSS").matches());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy HH");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse("2018-09-02 13:28:27");
        LocalDateTime localDateTime =
                LocalDateTime.ofInstant(date.toInstant(), DateFormatterConstants.DEFAULT_ZONE_OFFSET);
        Assert.assertEquals("2018 13", localDateTime.format(formatter));
    }

    @Test
    public void testIsDateTime() {
    }
}