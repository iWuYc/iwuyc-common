package com.iwuyc.tools.commons.thread.impl.bean;

import com.iwuyc.tools.commons.thread.ExecutorServiceFactory;
import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;
import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantLock;

public class JDKExecutorServiceTuple implements ExecutorServiceTuple {
    private final ExecutorServiceFactory factory;
    @Getter
    private final ThreadPoolConfig config;
    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;
    private ReentrantLock lock = new ReentrantLock(true);

    public JDKExecutorServiceTuple(ExecutorServiceFactory factory, ThreadPoolConfig config) {
        this.factory = factory;
        this.config = config;
    }

    @Override
    public ExecutorService getExecutorService() {
        if (null != executorService) {
            return executorService;
        }
        try {
            lock.lock();
            if (null == executorService) {
                executorService = factory.create(config);
            }
        } finally {
            lock.unlock();
        }
        return executorService;
    }

    @Override
    public ScheduledExecutorService getScheduledExecutorService() {
        if (null != scheduledExecutorService) {
            return scheduledExecutorService;
        }
        try {
            lock.lock();
            if (null == scheduledExecutorService) {
                scheduledExecutorService = factory.createSchedule(config);
            }
        } finally {
            lock.unlock();
        }
        return scheduledExecutorService;
    }
}
