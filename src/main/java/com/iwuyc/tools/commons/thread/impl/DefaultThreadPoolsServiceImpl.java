package com.iwuyc.tools.commons.thread.impl;

import com.iwuyc.tools.commons.classtools.AbstractClassUtils;
import com.iwuyc.tools.commons.thread.ExecutorServiceFactory;
import com.iwuyc.tools.commons.thread.ThreadConfig;
import com.iwuyc.tools.commons.thread.ThreadPoolsService;
import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;
import com.iwuyc.tools.commons.thread.conf.UsingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public class DefaultThreadPoolsServiceImpl implements ThreadPoolsService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultThreadPoolsServiceImpl.class);

    public Map<String, ExecutorService> executorServiceCache = new ConcurrentHashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private ThreadConfig config;
    private AtomicBoolean isShutdown = new AtomicBoolean();

    public DefaultThreadPoolsServiceImpl(ThreadConfig config) {
        this.config = config;
    }

    @Override
    public ExecutorService getExecutorService(Class<?> clazz) {
        return getExecutorServiceByMap(clazz, this.executorServiceCache);
    }

    private <T extends ExecutorService> T getExecutorServiceByMap(Class<?> clazz, Map<String, T> container) {

        String domain = null;
        if (null == clazz) {
            domain = "root";
        } else {
            domain = clazz.getName();
        }
        LOG.debug("Get executor service for :{}.domain is:{}", clazz, domain);
        T executorSer = container.get(domain);

        if (null == executorSer) {
            executorSer = findThreadPoolOrCreate(domain, container);
        }
        return executorSer;
    }

    private <T extends ExecutorService> T findThreadPoolOrCreate(String domain, Map<String, T> container) {

        UsingConfig usingConfig = this.config.findUsingSetting(domain);
        T executorService = container.get(usingConfig.getDomain());
        if (null != executorService) {
            return executorService;
        }

        Lock writeLock = this.lock.writeLock();
        try {
            writeLock.lock();

            if (container.containsKey(usingConfig.getDomain())) {
                executorService = container.get(usingConfig.getDomain());
                return executorService;
            }

            ThreadPoolConfig threadPoolConfig = this.config.findThreadPoolConfig(usingConfig.getThreadPoolsName());
            executorService = createNewThreadPoolFactory(threadPoolConfig);

            container.put(usingConfig.getDomain(), executorService);
            container.put(domain, executorService);

            return executorService;
        } finally {
            writeLock.unlock();
        }
    }

    private <T extends ExecutorService> T createNewThreadPoolFactory(ThreadPoolConfig threadPoolConfig) {
        ExecutorServiceFactory factory = AbstractClassUtils
                .instance(ExecutorServiceFactory.class, threadPoolConfig.getFactory());
        return (T) factory.create(threadPoolConfig);
    }

    @Override
    public ThreadConfig getConfig() {
        return this.config;
    }

    @Override
    public void shutdown() {

        if (!isShutdown.compareAndSet(false, true)) {
            return;
        }

        for (Map.Entry<String, ExecutorService> item : executorServiceCache.entrySet()) {
            try {
                item.getValue().shutdown();
            } catch (Exception e) {
                LOG.error("Shutdown pool raise an error.Cause:", e);
            }
        }
        executorServiceCache.clear();
    }

    @Override
    public boolean isShutdown() {
        return isShutdown.get();
    }

}
