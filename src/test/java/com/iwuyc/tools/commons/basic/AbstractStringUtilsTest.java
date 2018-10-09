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
}