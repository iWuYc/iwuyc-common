package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.util.file.FileUtil;
import org.junit.Assert;
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
        String filePath = testPropertiesUrl.getFile() + "test_delete.properties";
        File file = new File(filePath);
        try {
            boolean createFileResult = file.createNewFile();
            assertTrue(createFileResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(FileUtil.safeDelete(filePath));

    }

    @Test
    public void absoluteLocation() {
        String result = FileUtil.absoluteLocation("classpath:/test.properties");
        Assert.assertTrue(new File(result).exists());
        System.out.println(result);
        result = FileUtil.absoluteLocation("classpath:/tesasdasdat.properties");
        System.out.println(result);
    }

    @Test
    public void fileExistsTest() {
        final String location = FileUtil.class.getResource(FileUtil.class.getSimpleName() + ".class").getFile();
        boolean result = FileUtil.fileExists(location);
        assertTrue(result);
    }
}
