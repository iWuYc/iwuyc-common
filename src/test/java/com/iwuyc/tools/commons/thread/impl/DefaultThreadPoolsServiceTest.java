package com.iwuyc.tools.commons.thread.impl;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Test;

import com.iwuyc.tools.commons.thread.Config;

public class DefaultThreadPoolsServiceTest
{
    private Config config;
    private DefaultThreadPoolsService poolSer;

    @Before
    public void before() throws Exception
    {
        InputStream in = Config.class.getResourceAsStream("/thread/thread.properties");
        this.config = new Config();
        config.load(in);
        this.poolSer = new DefaultThreadPoolsService(config);
    }

    @Test
    public void test()
    {

        ExecutorService result = this.poolSer.getExecutorService(DefaultThreadPoolsService.class);
        System.out.println(result);
        result = this.poolSer.getExecutorService(DefaultThreadPoolsService.class);
        System.out.println(result);
    }

}
