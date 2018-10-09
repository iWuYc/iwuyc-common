package com.iwuyc.tools.commons.util;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NumberUtilsTest {
    @Test
    public void test() {
        assertFalse(NumberUtils.isNumberClass(null));
        assertTrue(NumberUtils.isNumberClass(int.class));
        assertTrue(NumberUtils.isNumberClass(Integer.class));

        assertFalse(NumberUtils.isNumber("1"));
        assertTrue(NumberUtils.isNumber(1));
        assertTrue(NumberUtils.isNumber(1L));
        assertTrue(NumberUtils.isNumber(1.1D));

        System.out.println(NumberUtils.parse("1", float.class));
        System.out.println(NumberUtils.parse("1", double.class));
        System.out.println(NumberUtils.parse("1", int.class));
        System.out.println(NumberUtils.parse("1", long.class));
        System.out.println(NumberUtils.parse("1", BigDecimal.class));
        System.out.println(NumberUtils.parse("1", BigInteger.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testException() {
        NumberUtils.parse("1", NumberTest.class);
    }

    static class NumberTest extends Number {
        @Override
        public int intValue() {
            return 0;
        }

        @Override
        public long longValue() {
            return 0;
        }

        @Override
        public float floatValue() {
            return 0;
        }

        @Override
        public double doubleValue() {
            return 0;
        }
    }
}