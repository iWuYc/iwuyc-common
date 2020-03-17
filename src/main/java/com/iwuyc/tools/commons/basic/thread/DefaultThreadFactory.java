package com.iwuyc.tools.commons.basic.thread;

import com.iwuyc.tools.commons.util.string.StringUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 默认线程工厂类
 *
 * @author Neil
 * @since 2017年10月15日
 */
@Data
@Slf4j
class DefaultThreadFactory implements ThreadFactory {
    private static final String SCHEDULE_EXECUTOR_NAME_FORMAT = "schedule-%s";

    /**
     * 线程名字前缀
     */
    private final ThreadFactoryConf threadFactoryConf;

    /**
     * 线程标志位，每产生一个线程则自增1
     */
    @Getter(AccessLevel.NONE)
    private final AtomicLong flag = new AtomicLong();
    private final String threadPreName;

    /**
     * 线程工厂
     *
     * @param threadFactoryConf 线程池配置项
     */
    public DefaultThreadFactory(ThreadFactoryConf threadFactoryConf) {
        this.threadFactoryConf = threadFactoryConf;
        String threadPoolsName = this.threadFactoryConf.getThreadPoolsName();
        String innerThreadPreName;
        if (StringUtils.isEmpty(threadPoolsName)) {
            innerThreadPreName = "tpgFramework";
            log.warn("未指定线程池名将使用默认的线程池名前缀{}", innerThreadPreName);
        } else {
            innerThreadPreName = threadPoolsName;
        }
        final String nameFormat = this.threadFactoryConf.isForSchedulePools() ? String.format(SCHEDULE_EXECUTOR_NAME_FORMAT, innerThreadPreName) : innerThreadPreName;
        threadPreName = nameFormat + "-%s";
        log.debug("线程池名称前缀为：{}", this.threadPreName);
    }

    @Override
    public Thread newThread(@Nonnull Runnable runnable) {
        String threadName = builderThreadName();
        Thread result = new Thread(runnable, threadName);
        result.setDaemon(this.threadFactoryConf.isDaemon());
        return result;
    }

    private String builderThreadName() {
        return String.format(threadPreName, flag.getAndIncrement());
    }
}
