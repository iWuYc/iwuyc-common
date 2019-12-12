package com.iwuyc.tools.commons.thread.conf;

import com.iwuyc.tools.commons.basic.type.TimeTuple;
import com.iwuyc.tools.commons.util.string.StringUtils;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Neil
 */
@EqualsAndHashCode
public class ThreadPoolConfig {

    private final String factory;
    /**
     * 线程池的名字
     */
    private String threadPoolsName;
    /**
     * 线程最小线程数
     */
    private int corePoolSize = 2;

    /**
     * 最大线程数
     */
    private int maximumPoolSize = 4;
    /**
     * 线程存活时间
     */
    private TimeTuple keepAliveTime;
    /**
     * 任务队列最大值
     */
    private int maxQueueSize = 1800;

    private Map<Object, Object> otherSetting;

    public ThreadPoolConfig() {
        this(null, null);
    }

    public ThreadPoolConfig(String factory, String threadPoolsName) {

        if (StringUtils.isEmpty(factory)) {
            factory = "com.iwuyc.tools.commons.thread.impl.ThreadPoolExecutor";
        }
        this.factory = factory;

        if (StringUtils.isEmpty(threadPoolsName)) {
            threadPoolsName = "thframe";
        }
        this.threadPoolsName = threadPoolsName;

    }

    public String getThreadPoolsName() {
        return threadPoolsName;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public TimeTuple getKeepAliveTime() {
        if (null == keepAliveTime) {
            this.keepAliveTime = TimeTuple.create(10, TimeUnit.MINUTES);
        }
        return keepAliveTime;
    }

    public void setKeepAliveTime(TimeTuple keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public String getFactory() {
        return factory;
    }

    public Map<Object, Object> getOtherSetting() {
        if (null == this.otherSetting) {
            this.otherSetting = Collections.emptyMap();
        }
        return otherSetting;
    }

    public void setOtherSetting(Map<Object, Object> otherSetting) {
        this.otherSetting = otherSetting;
    }
}
