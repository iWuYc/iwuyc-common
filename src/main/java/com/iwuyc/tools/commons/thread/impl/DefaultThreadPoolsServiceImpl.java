package com.iwuyc.tools.commons.thread.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.iwuyc.tools.commons.classtools.ClassUtils;
import com.iwuyc.tools.commons.thread.*;
import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;
import com.iwuyc.tools.commons.thread.conf.UsingConfig;
import com.iwuyc.tools.commons.thread.impl.bean.JDKExecutorServiceTuple;
import com.iwuyc.tools.commons.thread.impl.bean.RefreshableExecutorServiceTuple;
import com.iwuyc.tools.commons.thread.impl.bean.RefreshableExecutorServiceTupleLoader;
import com.iwuyc.tools.commons.thread.impl.bean.RefreshableExecutorServiceTuplesLoader;
import com.iwuyc.tools.commons.util.string.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
@SuppressWarnings("rawtypes")
@Slf4j
public class DefaultThreadPoolsServiceImpl implements ThreadPoolsService, ModifiableService {

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
    private final LoadingCache<JDKExecutorServiceTuple, Collection<RefreshableExecutorServiceTuple>> jdkExecutorMappingRefreshableExecutor = CacheBuilder.newBuilder().build(new RefreshableExecutorServiceTuplesLoader());

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
        domain = checkDomain(domain);
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

            linkedExecutors(jdkExecutorServiceTuple, refreshableExecutorServiceTuple);
            return wrappingScheduledExecutorService;
        }
    }

    /**
     * 将两种executor建立关联关系
     *
     * @param jdkExecutorServiceTuple         实际的executor
     * @param refreshableExecutorServiceTuple 可更新、引用类型、代理类型executor
     */
    private void linkedExecutors(JDKExecutorServiceTuple jdkExecutorServiceTuple, RefreshableExecutorServiceTuple refreshableExecutorServiceTuple) {
        final Collection<RefreshableExecutorServiceTuple> refreshableExecutorServiceTuples = this.jdkExecutorMappingRefreshableExecutor.getUnchecked(jdkExecutorServiceTuple);
        if (!refreshableExecutorServiceTuples.contains(refreshableExecutorServiceTuple)) {
            refreshableExecutorServiceTuples.add(refreshableExecutorServiceTuple);
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
        domain = checkDomain(domain);
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

            linkedExecutors(jdkExecutorServiceTuple, refreshableExecutorServiceTuple);

            return wrappingScheduledExecutorService;
        }
    }


    @Nonnull
    private String checkDomain(String domain) {
        if (StringUtils.isEmpty(domain)) {
            log.debug("未指定domain，将使用默认的domain：{}", DEFAULT_DOMAIN);
            domain = DEFAULT_DOMAIN;
        }
        log.debug("Get executor service for domain:{}", domain);
        return domain;
    }

    private JDKExecutorServiceTuple findOrCreateJdkExecutorServiceTuple(String domain) {


        UsingConfig usingConfig = this.threadConfig.findUsingSetting(domain);
        final ThreadPoolConfig threadPoolConfig = this.threadConfig.findThreadPoolConfig(usingConfig.getThreadPoolsName());
        JDKExecutorServiceTuple jdkExecutorServiceTuple = getOrCreateExecutorServiceTuple(threadPoolConfig);
        log.debug("找到[{}]对应的executorService。", usingConfig.getDomain());

        return jdkExecutorServiceTuple;
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

        if (!isShutdown.compareAndSet(false, true)) {
            return;
        }
        for (Map.Entry<JDKExecutorServiceTuple, Collection<RefreshableExecutorServiceTuple>> item : this.jdkExecutorMappingRefreshableExecutor.asMap().entrySet()) {
            JDKExecutorServiceTuple itemKey = item.getKey();
            if (!itemKey.getScheduledExecutorService().isShutdown()) {
                itemKey.getScheduledExecutorService().shutdown();
            }
            if (!itemKey.getExecutorService().isShutdown()) {
                itemKey.getExecutorService().shutdown();
            }

            Collection<RefreshableExecutorServiceTuple> itemVal = item.getValue();
            itemVal.clear();
        }

        this.jdkExecutorMappingRefreshableExecutor.invalidateAll();
        this.nameExecutorServiceCache.invalidateAll();
        this.domainRefreshableTupleServices.invalidateAll();
        this.executorServiceFactoryCache.clear();
    }

    @Override
    public boolean isShutdown() {
        return this.isShutdown.get();
    }

    @Override
    public boolean refresh() {
        final ConcurrentMap<JDKExecutorServiceTuple, Collection<RefreshableExecutorServiceTuple>> jdkExecutorMappingRefreshableExecutorMap = this.jdkExecutorMappingRefreshableExecutor.asMap();

        final Map<ThreadPoolConfig, JDKExecutorServiceTuple> configJdkServiceTupleMap = jdkExecutorMappingRefreshableExecutorMap.keySet().stream().collect(Collectors.toMap(JDKExecutorServiceTuple::getConfig, item -> item));

        for (Map.Entry<JDKExecutorServiceTuple, Collection<RefreshableExecutorServiceTuple>> item : jdkExecutorMappingRefreshableExecutorMap.entrySet()) {
            final Collection<RefreshableExecutorServiceTuple> itemValue = item.getValue();
            final JDKExecutorServiceTuple itemKey = item.getKey();

            Iterator<RefreshableExecutorServiceTuple> itemValueIt = itemValue.iterator();
            while (itemValueIt.hasNext()) {
                final RefreshableExecutorServiceTuple refreshableExecutorServiceTupleItem = itemValueIt.next();

                if (!pickOutNewJdkTuple(itemKey, configJdkServiceTupleMap, refreshableExecutorServiceTupleItem)) {
                    continue;
                }
                itemValueIt.remove();
            }
        }

        releaseDidNotUsingInstance();

        return false;
    }

    private void releaseDidNotUsingInstance() {
        final ConcurrentMap<JDKExecutorServiceTuple, Collection<RefreshableExecutorServiceTuple>> jdkExecutorMappingRefreshableExecutorMap = this.jdkExecutorMappingRefreshableExecutor.asMap();
        for (Map.Entry<JDKExecutorServiceTuple, Collection<RefreshableExecutorServiceTuple>> item : jdkExecutorMappingRefreshableExecutorMap.entrySet()) {
//            item.
        }
    }

    private boolean pickOutNewJdkTuple(JDKExecutorServiceTuple oldJdkService, Map<ThreadPoolConfig, JDKExecutorServiceTuple> configJdkServiceTupleMap, RefreshableExecutorServiceTuple refreshableExecutorServiceTuple) {
        final String domain = refreshableExecutorServiceTuple.getDomain();
        final UsingConfig currentUsingConfig = this.threadConfig.findUsingSetting(domain);
        final ThreadPoolConfig threadPoolConfig = this.threadConfig.findThreadPoolConfig(currentUsingConfig.getThreadPoolsName());
        // 两个配置一致，无需改变
        if (oldJdkService.getConfig().equals(threadPoolConfig)) {
            return false;
        }
        JDKExecutorServiceTuple newJdkExecutorServiceTuple = configJdkServiceTupleMap.get(threadPoolConfig);
        if (null == newJdkExecutorServiceTuple) {
            newJdkExecutorServiceTuple = getOrCreateExecutorServiceTuple(threadPoolConfig);
        }

        refreshableExecutor(newJdkExecutorServiceTuple, refreshableExecutorServiceTuple);
        this.linkedExecutors(newJdkExecutorServiceTuple, refreshableExecutorServiceTuple);
        return true;
    }

    @SuppressWarnings("unchecked")
    private void refreshableExecutor(JDKExecutorServiceTuple jdkExecutorServiceTuple, RefreshableExecutorServiceTuple refreshableExecutorServiceTuple) {
        final ThreadPoolConfig threadPoolConfig = jdkExecutorServiceTuple.getConfig();

        if (null != refreshableExecutorServiceTuple.getExecutorService()) {
            final WrappingExecutorService<ExecutorService> executorService = (WrappingExecutorService<ExecutorService>) refreshableExecutorServiceTuple.getExecutorService();
            executorService.refresh(jdkExecutorServiceTuple.getExecutorService());
            executorService.updateConfig(threadPoolConfig);
        }

        if (null != refreshableExecutorServiceTuple.getScheduledExecutorService()) {
            final RefreshableScheduledExecutorService scheduledExecutorService = refreshableExecutorServiceTuple.getScheduledExecutorService();
            scheduledExecutorService.refresh(jdkExecutorServiceTuple.getScheduledExecutorService());
            scheduledExecutorService.updateConfig(threadPoolConfig);
        }

    }

}
