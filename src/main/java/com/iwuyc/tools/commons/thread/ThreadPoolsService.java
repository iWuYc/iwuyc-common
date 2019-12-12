package com.iwuyc.tools.commons.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程池的服务
 *
 * @author @Neil
 * @since @2017年10月15日
 */
public interface ThreadPoolsService {
    /**
     * 通过类进行获取相应的线程池实例。
     *
     * @param clazz 类
     * @return 线程池实例
     */
    ExecutorService getExecutorService(Class<?> clazz);

    /**
     * 提供过字符串获取相应的线程池实例。
     *
     * @param domain 作用域，只能出现英文字母、“_”、“.”，以半角“.”字符作为分隔符，可以是包名，类全限定名。
     * @return 线程池实例
     */
    ExecutorService getExecutorService(String domain);

    /**
     * 获取定时执行的线程池
     *
     * @param clazz 类
     * @return 线程池实例
     */
    ScheduledExecutorService getScheduledExecutor(Class<?> clazz);

    /**
     * 获取定时执行的线程池
     *
     * @param domain 作用域，只能出现英文字母、“_”、“.”，以半角“.”字符作为分隔符，可以是包名，类全限定名。
     * @return 线程池实例
     */
    ScheduledExecutorService getScheduledExecutor(String domain);

    /**
     * 获取配置信息
     *
     * @return 配置实例
     */
    ThreadConfig getThreadConfig();

    /**
     * 停止该线程池服务，并释放相应的资源
     */
    void shutdown();

    /**
     * 是否已经停掉当前线程池服务
     *
     * @return true 为已经停掉，否则为未停止
     */
    boolean isShutdown();
}
