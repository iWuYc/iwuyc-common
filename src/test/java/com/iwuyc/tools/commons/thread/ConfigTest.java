package com.iwuyc.tools.commons.thread;

import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class ConfigTest
{

    private Config config;

    @Before
    public void before() throws Exception
    {
        InputStream in = Config.class.getResourceAsStream("/thread/thread.properties");
        this.config = new Config();
        config.load(in);

    }

    @Test
    public void test() throws Exception
    {
        config.findUsingSetting(Config.class.getName());
    }

    @Test
    public void test1() throws Exception
    {
        InputStream in = Config.class.getResourceAsStream("/thread/thread.properties");
        Properties properties = new Properties();
        properties.load(in);

    }
}
