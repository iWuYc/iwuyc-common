package com.iwuyc.tools.commons.util.string;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 功能说明
 *
 * @author 吴宇春
 * @version 1.0.0
 * @date 2022/1/30
 */
public class RegexUtilsTest {

    @Test
    public void getPattern() {
    }

    @Test
    public void testGetPattern() {
    }

    @Test
    public void match() {
        assertTrue(RegexUtils.match("a", "a"));
        assertFalse(RegexUtils.match("a", "ab"));
        assertFalse(RegexUtils.match("", "ab"));
        assertFalse(RegexUtils.match("a", null));
    }
}