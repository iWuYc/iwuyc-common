package com.iwuyc.tools.commons.math;

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
        final String target = "负壹万亿壹仟壹佰壹拾壹亿贰仟叁佰肆拾伍万陆仟柒佰捌拾玖";
        Assert.assertEquals(target,numStr);
    }
}