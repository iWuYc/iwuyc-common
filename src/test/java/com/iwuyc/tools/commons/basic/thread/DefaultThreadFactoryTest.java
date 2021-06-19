package com.iwuyc.tools.commons.basic.thread;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultThreadFactoryTest {

    private final String threadPoolsName = "neil-test";
    private DefaultThreadFactory threadFactory;
    private final ThreadFactoryConf threadFactoryConf = ThreadFactoryConf.builder().daemon(false).threadPoolsName(threadPoolsName).forSchedulePools(false).build();

    @BeforeEach
    void setUp() {
        threadFactory = (DefaultThreadFactory) threadFactoryConf.buildFactory();
    }

    @Test
    void construct() {
        final ThreadFactoryConf conf = ThreadFactoryConf.builder().daemon(false).forSchedulePools(false).build();
        final ThreadFactory threadFactory = conf.buildFactory();
        threadFactory.newThread(null);
        threadFactory.newThread(null);
    }

    @Test
    @Timeout(10_000)
    void newThread() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        threadFactory.newThread(latch::countDown).start();
        latch.await();
        threadFactory.newThread(null);
    }

    @Test
    void getThreadFactoryConf() {
        assertNotNull(threadFactory.getThreadFactoryConf());
    }

    @Test
    void getThreadPreName() {
        assertEquals(threadFactory.getThreadPreName(), threadPoolsName + "-%s");
    }
}