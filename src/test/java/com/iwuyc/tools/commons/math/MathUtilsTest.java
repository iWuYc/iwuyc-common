package com.iwuyc.tools.commons.math;

import org.junit.Assert;
import org.junit.Test;

public class MathUtilsTest {

    @Test
    public void translatorTest() {
        long num = -1111123456789L;
        String numStr = MathUtils.numberTranslation(num);
        final String target = "负壹万亿壹仟壹佰壹拾壹亿贰仟叁佰肆拾伍万陆仟柒佰捌拾玖";
        Assert.assertTrue(target.equals(numStr));
    }
}