package com.iwuyc.tools.commons.util.time;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 功能说明
 *
 * @author 吴宇春
 * @version 1.0.0
 * @date 2022/1/29
 */
public class SmartDateTimeFormatterTest {
    @Test(expected = NullPointerException.class)
    public void name() {
        SmartDateTimeFormatter.create(null);
    }
}