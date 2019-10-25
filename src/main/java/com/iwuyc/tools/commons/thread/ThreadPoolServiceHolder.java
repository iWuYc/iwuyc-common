package com.iwuyc.tools.commons.thread;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程池服务持有类，用于静态方法调用，需先使用 {@link ThreadConfig#config(File)} 进行配置后，才可以使用
 *
 * @author Neil
 */
public class ThreadPoolServiceHolder {

    private static ThreadPoolsService threadPoolsService;

    public static ExecutorService get(Class<?> clazz) {
        return getThreadPoolsService().getExecutorService(clazz);
    }

    public static ScheduledExecutorService getScheduleService(Class<?> clazz) {
        return getThreadPoolsService().getScheduledExecutor(clazz);
    }

    public static ThreadPoolsService getThreadPoolsService() {
        if (null == threadPoolsService) {
            throw new IllegalArgumentException(
                    "进行流量池配置，请先调用[com.iwuyc.tools.commons.thread.ThreadConfig.config(java.io.File)]方法进行配置。");
        }
        return threadPoolsService;
    }

    public static void setThreadPoolsService(ThreadPoolsService threadPoolsService) {
        ThreadPoolServiceHolder.threadPoolsService = threadPoolsService;
    }
}
