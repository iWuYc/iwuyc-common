package com.iwuyc.tools.commons.thread.conf;

/**
 * UsingConfig
 * 
 * @author @Neil
 * @since @2017年10月15日
 */
public class UsingConfig {
    private final String domain;
    private final String threadPoolsName;

    private UsingConfig(String domain, String threadPoolsName) {
        this.domain = domain;
        this.threadPoolsName = threadPoolsName;
    }

    public static UsingConfig create(String domain, String config) {
        UsingConfig usingConfig = new UsingConfig(domain, config);
        return usingConfig;
    }

    public String getDomain() {
        return domain;
    }

    public String getThreadPoolsName() {
        return threadPoolsName;
    }

}
