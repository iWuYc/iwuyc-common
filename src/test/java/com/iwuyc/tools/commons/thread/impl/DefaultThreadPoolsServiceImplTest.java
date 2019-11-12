package com.iwuyc.tools.commons.thread.impl;

import com.google.common.base.Stopwatch;
import com.iwuyc.tools.commons.thread.ThreadConfig;
import com.iwuyc.tools.commons.thread.ThreadPoolsService;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.*;

public class DefaultThreadPoolsServiceImplTest {
    private ThreadPoolsService poolSer;

    @Before
    public void before() throws Exception {
            this.poolSer =  ThreadConfig.config("classpath:/test.properties");
    }

    @Test
    public void test() {

        ExecutorService result = this.poolSer.getExecutorService(DefaultThreadPoolsServiceImpl.class);
        System.out.println(result);
        result = this.poolSer.getExecutorService(DefaultThreadPoolsServiceImpl.class);
        System.out.println(result);

        ExecutorService result1 = this.poolSer.getExecutorService(DefaultThreadPoolsServiceImpl.class.getName() + ".1");
        assertNotEquals(result, result1);
        ExecutorService result2 = this.poolSer.getExecutorService(DefaultThreadPoolsServiceImpl.class.getName() + ".2");
        assertNotEquals(result1, result2);


    }

    @Test
    public void testSchedule() {
        ScheduledExecutorService scheduled1 = this.poolSer.getScheduledExecutor(DefaultThreadPoolsServiceImpl.class);
        assertNotNull(scheduled1);
        ScheduledExecutorService scheduled2 = this.poolSer.getScheduledExecutor(DefaultThreadPoolsServiceImpl.class);
        assertNotNull(scheduled2);
        assertEquals(scheduled1, scheduled2);

        scheduled1 = this.poolSer.getScheduledExecutor(DefaultThreadPoolsServiceImpl.class.getName() + ".1");
        scheduled2 = this.poolSer.getScheduledExecutor(DefaultThreadPoolsServiceImpl.class.getName() + ".2");
        assertNotEquals(scheduled1, scheduled2);
    }

    @Test
    public void test1() {
        ExecutorService rootExecutor = poolSer.getExecutorService((Class<?>) null);
        System.out.println(rootExecutor);

        System.out.println(poolSer.getThreadConfig());
    }

    @Test(timeout = 5_000L)
    public void performance() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < 1000_0000; i++) {

            ExecutorService result1 = this.poolSer.getExecutorService(DefaultThreadPoolsServiceImpl.class.getName() + "." + i % 3);
        }
        System.out.println(stopwatch.stop());
    }
}