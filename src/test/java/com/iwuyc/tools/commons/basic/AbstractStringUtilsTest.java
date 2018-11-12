package com.iwuyc.tools.commons.basic;

import org.junit.Test;

import static org.junit.Assert.*;

public class AbstractStringUtilsTest {

    @Test
    public void isEmpty() {
        assertTrue(AbstractStringUtils.isEmpty(null));
        assertTrue(AbstractStringUtils.isEmpty(""));
        assertFalse(AbstractStringUtils.isEmpty("123"));
        assertTrue(AbstractStringUtils.isNotEmpty("123"));
    }

    @Test
    public void turnFirstCharToLowerCase() {
        String source = null;
        assertNull(AbstractStringUtils.turnFirstCharToLowerCase(source));
        source = "";
        assertTrue("".equals(AbstractStringUtils.turnFirstCharToLowerCase(source)));
        source = "";
        assertTrue("".equals(AbstractStringUtils.turnFirstCharToLowerCase(source)));
        source = "1Abc";
        assertTrue("1Abc".equals(AbstractStringUtils.turnFirstCharToLowerCase(source)));
        source = "Abc";
        assertTrue("abc".equals(AbstractStringUtils.turnFirstCharToLowerCase(source)));
        source = "AAbc";
        assertTrue("aAbc".equals(AbstractStringUtils.turnFirstCharToLowerCase(source)));

    }
}