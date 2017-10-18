package com.iwuyc.tools.commons.thread.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import com.iwuyc.tools.commons.basic.type.TimeTuple;
import com.iwuyc.tools.commons.thread.ExecutorServiceFactory;
import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public class ThreadPoolExecutorFactory implements ExecutorServiceFactory {

    @Override
    public ExecutorService create(ThreadPoolConfig config) {
        TimeTuple keepalive = config.getKeepAliveTime();

        BlockingQueue<Runnable> workQueue = builderBlockingQueue(config);
        ThreadFactory handler = new DefaultThreadFactory(config.getThreadPoolsName());
        ExecutorService service = new java.util.concurrent.ThreadPoolExecutor(config.getCorePoolSize(), config
                .getMaximumPoolSize(), keepalive.getTime(), keepalive.getTimeUnit(), workQueue, handler);

        return service;
    }

    private BlockingQueue<Runnable> builderBlockingQueue(ThreadPoolConfig config) {
        int maxQueueSize = config.getMaxQueueSize();
        if (maxQueueSize < 1) {
            throw new IllegalArgumentException("Queue size can't be less than 1.");
        }
        return new ArrayBlockingQueue<Runnable>(maxQueueSize, true);
    }
}
