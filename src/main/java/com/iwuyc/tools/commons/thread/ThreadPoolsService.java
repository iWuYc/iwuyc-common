package com.iwuyc.tools.commons.thread;

import java.util.concurrent.ExecutorService;

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
     * 获取配置信息
     *
     * @return 配置实例
     */
    ThreadConfig getConfig();

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
