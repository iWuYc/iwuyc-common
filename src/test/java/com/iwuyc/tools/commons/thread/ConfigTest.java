package com.iwuyc.tools.commons.thread;

import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class ConfigTest
{

    private ThreadConfig config;

    @Before
    public void before() throws Exception
    {
        InputStream in = ThreadConfig.class.getResourceAsStream("/thread/thread.properties");
        this.config = new ThreadConfig();
        config.load(in);

    }

    @Test
    public void test() throws Exception
    {
        config.findUsingSetting(ThreadConfig.class.getName());
    }

    @Test
    public void test1() throws Exception
    {
        InputStream in = ThreadConfig.class.getResourceAsStream("/thread/thread.properties");
        Properties properties = new Properties();
        properties.load(in);

    }
}
