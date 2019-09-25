package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.util.file.FileUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileUtilTest {

    private String path = "/Users/iwuyc/Downloads/sbt copy.tar";

    @Test
    public void test() {
        assertFalse(FileUtil.safeDelete(path));

        URL testPropertiesUrl = FileUtilTest.class.getResource("/");
        String filePath = testPropertiesUrl.getFile() + "test_delete.properties";
        File file = new File(filePath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(FileUtil.safeDelete(filePath));

    }

    @Test
    public void absoluteLocation() {
        Optional<String> result = FileUtil.absoluteLocation("classpath:/test.properties");
        System.out.println(result);
    }
}
