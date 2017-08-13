package com.iwuyc.tools.commons.math;

import static org.junit.Assert.*;

import org.junit.Test;

public class RangeTest
{

    @Test
    public void testSimple()
    {
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

    }

    @Test
    public void test()
    {
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

}
