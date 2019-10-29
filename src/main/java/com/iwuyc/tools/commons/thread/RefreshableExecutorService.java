package com.iwuyc.tools.commons.thread;

import java.util.concurrent.ExecutorService;

/**
 * 可刷新的线程池服务实现
 *
 * @param <Delegate> 代理对象，真实线程池类型
 * @author Neil
 */
public interface RefreshableExecutorService<Delegate extends ExecutorService, Config> extends RefreshDelegate<Delegate>, ExecutorService {
    /**
     * 获取实际代理对象
     *
     * @return 代理对象实例
     */
    Delegate delegate();

    /**
     * 获取当前线程池的配置
     *
     * @return 配置实例
     */
    Config config();
}
