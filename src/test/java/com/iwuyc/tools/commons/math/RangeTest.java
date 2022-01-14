package com.iwuyc.tools.commons.math;

import com.iwuyc.tools.commons.exception.ExpressionException;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

        range = Range.compiler("( 0,max]");
        assertTrue(range.inRange(Integer.MAX_VALUE));

        range = Range.compiler("( min,0)");
        assertNotNull(range);
        assertTrue(range.inRange(-1));
        assertFalse(range.inRange(0));
        assertFalse(range.inRange(10));

        range = Range.compiler("[min,max]");
        assertTrue(range.inRange(Long.MAX_VALUE));
        assertTrue(range.inRange(Long.MIN_VALUE));
        System.out.println(range);
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

    @Test
    public void test3() {
        try {
            Range range = Range.compiler("[100,10)||[20,30]|||(40,50]||(min,max)|[10,min)");
            assert range == null;
        } catch (ExpressionException e) {
            assertTrue(true);
        }
    }

    @Test
    public void test4() {
        Range range = Range.compiler("[1,10)| ");
        assertFalse(range.inRange(0));
        assertTrue(range.inRange(1));
        assertFalse(range.inRange(10));
        assertFalse(range.inRange(11));

        range = Range.compiler(" | ");
        assertFalse(range.inRange(1));
        assertFalse(range.inRange(0));
        assertFalse(range.inRange(-1));

        range = Range.compiler("[1,10)| ", false);
        assertFalse(range.inRange(0));
        assertTrue(range.inRange(1));
        assertFalse(range.inRange(10));
        assertFalse(range.inRange(11));
    }

    @Test(expected = ExpressionException.class)
    public void errorTest() {
        final Range range = Range.compiler("[1,0]");
    }

    @SuppressWarnings({"ALL"})
    @Test
    public void testBoundaryNumber() throws Exception {
        final Field minField = Range.class.getDeclaredField("MIN");
        final Field maxField = Range.class.getDeclaredField("MAX");
        minField.setAccessible(true);
        maxField.setAccessible(true);

        final Object min = minField.get(Range.class);
        final Object max = maxField.get(Range.class);

        assertEquals(min, min);

        assertNotEquals(min, null);
        assertNotEquals(min, new Object());
        assertNotEquals(min, max);

        assertEquals(0, min.hashCode());
        assertEquals(1, max.hashCode());

    }
}
