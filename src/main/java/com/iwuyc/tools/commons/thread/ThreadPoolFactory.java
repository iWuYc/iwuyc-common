package com.iwuyc.tools.commons.thread;

import java.util.concurrent.ExecutorService;

public class ThreadPoolFactory {

    private static ThreadPoolsService threadPoolsService;

    public static ExecutorService get(Class<?> clazz) {
        if (null == threadPoolsService) {
            throw new IllegalArgumentException(
                    "进行流量池配置，请先调用[com.iwuyc.tools.commons.thread.ThreadConfig.config(java.io.File)]方法进行配置。");
        }
        return threadPoolsService.getExecutorService(clazz);
    }

    public static ThreadPoolsService getThreadPoolsService() {
        return threadPoolsService;
    }

    public static void setThreadPoolsService(ThreadPoolsService threadPoolsService) {
        ThreadPoolFactory.threadPoolsService = threadPoolsService;
    }
}
