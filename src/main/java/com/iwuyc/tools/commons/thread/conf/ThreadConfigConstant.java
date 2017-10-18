package com.iwuyc.tools.commons.thread.conf;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public interface ThreadConfigConstant {
    /**
     * 配置中以这个开头的，表示是线程池的创建配置
     */
    String THREAD_CONFIG_PRENAME = "thread.conf";

    /**
     * 配置中以这个开头的，表示是线程的使用配置
     */
    String THREAD_USING_PRENAME = "thread.using";
}
