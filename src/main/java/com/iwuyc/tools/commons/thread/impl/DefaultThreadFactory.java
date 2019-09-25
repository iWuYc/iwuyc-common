package com.iwuyc.tools.commons.thread.impl;

import com.iwuyc.tools.commons.basic.AbstractStringUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
@Data
public class DefaultThreadFactory implements ThreadFactory {
    /**
     * 线程名字前缀
     */
    private final String threadPreName;

    /**
     * 线程标志位，每产生一个线程则自增1
     */
    @Getter(AccessLevel.NONE)
    private final AtomicLong flag = new AtomicLong();

    /**
     * 线程工厂
     *
     * @param threadPreName 线程名前缀
     */
    public DefaultThreadFactory(String threadPreName) {
        if (AbstractStringUtils.isEmpty(threadPreName)) {
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
        return String.format(threadPreName, flag.getAndIncrement());
    }
}
