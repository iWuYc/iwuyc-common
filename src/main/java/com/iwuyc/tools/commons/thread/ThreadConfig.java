package com.iwuyc.tools.commons.thread;

import com.iwuyc.tools.commons.basic.AbstractMapUtil;
import com.iwuyc.tools.commons.classtools.ClassUtils;
import com.iwuyc.tools.commons.thread.conf.ThreadConfigConstant;
import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;
import com.iwuyc.tools.commons.thread.conf.UsingConfig;
import com.iwuyc.tools.commons.thread.impl.DefaultThreadPoolsServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 线程池的配置项
 *
 * @author @Neil
 * @since @2017年10月15日
 */
@Slf4j
public class ThreadConfig {
    public static final String DEFAULT_CONF = "/thread/thread.properties";

    /**
     * 线程池配置缓存。key是线程池的名字，val为线程池的配置实例。
     */
    private final Map<String, ThreadPoolConfig> threadConfigCache = new HashMap<>();

    /**
     * 使用于配置缓存。key是范围，val为线程池名字。
     */
    private final Map<String, UsingConfig> usingConfigCache = new ConcurrentHashMap<>();

    private Properties propertis;

    public ThreadConfig() {
    }

    /**
     * 提供配置文件，直接返回默认的 ThreadPoolsService 实例。
     *
     * @param file 可以为空，空则取classpath中/thread/thread.properties的默认配置
     * @return 返回threadPoolService的实例
     */
    public static ThreadPoolsService config(File file) {
        if (null == file) {
            log.warn("未指定配置文件，将使用默认配置进行配置。[classpath:{}]", DEFAULT_CONF);
            URL defaultConfigFile = ThreadConfig.class.getResource(DEFAULT_CONF);
            log.info("默认配置文件全路径为：[{}]", defaultConfigFile.getFile());
            file = new File(defaultConfigFile.getFile());
        }

        ThreadConfig config = new ThreadConfig();
        try (InputStream in = new FileInputStream(file)) {
            config.load(in);
            ThreadPoolServiceHolder.setThreadPoolsService(new DefaultThreadPoolsServiceImpl(config));
            log.info("初始化线程池框架完成。");
            return ThreadPoolServiceHolder.getThreadPoolsService();
        } catch (IOException e) {
            log.error("Config thread pool service raise an error:", e);
        }
        return null;
    }

    private void usingConfig(Map<Object, Object> usingInfo) {
        String key;
        String prefixUsingDomain;
        String config;
        UsingConfig usingConfig;
        for (Entry<Object, Object> item : usingInfo.entrySet()) {
            key = String.valueOf(item.getKey());
            prefixUsingDomain = key.substring(ThreadConfigConstant.THREAD_USING_PRENAME.length() + 1);

            config = String.valueOf(item.getValue());
            usingConfig = UsingConfig.create(prefixUsingDomain, config);
            usingConfigCache.put(usingConfig.getDomain(), usingConfig);
        }

    }

    private void config(Map<Object, Object> configInfo) {

        Set<Object> keys = configInfo.keySet();

        String threadPoolsNamePrefix;
        String key;
        Map<Object, Object> threadPoolFacotryConfig;

        while (!keys.isEmpty()) {
            key = String.valueOf(keys.iterator().next());
            threadPoolsNamePrefix = findThreadNameIncludePrefix(key);
            threadPoolFacotryConfig = AbstractMapUtil.findEntryByPrefixKey(configInfo, threadPoolsNamePrefix + '.');

            threadPoolFacotryConfig.forEach(configInfo::remove);

            threadPoolFactoryConfig(threadPoolsNamePrefix, threadPoolFacotryConfig);

            keys = configInfo.keySet();
        }
    }

    private void threadPoolFactoryConfig(String prefix, Map<Object, Object> threadPoolConfig) {
        final Map<String, Object> injectFieldVal = new HashMap<>(threadPoolConfig.size());
        threadPoolConfig.forEach((k, v) -> {
            String newKey = String.valueOf(k).substring(prefix.length() + 1);
            injectFieldVal.put(newKey, v);
        });
        final String threadPoolsName = prefix.substring(ThreadConfigConstant.THREAD_CONFIG_PRENAME.length() + 1);
        injectFieldVal.put("threadPoolsName", threadPoolsName);

        ThreadPoolConfig config = new ThreadPoolConfig();
        Map<Object, Object> otherSetting = ClassUtils.injectFields(config, injectFieldVal);
        config.setOtherSetting(otherSetting);

        threadConfigCache.put(config.getThreadPoolsName(), config);
    }

    private String findThreadNameIncludePrefix(String key) {
        int prefixLength = ThreadConfigConstant.THREAD_CONFIG_PRENAME.length();
        int endIndex = key.indexOf('.', prefixLength + 1);
        return key.substring(0, endIndex);
    }

    public UsingConfig findUsingSetting(String domain) {
        String innerDomain = domain;
        UsingConfig config;
        int lastDotIndex;
        do {
            config = usingConfigCache.get(innerDomain);
            if (null != config) {
                break;
            }
            lastDotIndex = innerDomain.lastIndexOf('.');
            if (lastDotIndex < 0) {
                break;
            }
            innerDomain = innerDomain.substring(0, lastDotIndex);
        } while (true);

        if (null == config) {
            log.debug("未找到匹配的命名空间[{}]配置。使用默认配置:[root]", domain);
            config = usingConfigCache.get("root");
        }
        usingConfigCache.put(domain, config);
        return config;
    }

    public ThreadPoolConfig findThreadPoolConfig(String threadPoolsName) {
        ThreadPoolConfig config = this.threadConfigCache.get(threadPoolsName);
        if (null == config) {
            config = this.threadConfigCache.get("default");
        }
        return config;
    }


    /**
     * 可以重复调用多次，增加新的配置项，或者修改配置项
     *
     * @param in 配置文件的文本流
     * @throws IOException 流读取异常
     */
    public void load(InputStream in) throws IOException {
        if (null == this.propertis) {
            log.debug("未初始化过，进行初始化。");
            this.propertis = new Properties();

            // 加载默认配置。
            try (InputStream defaultSettings = DefaultThreadPoolsServiceImpl.class
                    .getResourceAsStream(DEFAULT_CONF)) {
                this.propertis.load(defaultSettings);
                this.propertis.putAll(InitDefaultProperties.DEFAULT_SETTING);
                log.info("加载默认的配置完成。[{}]", this.propertis);
            }
        }
        if (null != in) {
            propertis.load(in);
        }

        Map<Object, Object> configInfo = AbstractMapUtil
                .findEntryByPrefixKey(this.propertis, ThreadConfigConstant.THREAD_CONFIG_PRENAME);
        config(configInfo);

        Map<Object, Object> usingInfo = AbstractMapUtil
                .findEntryByPrefixKey(this.propertis, ThreadConfigConstant.THREAD_USING_PRENAME);
        usingConfig(usingInfo);
    }

    private static class InitDefaultProperties {
        private final static Map<String, String> DEFAULT_SETTING;
        private static final String DEFAULT_CORE_POOL_SIZE = "thread.conf.default.corePoolSize";
        private static final String DEFAULT_MAX_POOL_SIZE = "thread.conf.default.maximumPoolSize";
        private static final String DEFAULT_SCHEDULE_CORE_POOL_SIZE = "thread.conf.defaultSchedule.corePoolSize";

        static {
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            HashMap<String, String> temp = new HashMap<>();
            temp.put(DEFAULT_CORE_POOL_SIZE, String.valueOf(availableProcessors));
            temp.put(DEFAULT_MAX_POOL_SIZE, String.valueOf(availableProcessors * 4));
            temp.put(DEFAULT_SCHEDULE_CORE_POOL_SIZE, String.valueOf(availableProcessors));
            DEFAULT_SETTING = Collections.unmodifiableMap(temp);
        }
    }

}
