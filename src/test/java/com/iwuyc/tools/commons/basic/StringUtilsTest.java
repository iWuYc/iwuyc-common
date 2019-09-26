package com.iwuyc.tools.commons.basic;

import com.iwuyc.tools.commons.util.string.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilsTest {

    @Test
    public void isEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty("123"));
        assertTrue(StringUtils.isNotEmpty("123"));
    }

    @Test
    public void turnFirstCharToLowerCase() {
        String source = null;
        assertNull(StringUtils.turnFirstCharToLowerCase(source));
        source = "";
        assertTrue("".equals(StringUtils.turnFirstCharToLowerCase(source)));
        source = "";
        assertTrue("".equals(StringUtils.turnFirstCharToLowerCase(source)));
        source = "1Abc";
        assertTrue("1Abc".equals(StringUtils.turnFirstCharToLowerCase(source)));
        source = "Abc";
        assertTrue("abc".equals(StringUtils.turnFirstCharToLowerCase(source)));
        source = "AAbc";
        assertTrue("aAbc".equals(StringUtils.turnFirstCharToLowerCase(source)));

    }
}