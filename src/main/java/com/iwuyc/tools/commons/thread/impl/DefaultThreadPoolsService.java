package com.iwuyc.tools.commons.thread.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwuyc.tools.commons.classtools.ClassUtils;
import com.iwuyc.tools.commons.thread.Config;
import com.iwuyc.tools.commons.thread.ExecutorServiceFactory;
import com.iwuyc.tools.commons.thread.ThreadPoolsService;
import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;
import com.iwuyc.tools.commons.thread.conf.UsingConfig;

public class DefaultThreadPoolsService implements ThreadPoolsService
{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultThreadPoolsService.class);

    public Map<String, ExecutorService> executorServiceCache = new ConcurrentHashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private Config config;

    public DefaultThreadPoolsService(Config config)
    {
        this.config = config;
    }

    @Override
    public ExecutorService getExecutorService(Class<?> clazz)
    {

        String domain = null;
        if (null == clazz)
        {
            domain = "root";
        }
        else
        {
            domain = clazz.getName();
        }
        LOG.debug("Get executor service for :{}.domain is:{}", clazz, domain);
        ExecutorService executorSer = executorServiceCache.get(domain);
        if (null == executorSer)
        {
            Lock writeLock = lock.writeLock();
            try
            {
                writeLock.lock();
                executorSer = findThreadPoolOrCreate(domain);
            }
            finally
            {
                writeLock.unlock();
            }
        }
        return executorSer;
    }

    private ExecutorService findThreadPoolOrCreate(String domain)
    {
        UsingConfig usingConfig = config.findUsingSetting(domain);
        ExecutorService executorService = executorServiceCache.get(usingConfig.getDomain());
        if (null != executorService)
        {
            this.executorServiceCache.put(domain, executorService);
            return executorService;
        }

        ThreadPoolConfig threadPoolConfig = config.findThreadPoolConfig(usingConfig.getDomain());
        executorService = createNewThreadPoolFactory(threadPoolConfig);

        this.executorServiceCache.put(usingConfig.getDomain(), executorService);
        this.executorServiceCache.put(domain, executorService);

        return executorService;
    }

    private ExecutorService createNewThreadPoolFactory(ThreadPoolConfig threadPoolConfig)
    {
        ExecutorServiceFactory factory = ClassUtils.instance(ExecutorServiceFactory.class,
                threadPoolConfig.getFactory());
        return factory.create(threadPoolConfig);
    }

    @Override
    public Config getConfig()
    {
        return this.config;
    }

}
