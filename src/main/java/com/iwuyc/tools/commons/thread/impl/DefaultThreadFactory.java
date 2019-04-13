package com.iwuyc.tools.commons.thread.impl;

import com.iwuyc.tools.commons.basic.StringUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
public class DefaultThreadFactory implements ThreadFactory {
    /**
     * 线程名字前缀
     */
    public final String threadPreName;

    /**
     * 线程标志位，每产生一个线程则自增1
     */
    public final AtomicLong flag = new AtomicLong();

    /**
     * 线程工厂
     *
     * @param threadPreName 线程名前缀
     */
    public DefaultThreadFactory(String threadPreName) {
        if (StringUtils.isEmpty(threadPreName)) {
            threadPreName = "thframe";
        }
        this.threadPreName = threadPreName + "-%s";
    }

    @Override
    public Thread newThread(Runnable r) {
        String threadName = builderThreadName();
        return new Thread(r, threadName);
    }

    private String builderThreadName() {
        return String.format(threadPreName,flag.getAndIncrement());
    }
}
