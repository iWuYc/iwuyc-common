package com.iwuyc.tools.commons.util;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileUtilTest {
    private String path = "/Users/iwuyc/Downloads/sbt copy.tar";

    @Test
    public void test() {
        assertFalse(FileUtil.safeDelete(path));

        URL testPropertiesUrl = FileUtilTest.class.getResource("/");
        String filePath = testPropertiesUrl.getFile()+"test.properties";
        File file = new File(filePath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(FileUtil.safeDelete(filePath));

    }

}
