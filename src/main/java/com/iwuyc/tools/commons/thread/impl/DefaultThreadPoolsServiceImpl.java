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

    public DefaultThreadPoolsServiceImpl(ThreadConfig config) {
        this.config = config;
    }

    @Override
    public ExecutorService getExecutorService(Class<?> clazz) {

        String domain = null;
        if (null == clazz) {
            domain = "root";
        } else {
            domain = clazz.getName();
        }
        LOG.debug("Get executor service for :{}.domain is:{}", clazz, domain);
        ExecutorService executorSer = executorServiceCache.get(domain);

        if (null == executorSer) {
            executorSer = findThreadPoolOrCreate(domain);
        }
        return executorSer;
    }

    private ExecutorService findThreadPoolOrCreate(String domain) {

        UsingConfig usingConfig = this.config.findUsingSetting(domain);
        ExecutorService executorService = this.executorServiceCache.get(usingConfig.getDomain());
        if (null != executorService) {
            return executorService;
        }

        Lock writeLock = this.lock.writeLock();
        try {
            writeLock.lock();

            if (this.executorServiceCache.containsKey(usingConfig.getDomain())) {
                executorService = this.executorServiceCache.get(usingConfig.getDomain());
                return executorService;
            }

            ThreadPoolConfig threadPoolConfig = this.config.findThreadPoolConfig(usingConfig.getDomain());
            executorService = createNewThreadPoolFactory(threadPoolConfig);

            this.executorServiceCache.put(usingConfig.getDomain(), executorService);
            this.executorServiceCache.put(domain, executorService);

            return executorService;
        } finally {
            writeLock.unlock();
        }
    }

    private ExecutorService createNewThreadPoolFactory(ThreadPoolConfig threadPoolConfig) {
        ExecutorServiceFactory factory = AbstractClassUtils.instance(ExecutorServiceFactory.class, threadPoolConfig
                .getFactory());
        return factory.create(threadPoolConfig);
    }

    @Override
    public ThreadConfig getConfig() {
        return this.config;
    }

}
