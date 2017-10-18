package com.iwuyc.tools.commons.thread.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.iwuyc.tools.commons.thread.ExecutorServiceFactory;
import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public class ScheduledExecutorServiceFactory implements ExecutorServiceFactory {

    @Override
    public ExecutorService create(ThreadPoolConfig config) {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(config.getCorePoolSize(),
                new DefaultThreadFactory(config.getThreadPoolsName()));
        return scheduledExecutorService;
    }

}
