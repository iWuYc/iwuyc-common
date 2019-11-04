package com.iwuyc.tools.commons.thread.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.iwuyc.tools.commons.classtools.ClassUtils;
import com.iwuyc.tools.commons.thread.*;
import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;
import com.iwuyc.tools.commons.thread.conf.UsingConfig;
import com.iwuyc.tools.commons.thread.impl.bean.ExecutorServiceTuple;
import com.iwuyc.tools.commons.thread.impl.bean.RefreshableExecutorServiceTuple;
import com.iwuyc.tools.commons.thread.impl.bean.RefreshableExecutorServiceTupleLoader;
import com.iwuyc.tools.commons.thread.impl.bean.RefreshableExecutorServiceTuplesLoader;
import com.iwuyc.tools.commons.util.string.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
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
    /**
     *
     */
    private final Map<String, ExecutorServiceFactory> executorServiceFactoryCache = new ConcurrentHashMap<>();
    /**
     * 线程池名字-线程池实例
     */
    private final Cache<String, ExecutorServiceTuple> nameExecutorServiceCache = CacheBuilder.newBuilder().build();
    /**
     * 线程池元组 -> 对应引用当前线程池元组的封装实例
     */
    private final LoadingCache<ExecutorServiceTuple, Collection<RefreshableExecutorServiceTuple>> executorServiceGuavaCache = CacheBuilder.newBuilder().build(new RefreshableExecutorServiceTuplesLoader());

    /**
     * domain -> RefreshableExecutorServiceTuple
     */
    private final LoadingCache<String, RefreshableExecutorServiceTuple> domainRefreshableTupleServices = CacheBuilder.newBuilder().build(new RefreshableExecutorServiceTupleLoader());


    private ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private ThreadConfig threadConfig;
    private AtomicBoolean isShutdown = new AtomicBoolean();

    public DefaultThreadPoolsServiceImpl(ThreadConfig threadConfig) {
        this.threadConfig = threadConfig;
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
        return getExecutorServiceByMap(domain, false);
    }

    @Override
    public ScheduledExecutorService getScheduledExecutor(String domain) {
        return getExecutorServiceByMap(domain, true);
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

    @SuppressWarnings("unchecked")
    private <T extends RefreshableExecutorService> T getExecutorServiceByMap(String domain, boolean isScheduleService) {
        if (StringUtils.isEmpty(domain)) {
            log.debug("未指定domain，将使用默认的domain：{}", DEFAULT_DOMAIN);
            domain = DEFAULT_DOMAIN;
        }

        log.debug("Get executor service for domain:{}", domain);
        RefreshableExecutorService result = findOrCreateRefreshableExecutorServiceTuple(domain, isScheduleService);
        return (T) result;
    }

    private RefreshableExecutorService findOrCreateRefreshableExecutorServiceTuple(String domain, boolean isScheduleService) {

        final RefreshableExecutorServiceTuple refreshableExecutorServiceTuple = this.domainRefreshableTupleServices.getUnchecked(domain);
        try {
            this.lock.readLock().lock();
            RefreshableExecutorService result;
            if (isScheduleService) {
                result = refreshableExecutorServiceTuple.getScheduledExecutorService();
            } else {
                result = refreshableExecutorServiceTuple.getExecutorService();
            }
            if (result != null) {
                return result;
            }
        } finally {
            this.lock.readLock().unlock();
        }

        UsingConfig usingConfig = this.threadConfig.findUsingSetting(domain);


        ExecutorServiceTuple executorServiceTuple = nameExecutorServiceCache.getIfPresent(usingConfig.getThreadPoolsName());
        if (null != executorServiceTuple) {
            log.debug("找到[{}]对应的executorService。", usingConfig.getDomain());
            RefreshableExecutorService executorService = isScheduleService ? WrappingScheduledExecutorService.create(executorServiceTuple) : new WrappingExecutorService<>(executorServiceTuple.getExecutorService(), executorServiceTuple.getConfig());
            return (T) executorService;
        }

        Lock writeLock = this.lock.writeLock();
        try {
            writeLock.lock();

            if (container.containsKey(usingConfig.getDomain())) {
                wrappingExecutorService = container.get(usingConfig.getDomain());
                log.debug("找到[{}]对应的executorService。", usingConfig.getDomain());
                return wrappingExecutorService;
            }

            ThreadPoolConfig threadPoolConfig = this.threadConfig.findThreadPoolConfig(usingConfig.getThreadPoolsName());
            executorServiceTuple = getOrCreateExecutorServiceTuple(threadPoolConfig);

//            final ExecutorService executorService;
//            if (isScheduleExecutor(targetType)) {
//                executorService = executorServiceTuple.getScheduledExecutorService();
//                wrappingExecutorService = (T) new WrappingScheduledExecutorService((ScheduledExecutorService) executorService, threadPoolConfig);
//            } else {
//                executorService = executorServiceTuple.getExecutorService();
//                wrappingExecutorService = (T) new WrappingExecutorService<>(executorService, threadPoolConfig);
//            }

            container.put(usingConfig.getDomain(), wrappingExecutorService);
            container.put(domain, wrappingExecutorService);

            return wrappingExecutorService;
        } finally {
            writeLock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends RefreshableExecutorService<?, ?>> T findThreadPoolOrCreate(String domain, Map<String, T> container, boolean isScheduleService) {

        UsingConfig usingConfig = this.threadConfig.findUsingSetting(domain);


        ExecutorServiceTuple executorServiceTuple = nameExecutorServiceCache.getUnchecked(usingConfig.getThreadPoolsName());
        if (null != executorServiceTuple) {
            log.debug("找到[{}]对应的executorService。", usingConfig.getDomain());
            RefreshableExecutorService executorService = isScheduleService ? WrappingScheduledExecutorService.create(executorServiceTuple) : new WrappingExecutorService<>(executorServiceTuple.getExecutorService(), executorServiceTuple.getConfig());
            return (T) executorService;
        }

        Lock writeLock = this.lock.writeLock();
        try {
            writeLock.lock();

            if (container.containsKey(usingConfig.getDomain())) {
                wrappingExecutorService = container.get(usingConfig.getDomain());
                log.debug("找到[{}]对应的executorService。", usingConfig.getDomain());
                return wrappingExecutorService;
            }

            ThreadPoolConfig threadPoolConfig = this.threadConfig.findThreadPoolConfig(usingConfig.getThreadPoolsName());
            executorServiceTuple = getOrCreateExecutorServiceTuple(threadPoolConfig);

//            final ExecutorService executorService;
//            if (isScheduleExecutor(targetType)) {
//                executorService = executorServiceTuple.getScheduledExecutorService();
//                wrappingExecutorService = (T) new WrappingScheduledExecutorService((ScheduledExecutorService) executorService, threadPoolConfig);
//            } else {
//                executorService = executorServiceTuple.getExecutorService();
//                wrappingExecutorService = (T) new WrappingExecutorService<>(executorService, threadPoolConfig);
//            }

            container.put(usingConfig.getDomain(), wrappingExecutorService);
            container.put(domain, wrappingExecutorService);

            return wrappingExecutorService;
        } finally {
            writeLock.unlock();
        }
    }

    private <T extends ExecutorService> boolean isScheduleExecutor(Class<T> targetType) {
        return ScheduledExecutorService.class.isAssignableFrom(targetType);
    }

    @SuppressWarnings("unchecked")
    private ExecutorServiceTuple getOrCreateExecutorServiceTuple(ThreadPoolConfig threadPoolConfig) {
        final String threadPoolsName = threadPoolConfig.getThreadPoolsName();
        ExecutorServiceTuple executorServiceTuple = this.nameExecutorServiceCache.getUnchecked(threadPoolsName);
        if (null != executorServiceTuple) {
            return executorServiceTuple;
        }

        try {
            this.lock.writeLock().lock();
            final String executorServiceFactoryName = threadPoolConfig.getFactory();
            ExecutorServiceFactory executorServiceFactory = executorServiceFactoryCache.get(executorServiceFactoryName);
            if (null == executorServiceFactory) {
                executorServiceFactory = createOrGetExecutorServiceFactory(executorServiceFactoryName);
            }
            executorServiceTuple = new ExecutorServiceTuple(executorServiceFactory, threadPoolConfig);
            this.nameExecutorServiceCache.put(threadPoolsName, executorServiceTuple);
            return executorServiceTuple;
        } finally {
            this.lock.writeLock().unlock();
        }


//        T instance;
//        if (isScheduleExecutor) {
//            ScheduledExecutorService instanceTmp = executorServiceFactory.createSchedule(threadPoolConfig);
//            instance = (T) new WrappingScheduledExecutorService(instanceTmp, threadPoolConfig);
//        } else {
//            ExecutorService instanceTmp = executorServiceFactory.create(threadPoolConfig);
//            instance = (T) new WrappingExecutorService<>(instanceTmp, threadPoolConfig);
//        }
//        return instance;
    }

    private ExecutorServiceFactory createOrGetExecutorServiceFactory(String executorServiceFactoryName) {
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
            executorServiceFactoryCache.put(executorServiceFactoryName, factory);
            return factory;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public ThreadConfig getThreadConfig() {
        return this.threadConfig;
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
                final RefreshableExecutorService<?, ?> newThreadPoolFactory = this.getOrCreateExecutorServiceTuple(threadPoolConfig, false);
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
        deleteExecutorService(threadPoolConfigs, this.executorServiceCache);
        return null;
    }

    @SuppressWarnings("unchecked")
    private void deleteExecutorService(Collection<ThreadPoolConfig> threadPoolConfigs, Map<String, RefreshableExecutorService> executorServiceCache) {
        Collection<RefreshableExecutorService> refreshableExecutorServices = new HashSet<>(threadPoolConfigs.size());
        for (Map.Entry<String, RefreshableExecutorService> item : executorServiceCache.entrySet()) {
            final RefreshableExecutorService<ExecutorService, ThreadPoolConfig> executorService = item.getValue();
            final ThreadPoolConfig config = executorService.config();
            if (!threadPoolConfigs.contains(config)) {
                continue;
            }

            RefreshableExecutorService<ExecutorService, ThreadPoolConfig> parentThreadPools = findParentThreadPools(item.getKey(), threadPoolConfigs);
            executorService.refresh(parentThreadPools.delegate());

            refreshableExecutorServices.add(parentThreadPools);
        }
    }

    @SuppressWarnings("unchecked")
    private RefreshableExecutorService<ExecutorService, ThreadPoolConfig> findParentThreadPools(String currentDomain, Collection<ThreadPoolConfig> shouldBeDeleteThreadPoolConfig) {
        String domain = currentDomain;
        do {
            final int lastIndexOf = domain.lastIndexOf('.');
            if (lastIndexOf > 0) {
                domain = domain.substring(0, lastIndexOf);
            } else {
                domain = "root";
            }

            final UsingConfig usingSetting = this.threadConfig.findUsingSetting(domain);
            final RefreshableExecutorService<ExecutorService, ThreadPoolConfig> refreshableExecutorService = executorServiceCache.get(usingSetting.getThreadPoolsName());
            ThreadPoolConfig config = refreshableExecutorService.config();
            if (!shouldBeDeleteThreadPoolConfig.contains(config)) {
                return refreshableExecutorService;
            }
        } while (!"root".equals(domain));

        return executorServiceCache.get("default");
    }

    @Override
    public Boolean add(Collection<ThreadPoolConfig> threadPoolConfigs) {
        return null;
    }

}
