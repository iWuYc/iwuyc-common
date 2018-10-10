package com.iwuyc.tools.commons.thread.impl;

import com.iwuyc.tools.commons.thread.ThreadConfig;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.*;

public class DefaultThreadPoolsServiceImplTest {
    private ThreadConfig config;
    private DefaultThreadPoolsServiceImpl poolSer;

    @Before
    public void before() throws Exception {
        try (InputStream in = ThreadConfig.class.getResourceAsStream("/thread/thread.properties");) {
            this.config = new ThreadConfig();
            config.load(in);
            this.poolSer = new DefaultThreadPoolsServiceImpl(config);
        }
    }

    @Test
    public void test() {

        ExecutorService result = this.poolSer.getExecutorService(DefaultThreadPoolsServiceImpl.class);
        System.out.println(result);
        result = this.poolSer.getExecutorService(DefaultThreadPoolsServiceImpl.class);
        System.out.println(result);
    }

    @Test
    public void test1() {
        ExecutorService rootExecutor = poolSer.getExecutorService(null);
        System.out.println(rootExecutor);

        System.out.println(poolSer.getConfig());
    }

}