package com.iwuyc.tools.commons.thread;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.*;

public class ThreadConfigTest {

    private ThreadConfig config;

    @Before
    public void before() throws Exception {
        InputStream in = ThreadConfig.class.getResourceAsStream("/thread/thread.properties");
        this.config = new ThreadConfig();
        config.load(in);

    }

    @Test
    public void test() throws Exception {
        config.findUsingSetting(ThreadConfig.class.getName());
        // return root setting
        config.findUsingSetting("org");
    }

    @Test
    public void test1() throws Exception {
        InputStream in = ThreadConfig.class.getResourceAsStream("/thread/thread.properties");
        Properties properties = new Properties();
        properties.load(in);

    }

    @Test
    public void config() {
        URL configFileUrl = ThreadConfig.class.getResource("/thread/thread.properties");
        File file = new File(configFileUrl.getFile());
        ThreadPoolsService config = ThreadConfig.config(file);
        System.out.println(config);
    }

    @Test(expected = IOException.class)
    public void configException() {
        File file = new File("/thread/thread_not_exists.properties");
        ThreadPoolsService config = ThreadConfig.config(file);
        System.out.println(config);
    }
}
