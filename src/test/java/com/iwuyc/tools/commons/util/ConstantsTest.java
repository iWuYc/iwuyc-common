package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.util.string.StringUtils;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 功能说明
 *
 * @author 吴宇春
 * @version 1.0.0
 * @date 2022/1/27
 */
public class ConstantsTest {
    @Test
    public void name() {
        assertEquals(Constants.UTF8_STR, "UTF-8");
        assertEquals(Constants.UTF8, StandardCharsets.UTF_8);
        assertTrue(StringUtils.isEmpty(Constants.NIL_STRING));
    }
}