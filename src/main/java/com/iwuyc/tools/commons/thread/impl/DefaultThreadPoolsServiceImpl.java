package com.iwuyc.tools.commons.thread.impl;

import com.iwuyc.tools.commons.basic.StringUtils;
import com.iwuyc.tools.commons.classtools.ClassUtils;
import com.iwuyc.tools.commons.thread.ExecutorServiceFactory;
import com.iwuyc.tools.commons.thread.ThreadConfig;
import com.iwuyc.tools.commons.thread.ThreadPoolsService;
import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;
import com.iwuyc.tools.commons.thread.conf.UsingConfig;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class DefaultThreadPoolsServiceImpl implements ThreadPoolsService {

    private static final String DEFAULT_DOMAIN = "root";

    private Map<String, ExecutorService> executorServiceCache = new ConcurrentHashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private ThreadConfig config;
    private AtomicBoolean isShutdown = new AtomicBoolean();

    public DefaultThreadPoolsServiceImpl(ThreadConfig config) {
        this.config = config;
    }

    @Override
    public ExecutorService getExecutorService(Class<?> clazz) {
        String domain;
        if (null == clazz) {
            log.debug("未指定类，将使用默认的domain：{}", DEFAULT_DOMAIN);
            domain = DEFAULT_DOMAIN;
        } else {
            domain = clazz.getName();
        }
        return getExecutorService(domain);
    }

    @Override
    public ExecutorService getExecutorService(String domain) {
        return getExecutorServiceByMap(domain, this.executorServiceCache);
    }

    private <T extends ExecutorService> T getExecutorServiceByMap(String domain, Map<String, T> container) {
        if (StringUtils.isEmpty(domain)) {
            log.debug("未指定domain，将使用默认的domain：{}", DEFAULT_DOMAIN);
            domain = DEFAULT_DOMAIN;
        }

        log.debug("Get executor service for domain:{}", domain);
        T executorSer = container.get(domain);

        if (null == executorSer) {
            log.debug("未能直接命中获取到指定的executorService，将尝试查找父域，或者创建一个对应的实例。domain为：{}", domain);
            executorSer = findThreadPoolOrCreate(domain, container);
        }
        return executorSer;
    }

    private <T extends ExecutorService> T findThreadPoolOrCreate(String domain, Map<String, T> container) {

        UsingConfig usingConfig = this.config.findUsingSetting(domain);
        T executorService = container.get(usingConfig.getDomain());
        if (null != executorService) {
            log.debug("找到[{}]对应的executorService。", usingConfig.getDomain());
            return executorService;
        }

        Lock writeLock = this.lock.writeLock();
        try {
            writeLock.lock();

            if (container.containsKey(usingConfig.getDomain())) {
                executorService = container.get(usingConfig.getDomain());
                log.debug("找到[{}]对应的executorService。", usingConfig.getDomain());
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
        ExecutorServiceFactory factory = ClassUtils
                .instance(ExecutorServiceFactory.class, threadPoolConfig.getFactory());
        if (null == factory) {
            log.error("无法实例化指定的工厂类[{}]。", threadPoolConfig.getFactory());
            throw new IllegalArgumentException("无法实例化工厂类[" + threadPoolConfig.getFactory() + "]");
        }
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
                log.error("Shutdown pool raise an error.Cause:", e);
            }
        }
        executorServiceCache.clear();
    }

    @Override
    public boolean isShutdown() {
        return isShutdown.get();
    }

}
