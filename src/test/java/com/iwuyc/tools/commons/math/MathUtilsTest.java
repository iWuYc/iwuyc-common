package com.iwuyc.tools.commons.math;

import com.google.common.base.Stopwatch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MathUtilsTest {

    @Before
    public void setUp() {
        Class<MathUtils> clazz = MathUtils.class;
        System.out.println(clazz);
    }

    @Test
    public void translatorTest() {
        long num = -1111123456789L;
        String numStr = MathUtils.numberTranslation(num);
        final String target = "负壹万壹仟壹佰壹拾壹亿贰仟叁佰肆拾伍万陆仟柒佰捌拾玖";
        Assert.assertEquals(target, numStr);
    }

    @Test
    public void translatorTest1() {
        long num = 0;
        String numStr = MathUtils.numberTranslation(num);
        Assert.assertEquals(numStr, "零");

        num = 100;
        numStr = MathUtils.numberTranslation(num);
        Assert.assertEquals(numStr, "壹佰");

        num = 1000;
        numStr = MathUtils.numberTranslation(num);
        Assert.assertEquals(numStr, "壹仟");

        num = 10000;
        numStr = MathUtils.numberTranslation(num);
        Assert.assertEquals(numStr, "壹万");

        num = 101;
        numStr = MathUtils.numberTranslation(num);
        Assert.assertEquals(numStr, "壹佰零壹");

        num = 1001;
        numStr = MathUtils.numberTranslation(num);
        Assert.assertEquals(numStr, "壹仟零壹");

        num = 1010;
        numStr = MathUtils.numberTranslation(num);
        Assert.assertEquals(numStr, "壹仟零壹拾");

        num = 10100;
        numStr = MathUtils.numberTranslation(num);
        Assert.assertEquals(numStr, "壹万零壹佰");

        num = 10101;
        numStr = MathUtils.numberTranslation(num);
        Assert.assertEquals(numStr, "壹万零壹佰零壹");
    }

    @Test(timeout = 2_000)
    public void performance() {
        int times = 1000_0000;
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < times; i++) {
            MathUtils.numberTranslation(i);
        }
        System.out.println(stopwatch.stop());
    }
}