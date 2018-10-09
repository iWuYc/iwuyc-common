package com.iwuyc.tools.commons.util;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileUtilTest {

    @Test
    public void test() {
        assertFalse(FileUtil.safeDelete("/Users/iwuyc/Downloads/sbt copy.tar"));

        URL testPropertiesUrl = FileUtilTest.class.getResource("/test.properties");
        assertTrue(FileUtil.safeDelete(testPropertiesUrl.getFile()));

    }

}
