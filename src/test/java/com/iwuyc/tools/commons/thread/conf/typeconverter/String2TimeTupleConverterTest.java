package com.iwuyc.tools.commons.thread.conf.typeconverter;

import com.iwuyc.tools.commons.basic.type.TimeTuple;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class String2TimeTupleConverterTest {

    @Test
    public void converterData() {
        String2TimeTupleConverter converter = new String2TimeTupleConverter();
        TimeTuple timeTuple = converter.converterData("12ms", TimeTuple.class);
        System.out.println(timeTuple);

        timeTuple = converter.converterData("12", TimeTuple.class);
        System.out.println(timeTuple);

        System.out.println(converter.isSupport(TimeTuple.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test1() {
        String2TimeTupleConverter converter = new String2TimeTupleConverter();
        System.out.println(converter.converterData("12test", TimeTuple.class));
    }

    @Test
    public void isSupport() {
    }

    private static final Pattern UNIT_PATTERN = Pattern.compile("[A-Za-z]+");

}