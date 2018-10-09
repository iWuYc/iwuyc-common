package com.iwuyc.tools.commons.math;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.*;

public class RangeTest {

    @Test
    public void testSimple() {
        Range range = Range.compiler("[ 0,10]");
        assertNotNull(range);

        range = Range.compiler("( 0,10]");
        assertNotNull(range);
        range = Range.compiler("[ 0,10)");
        assertNotNull(range);
        range = Range.compiler("( 0,10)");
        assertNotNull(range);

        range = Range.compiler("( 0,max)");
        assertNotNull(range);
        assertFalse(range.inRange(-1));
        assertFalse(range.inRange(0));
        assertTrue(range.inRange(10));

        range = Range.compiler("( min,0)");
        assertNotNull(range);
        assertTrue(range.inRange(-1));
        assertFalse(range.inRange(0));
        assertFalse(range.inRange(10));

        System.out.println(range.toString());
    }

    @Test
    public void test() {
        Range range = Range.compiler("[0,10)|[20,30]|(40,50]");
        assertNotNull(range);
        assertFalse(range.inRange(-1));
        assertTrue(range.inRange(0));
        assertFalse(range.inRange(10));
        assertFalse(range.inRange(11));
        assertTrue(range.inRange(20));
        assertTrue(range.inRange(30));
        assertFalse(range.inRange(31));
        assertFalse(range.inRange(40));
        assertTrue(range.inRange(41));
        assertTrue(range.inRange(50));
        assertFalse(range.inRange(51));

    }

    @Test
    public void testBigNum() {
        Range range = Range.compiler("[0,10)|[20,30]|(40,50]");
        assertNotNull(range);
        assertFalse(range.inRange(new BigDecimal("-1")));
        assertTrue(range.inRange(new BigInteger("0")));
        assertFalse(range.inRange(new BigInteger("10")));
        assertFalse(range.inRange(new BigInteger("11")));
        assertTrue(range.inRange(new BigInteger("20")));
        assertTrue(range.inRange(new BigInteger("30")));
        assertFalse(range.inRange(new BigInteger("31")));
        assertFalse(range.inRange(new BigInteger("40")));
        assertTrue(range.inRange(new BigInteger("41")));
        assertTrue(range.inRange(new BigInteger("50")));
        assertFalse(range.inRange(new BigInteger("51")));

    }

    @Test
    public void test2() {
        Range range = Range.compiler("[0,10)||[20,30]|||(40,50]||");
        assertNotNull(range);
        assertFalse(range.inRange(-1));
        assertTrue(range.inRange(0));
        assertFalse(range.inRange(10));
        assertFalse(range.inRange(11));
        assertTrue(range.inRange(20));
        assertTrue(range.inRange(30));
        assertFalse(range.inRange(31));
        assertFalse(range.inRange(40));
        assertTrue(range.inRange(41));
        assertTrue(range.inRange(50));
        assertFalse(range.inRange(51));

    }

    @Test(expected = IllegalArgumentException.class)
    public void test3() {
        Range range = Range.compiler("[100,10)||[20,30]|||(40,50]||(min,max)|[10,min)");
        assertNotNull(range);
        assertFalse(range.inRange(-1));
        assertTrue(range.inRange(0));
        assertFalse(range.inRange(10));
        assertFalse(range.inRange(11));
        assertTrue(range.inRange(20));
        assertTrue(range.inRange(30));
        assertFalse(range.inRange(31));
        assertFalse(range.inRange(40));
        assertTrue(range.inRange(41));
        assertTrue(range.inRange(50));
        assertFalse(range.inRange(51));

    }

    @Test
    public void test4() {
        Range range = Range.compiler("[1,10)| ");
        System.out.println(range);

        range = Range.compiler(" | ");
        System.out.println(range);
        System.out.println(range.inRange(1));
        System.out.println(range.inRange(0));
        System.out.println(range.inRange(-1));
    }
}
