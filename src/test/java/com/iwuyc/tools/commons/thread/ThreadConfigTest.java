package com.iwuyc.tools.commons.thread;

import com.iwuyc.tools.commons.thread.conf.UsingConfig;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class ThreadConfigTest {

    private ThreadConfig config;

    private String pathName = "/thread/thread_not_exists.properties";

    @Before
    public void before() throws Exception {
        try (InputStream in = ThreadConfig.class.getResourceAsStream("/thread/thread.properties")) {
            this.config = new ThreadConfig();
            config.load(in);
        }
    }

    @Test
    public void test() {
        UsingConfig result = config.findUsingSetting(ThreadConfig.class.getName());
        System.out.println(result);
        // return root setting
        result = config.findUsingSetting("org");
        System.out.println(result);
    }

    @Test
    public void config() {
        URL configFileUrl = ThreadConfig.class.getResource("/thread/thread.properties");
        File file = new File(configFileUrl.getFile());
        ThreadPoolsService config = ThreadConfig.config(file);
        System.out.println(config);
    }

    @Test
    public void test1() throws Exception {
        try (InputStream in = ThreadConfig.class.getResourceAsStream("/thread/thread.properties")) {
            Properties properties = new Properties();
            properties.load(in);
        }
    }

    @Test
    public void configException() {
        File file = new File(pathName);
        ThreadPoolsService config = ThreadConfig.config(file);
        System.out.println(config);
    }

    @Test
    public void coreSize() {
        System.out.println(Runtime.getRuntime().availableProcessors());
    }
}
