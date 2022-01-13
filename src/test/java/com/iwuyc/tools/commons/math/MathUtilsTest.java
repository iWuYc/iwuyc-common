package com.iwuyc.tools.commons.math;

import com.google.common.base.Stopwatch;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MathUtilsTest {

    @Before
    public void setUp() {
        Class<MathUtils> clazz = MathUtils.class;
        assertNotNull(clazz);
    }

    @Test
    public void translatorTest() {
        long num = -1111123456789L;
        String numStr = MathUtils.numberTranslation(num);
        final String target = "负壹万壹仟壹佰壹拾壹亿贰仟叁佰肆拾伍万陆仟柒佰捌拾玖";
        assertEquals(target, numStr);
    }

    @Test
    public void translatorTest1() {
        long num = 0;
        String numStr = MathUtils.numberTranslation(num);
        assertEquals("零", numStr);

        num = 100;
        numStr = MathUtils.numberTranslation(num);
        assertEquals("壹佰", numStr);

        num = 1000;
        numStr = MathUtils.numberTranslation(num);
        assertEquals("壹仟", numStr);

        num = 10000;
        numStr = MathUtils.numberTranslation(num);
        assertEquals("壹万", numStr);

        num = 101;
        numStr = MathUtils.numberTranslation(num);
        assertEquals("壹佰零壹", numStr);

        num = 1001;
        numStr = MathUtils.numberTranslation(num);
        assertEquals("壹仟零壹", numStr);

        num = 1010;
        numStr = MathUtils.numberTranslation(num);
        assertEquals("壹仟零壹拾", numStr);

        num = 10100;
        numStr = MathUtils.numberTranslation(num);
        assertEquals("壹万零壹佰", numStr);

        num = 10101;
        numStr = MathUtils.numberTranslation(num);
        assertEquals("壹万零壹佰零壹", numStr);
    }

    @Test
    @Ignore("Performance test skip it.")
    public void performance() {
        int times = 1000_0000;
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < times; i++) {
            MathUtils.numberTranslation(i);
        }
        System.out.println(stopwatch.stop());
        assertTrue(true);
    }
}