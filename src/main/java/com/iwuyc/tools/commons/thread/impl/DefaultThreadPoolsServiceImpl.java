package com.iwuyc.tools.commons.thread.impl;

import com.iwuyc.tools.commons.classtools.ClassUtils;
import com.iwuyc.tools.commons.thread.ExecutorServiceFactory;
import com.iwuyc.tools.commons.thread.ThreadConfig;
import com.iwuyc.tools.commons.thread.ThreadPoolsService;
import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;
import com.iwuyc.tools.commons.thread.conf.UsingConfig;
import com.iwuyc.tools.commons.util.string.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
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
    private Map<String, ScheduledExecutorService> scheduleExecutorServiceCache = new ConcurrentHashMap<>();
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
        return getExecutorServiceByMap(domain, this.executorServiceCache, ExecutorService.class);
    }

    @Override
    public ScheduledExecutorService getScheduledExecutor(String domain) {
        return getExecutorServiceByMap(domain, this.scheduleExecutorServiceCache, ScheduledExecutorService.class);
    }

    @Override
    public ScheduledExecutorService getScheduledExecutor(Class<?> clazz) {
        String domain;
        if (null == clazz) {
            domain = DEFAULT_DOMAIN;
        } else {
            domain = clazz.getName();
        }
        return getScheduledExecutor(domain);
    }

    private <T extends ExecutorService> T getExecutorServiceByMap(String domain, Map<String, T> container, Class<T> targetType) {
        if (StringUtils.isEmpty(domain)) {
            log.debug("未指定domain，将使用默认的domain：{}", DEFAULT_DOMAIN);
            domain = DEFAULT_DOMAIN;
        }

        log.debug("Get executor service for domain:{}", domain);
        T executorSer = container.get(domain);

        if (null == executorSer) {
            log.debug("未能直接命中获取到指定的executorService，将尝试查找父域，或者创建一个对应的实例。domain为：{}", domain);
            executorSer = findThreadPoolOrCreate(domain, container, targetType);
        }
        return executorSer;
    }

    private <T extends ExecutorService> T findThreadPoolOrCreate(String domain, Map<String, T> container, Class<T> targetType) {

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
            executorService = createNewThreadPoolFactory(threadPoolConfig, isScheduleExecutor(targetType));

            container.put(usingConfig.getDomain(), executorService);
            container.put(domain, executorService);

            return executorService;
        } finally {
            writeLock.unlock();
        }
    }

    private <T extends ExecutorService> boolean isScheduleExecutor(Class<T> targetType) {
        return ScheduledExecutorService.class.isAssignableFrom(targetType);
    }

    @SuppressWarnings("unchecked")
    private <T extends ExecutorService> T createNewThreadPoolFactory(ThreadPoolConfig threadPoolConfig, boolean isScheduleExecutor) {
        ExecutorServiceFactory factory = ClassUtils
                .instance(ExecutorServiceFactory.class, threadPoolConfig.getFactory());
        if (null == factory) {
            log.error("无法实例化指定的工厂类[{}]。", threadPoolConfig.getFactory());
            throw new IllegalArgumentException("无法实例化工厂类[" + threadPoolConfig.getFactory() + "]");
        }
        T instance;
        if (isScheduleExecutor) {
            instance = (T) factory.createSchedule(threadPoolConfig);
        } else {
            instance = (T) factory.create(threadPoolConfig);
        }
        return instance;
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
