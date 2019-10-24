package com.iwuyc.tools.commons.thread.impl;

import com.iwuyc.tools.commons.basic.type.TimeTuple;
import com.iwuyc.tools.commons.thread.ExecutorServiceFactory;
import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;

import java.util.concurrent.*;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public class DefaultExecutorServiceFactory implements ExecutorServiceFactory {
    private static final String SCHEDULE_EXECUTOR_NAME_FORMAT = "schedule-%s";

    @Override
    public ExecutorService create(ThreadPoolConfig config) {
        TimeTuple keepalive = config.getKeepAliveTime();

        BlockingQueue<Runnable> workQueue = builderBlockingQueue(config);
        ThreadFactory threadFactory = new DefaultThreadFactory(config.getThreadPoolsName());

        return new ThreadPoolExecutor(config.getCorePoolSize(), config.getMaximumPoolSize(), keepalive.getTime(), keepalive.getTimeUnit(), workQueue, threadFactory);
    }

    @Override
    public ScheduledExecutorService createSchedule(ThreadPoolConfig config) {
        String threadPoolName = String.format(SCHEDULE_EXECUTOR_NAME_FORMAT, config.getThreadPoolsName());
        final DefaultThreadFactory defaultThreadFactory = new DefaultThreadFactory(threadPoolName);
        return new ScheduledThreadPoolExecutor(config.getCorePoolSize(), defaultThreadFactory);
    }

    private BlockingQueue<Runnable> builderBlockingQueue(ThreadPoolConfig config) {
        int maxQueueSize = config.getMaxQueueSize();
        if (maxQueueSize < 1) {
            throw new IllegalArgumentException("Queue size can't be less than 1.");
        }
        return new ArrayBlockingQueue<>(maxQueueSize, true);
    }
}
