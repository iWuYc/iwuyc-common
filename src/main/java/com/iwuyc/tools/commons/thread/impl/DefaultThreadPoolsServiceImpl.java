package com.iwuyc.tools.commons.thread.impl;

import com.iwuyc.tools.commons.classtools.ClassUtils;
import com.iwuyc.tools.commons.thread.*;
import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;
import com.iwuyc.tools.commons.thread.conf.UsingConfig;
import com.iwuyc.tools.commons.util.string.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
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
@SuppressWarnings("rawtypes")
@Slf4j
public class DefaultThreadPoolsServiceImpl implements ThreadPoolsService, ModifiableService<ThreadPoolConfig, Boolean> {

    private static final String DEFAULT_DOMAIN = "root";

    private final Map<String, RefreshableExecutorService> executorServiceCache = new ConcurrentHashMap<>();
    private final Map<String, RefreshableScheduledExecutorService> scheduleExecutorServiceCache = new ConcurrentHashMap<>();
    private final Map<String, ExecutorServiceFactory> executorServiceFactoryCache = new ConcurrentHashMap<>();
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
        return getExecutorServiceByMap(domain, this.executorServiceCache, RefreshableExecutorService.class);
    }

    @Override
    public ScheduledExecutorService getScheduledExecutor(String domain) {
        return getExecutorServiceByMap(domain, this.scheduleExecutorServiceCache, RefreshableScheduledExecutorService.class);
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

    private <T extends RefreshableExecutorService> T getExecutorServiceByMap(String domain, Map<String, T> container, Class<T> targetType) {
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

    private <T extends RefreshableExecutorService<?, ?>> T findThreadPoolOrCreate(String domain, Map<String, T> container, Class<T> targetType) {

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
    private <T extends RefreshableExecutorService<?, ?>> T createNewThreadPoolFactory(ThreadPoolConfig threadPoolConfig, boolean isScheduleExecutor) {
        final String executorServiceFactoryName = threadPoolConfig.getFactory();
        ExecutorServiceFactory executorServiceFactory = executorServiceFactoryCache.get(executorServiceFactoryName);
        if (null == executorServiceFactory) {
            executorServiceFactory = createExecutorServiceFactory(executorServiceFactoryName);
            executorServiceFactoryCache.put(executorServiceFactoryName, executorServiceFactory);
        }

        T instance;
        if (isScheduleExecutor) {
            ScheduledExecutorService instanceTmp = executorServiceFactory.createSchedule(threadPoolConfig);
            instance = (T) new WrappingScheduledExecutorService(instanceTmp, threadPoolConfig);
        } else {
            ExecutorService instanceTmp = executorServiceFactory.create(threadPoolConfig);
            instance = (T) new WrappingExecutorService<>(instanceTmp, threadPoolConfig);
        }
        return instance;
    }

    private ExecutorServiceFactory createExecutorServiceFactory(String executorServiceFactoryName) {
        try {
            this.lock.writeLock().lock();
            ExecutorServiceFactory factory = this.executorServiceFactoryCache.get(executorServiceFactoryName);
            if (null == factory) {
                factory = ClassUtils.instance(ExecutorServiceFactory.class, executorServiceFactoryName);
                if (null == factory) {
                    log.error("无法实例化指定的工厂类[{}]。", executorServiceFactoryName);
                    throw new IllegalArgumentException("无法实例化工厂类[" + executorServiceFactoryName + "]");
                }
            }
            return factory;
        } finally {
            this.lock.writeLock().unlock();
        }
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

        for (Map.Entry<String, RefreshableExecutorService> item : executorServiceCache.entrySet()) {
            try {
                item.getValue().shutdown();
            } catch (Exception e) {
                log.error("Shutdown pool raise an error.Cause:", e);
            }
        }
        executorServiceCache.clear();

        for (Map.Entry<String, RefreshableScheduledExecutorService> item : this.scheduleExecutorServiceCache.entrySet()) {
            try {
                item.getValue().shutdown();
            } catch (Exception e) {
                log.error("Shutdown schedule pool raise an error.Cause:", e);
            }
        }
        this.scheduleExecutorServiceCache.clear();
    }

    @Override
    public boolean isShutdown() {
        return isShutdown.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Boolean update(Collection<ThreadPoolConfig> threadPoolConfigs) {
        try {
            this.lock.writeLock().lock();
            for (ThreadPoolConfig threadPoolConfig : threadPoolConfigs) {

                final String threadPoolsName = threadPoolConfig.getThreadPoolsName();
                final RefreshableExecutorService<ExecutorService, ThreadPoolConfig> refreshableExecutorService = this.executorServiceCache.get(threadPoolsName);
                if (null == refreshableExecutorService) {
                    return Boolean.TRUE;
                }
                final RefreshableExecutorService<?, ?> newThreadPoolFactory = this.createNewThreadPoolFactory(threadPoolConfig, false);
                refreshableExecutorService.refresh(newThreadPoolFactory.delegate());
            }
        } finally {
            this.lock.writeLock().unlock();
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean delete(Collection<ThreadPoolConfig> threadPoolConfigs) {
        // TODO
        deleteExecutorService(threadPoolConfigs);
        return null;
    }

    @SuppressWarnings("unchecked")
    private void deleteExecutorService(Collection<ThreadPoolConfig> threadPoolConfigs) {
        for (Map.Entry<String, RefreshableExecutorService> item : executorServiceCache.entrySet()) {
            final RefreshableExecutorService<ExecutorService, ThreadPoolConfig> executorService = item.getValue();
            final ThreadPoolConfig config = executorService.config();
            if (!threadPoolConfigs.contains(config)) {
                continue;
            }
            String domain = item.getKey();
            final int lastIndexOf = domain.lastIndexOf('.');
            if (lastIndexOf > 0) {
                domain = domain.substring(0, lastIndexOf);
            } else {
                domain = "root";
            }

            final UsingConfig usingSetting = this.config.findUsingSetting(domain);
            final RefreshableExecutorService refreshableExecutorService = executorServiceCache.get(usingSetting.getThreadPoolsName());

        }
    }

    @Override
    public Boolean add(Collection<ThreadPoolConfig> threadPoolConfigs) {
        return null;
    }
}
