package com.iwuyc.tools.commons.thread;

import java.io.File;
import java.io.InputStream;

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

}
