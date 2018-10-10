package com.iwuyc.tools.commons.basic.type;

import com.iwuyc.tools.commons.util.time.DateFormatterConstants;
import org.junit.Test;

import java.util.Locale;

public class DateTimeFormatterTupleTest {

    @Test
    public void test() {
        DateTimeFormatterTuple tuple = DateTimeFormatterTuple.create(DateFormatterConstants.DEFAULT, Locale.CHINA);
        System.out.println(tuple);
    }
}