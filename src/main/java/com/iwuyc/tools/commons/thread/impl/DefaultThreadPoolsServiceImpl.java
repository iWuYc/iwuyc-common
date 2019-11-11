package com.iwuyc.tools.commons.thread.impl;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.iwuyc.tools.commons.classtools.ClassUtils;
import com.iwuyc.tools.commons.thread.*;
import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;
import com.iwuyc.tools.commons.thread.conf.UsingConfig;
import com.iwuyc.tools.commons.thread.impl.bean.*;
import com.iwuyc.tools.commons.util.string.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
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
     * name->ExecutorServiceFactory
     */
    private final Map<String, ExecutorServiceFactory> executorServiceFactoryCache = new ConcurrentHashMap<>();
    /**
     * 线程池名字-线程池实例
     */
    private final Cache<String, JDKExecutorServiceTuple> nameExecutorServiceCache = CacheBuilder.newBuilder().build();
    /**
     * 线程池元组 -> 对应引用当前线程池元组的封装实例
     */
    private final LoadingCache<JDKExecutorServiceTuple, Collection<RefreshableExecutorServiceTuple>> executorServiceGuavaCache = CacheBuilder.newBuilder().build(new RefreshableExecutorServiceTuplesLoader());

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
        {
            Preconditions.checkNotNull(domain);
            final RefreshableExecutorServiceTuple refreshableExecutorServiceTuple = this.domainRefreshableTupleServices.getUnchecked(domain);
            if (null != refreshableExecutorServiceTuple.getExecutorService()) {
                return refreshableExecutorServiceTuple.getExecutorService();
            }

            synchronized (this) {
                if (null != refreshableExecutorServiceTuple.getExecutorService()) {
                    return refreshableExecutorServiceTuple.getExecutorService();
                }

                final JDKExecutorServiceTuple jdkExecutorServiceTuple = findOrCreateJdkExecutorServiceTuple(domain);
                final WrappingExecutorService wrappingScheduledExecutorService = WrappingExecutorService.create(jdkExecutorServiceTuple);
                refreshableExecutorServiceTuple.setExecutorService(wrappingScheduledExecutorService);
                return wrappingScheduledExecutorService;
            }
        }
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

    @Override
    public ScheduledExecutorService getScheduledExecutor(String domain) {
        Preconditions.checkNotNull(domain);
        final RefreshableExecutorServiceTuple refreshableExecutorServiceTuple = this.domainRefreshableTupleServices.getUnchecked(domain);
        if (null != refreshableExecutorServiceTuple.getScheduledExecutorService()) {
            return refreshableExecutorServiceTuple.getScheduledExecutorService();
        }

        synchronized (this) {
            if (null != refreshableExecutorServiceTuple.getScheduledExecutorService()) {
                return refreshableExecutorServiceTuple.getScheduledExecutorService();
            }

            final JDKExecutorServiceTuple jdkExecutorServiceTuple = findOrCreateJdkExecutorServiceTuple(domain);
            final WrappingScheduledExecutorService wrappingScheduledExecutorService = WrappingScheduledExecutorService.create(jdkExecutorServiceTuple);
            refreshableExecutorServiceTuple.setScheduledExecutorService(wrappingScheduledExecutorService);
            return wrappingScheduledExecutorService;
        }
    }


    private ExecutorServiceTuple getExecutorServiceByMap(String domain) {
        if (StringUtils.isEmpty(domain)) {
            log.debug("未指定domain，将使用默认的domain：{}", DEFAULT_DOMAIN);
            domain = DEFAULT_DOMAIN;
        }
        log.debug("Get executor service for domain:{}", domain);
        return findOrCreateJdkExecutorServiceTuple(domain);
    }

    private JDKExecutorServiceTuple findOrCreateJdkExecutorServiceTuple(String domain) {


        UsingConfig usingConfig = this.threadConfig.findUsingSetting(domain);
        final ThreadPoolConfig threadPoolConfig = this.threadConfig.findThreadPoolConfig(usingConfig.getThreadPoolsName());
        JDKExecutorServiceTuple jdkExecutorServiceTuple = getOrCreateExecutorServiceTuple(threadPoolConfig);
        log.debug("找到[{}]对应的executorService。", usingConfig.getDomain());

        return jdkExecutorServiceTuple;
    }

    private <T extends ExecutorService> boolean isScheduleExecutor(Class<T> targetType) {
        return ScheduledExecutorService.class.isAssignableFrom(targetType);
    }

    @Nonnull
    private JDKExecutorServiceTuple getOrCreateExecutorServiceTuple(ThreadPoolConfig threadPoolConfig) {
        final String threadPoolsName = threadPoolConfig.getThreadPoolsName();
        JDKExecutorServiceTuple JDKExecutorServiceTuple = this.nameExecutorServiceCache.getIfPresent(threadPoolsName);
        if (null != JDKExecutorServiceTuple) {
            return JDKExecutorServiceTuple;
        }

        try {
            this.lock.writeLock().lock();
            final String executorServiceFactoryName = threadPoolConfig.getFactory();
            ExecutorServiceFactory executorServiceFactory = executorServiceFactoryCache.get(executorServiceFactoryName);
            if (null == executorServiceFactory) {
                executorServiceFactory = createOrGetExecutorServiceFactory(executorServiceFactoryName);
            }
            JDKExecutorServiceTuple = new JDKExecutorServiceTuple(executorServiceFactory, threadPoolConfig);
            this.nameExecutorServiceCache.put(threadPoolsName, JDKExecutorServiceTuple);
            return JDKExecutorServiceTuple;
        } finally {
            this.lock.writeLock().unlock();
        }


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

//        if (!isShutdown.compareAndSet(false, true)) {
//            return;
//        }
//
//        for (Map.Entry<String, RefreshableExecutorService> item : executorServiceCache.entrySet()) {
//            try {
//                item.getValue().shutdown();
//            } catch (Exception e) {
//                log.error("Shutdown pool raise an error.Cause:", e);
//            }
//        }
//        executorServiceCache.clear();
//
//        for (Map.Entry<String, RefreshableScheduledExecutorService> item : this.scheduleExecutorServiceCache.entrySet()) {
//            try {
//                item.getValue().shutdown();
//            } catch (Exception e) {
//                log.error("Shutdown schedule pool raise an error.Cause:", e);
//            }
//        }
//        this.scheduleExecutorServiceCache.clear();
    }

    @Override
    public boolean isShutdown() {
        return isShutdown.get();
    }

    @Override
    public Boolean update(Collection<ThreadPoolConfig> threadPoolConfigs) {
//        try {
//            this.lock.writeLock().lock();
//            for (ThreadPoolConfig threadPoolConfig : threadPoolConfigs) {
//
//                final String threadPoolsName = threadPoolConfig.getThreadPoolsName();
//                final RefreshableExecutorService<ExecutorService, ThreadPoolConfig> refreshableExecutorService = this.executorServiceCache.get(threadPoolsName);
//                if (null == refreshableExecutorService) {
//                    return Boolean.TRUE;
//                }
//                final RefreshableExecutorService<?, ?> newThreadPoolFactory = this.getOrCreateExecutorServiceTuple(threadPoolConfig, false);
//                refreshableExecutorService.refresh(newThreadPoolFactory.delegate());
//            }
//        } finally {
//            this.lock.writeLock().unlock();
//        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean delete(Collection<ThreadPoolConfig> threadPoolConfigs) {
        // TODO
//        deleteExecutorService(threadPoolConfigs, this.executorServiceCache);
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
            if (null == parentThreadPools) {
                // TODO
                return;
            }
            executorService.refresh(parentThreadPools.delegate());

            refreshableExecutorServices.add(parentThreadPools);
        }
    }

    private RefreshableExecutorService<ExecutorService, ThreadPoolConfig> findParentThreadPools(String currentDomain, Collection<ThreadPoolConfig> shouldBeDeleteThreadPoolConfig) {
//        String domain = currentDomain;
//        do {
//            final int lastIndexOf = domain.lastIndexOf('.');
//            if (lastIndexOf > 0) {
//                domain = domain.substring(0, lastIndexOf);
//            } else {
//                domain = "root";
//            }
//
//            final UsingConfig usingSetting = this.threadConfig.findUsingSetting(domain);
//            final RefreshableExecutorService<ExecutorService, ThreadPoolConfig> refreshableExecutorService = executorServiceCache.get(usingSetting.getThreadPoolsName());
//            ThreadPoolConfig config = refreshableExecutorService.config();
//            if (!shouldBeDeleteThreadPoolConfig.contains(config)) {
//                return refreshableExecutorService;
//            }
//        } while (!"root".equals(domain));
//
//        return executorServiceCache.get("default");
        return null;
    }

    @Override
    public Boolean add(Collection<ThreadPoolConfig> threadPoolConfigs) {
        return null;
    }

}
