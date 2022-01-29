package com.iwuyc.tools.commons.util.string;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {

    @Test
    public void isEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty("123"));
        assertTrue(StringUtils.isNotEmpty("123"));

        assertTrue(StringUtils.isNotEmpty("123"));
        assertFalse(StringUtils.isNotEmpty(""));
        assertFalse(StringUtils.isNotEmpty(null));
    }

    @Test
    public void turnFirstCharToLowerCase() {
        String source = null;
        assertNull(StringUtils.turnFirstCharToLowerCase(source));
        source = "";
        assertEquals("", StringUtils.turnFirstCharToLowerCase(source));
        source = "";
        assertEquals("", StringUtils.turnFirstCharToLowerCase(source));
        source = "1Abc";
        assertEquals("1Abc", StringUtils.turnFirstCharToLowerCase(source));
        source = "Abc";
        assertEquals("abc", StringUtils.turnFirstCharToLowerCase(source));
        source = "AAbc";
        assertEquals("aAbc", StringUtils.turnFirstCharToLowerCase(source));

    }

    @Test
    public void equals() {
        assertTrue(StringUtils.equals("a", "a"));
        assertTrue(StringUtils.equals(null, null));
        assertTrue(StringUtils.equals("", ""));
        assertFalse(StringUtils.equals("a", ""));
        assertFalse(StringUtils.equals("", null));
        assertFalse(StringUtils.equals(null, ""));
        assertFalse(StringUtils.equals("a", "ab"));


        assertFalse(StringUtils.notEquals("a", "a"));
        assertFalse(StringUtils.notEquals(null, null));
        assertFalse(StringUtils.notEquals("", ""));
        assertTrue(StringUtils.notEquals("a", ""));
        assertTrue(StringUtils.notEquals("", null));
        assertTrue(StringUtils.notEquals(null, ""));
        assertTrue(StringUtils.notEquals("a", "ab"));

        assertFalse(StringUtils.isNotBlank(""));
        assertFalse(StringUtils.isNotBlank(" "));
        assertFalse(StringUtils.isNotBlank(null));
        assertTrue(StringUtils.isNotBlank(" a"));
        assertTrue(StringUtils.isNotBlank(" a "));
        assertTrue(StringUtils.isNotBlank("a "));
    }

    @Test
    public void length() {
        assertEquals(0, StringUtils.length(null));
        assertEquals(0, StringUtils.length(""));
        assertEquals(1, StringUtils.length(" "));
        assertEquals(3, StringUtils.length("abc"));
        assertEquals(4, StringUtils.length(" abc"));
        assertEquals(5, StringUtils.length(" abc "));
    }

    @Test
    public void isAnyEmpty() {
        assertTrue(StringUtils.isAnyEmpty((String) null));
        assertTrue(StringUtils.isAnyEmpty("a", null));
        assertTrue(StringUtils.isAnyEmpty("a", null, "b"));
        assertTrue(StringUtils.isAnyEmpty(null, "b"));
        assertTrue(StringUtils.isAnyEmpty("", "b"));
        assertFalse(StringUtils.isAnyEmpty("a", "null", "b"));


        assertFalse(StringUtils.isNonEmpty((String) null));
        assertFalse(StringUtils.isNonEmpty("a", null));
        assertFalse(StringUtils.isNonEmpty("a", null, "b"));
        assertFalse(StringUtils.isNonEmpty(null, "b"));
        assertTrue(StringUtils.isNonEmpty("a", "null", "b"));
    }

    @Test
    public void trim() {
        assertEquals("", StringUtils.trim(""));
        assertEquals("", StringUtils.trim(" "));
        assertEquals("a", StringUtils.trim(" a"));
        assertEquals("a", StringUtils.trim(" a "));
        assertEquals("a", StringUtils.trim("a "));
    }
}