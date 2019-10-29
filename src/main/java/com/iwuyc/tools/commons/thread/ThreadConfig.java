package com.iwuyc.tools.commons.thread;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.iwuyc.tools.commons.basic.type.TimeTuple;
import com.iwuyc.tools.commons.classtools.ClassUtils;
import com.iwuyc.tools.commons.thread.conf.FileInformation;
import com.iwuyc.tools.commons.thread.conf.ThreadConfigConstant;
import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;
import com.iwuyc.tools.commons.thread.conf.UsingConfig;
import com.iwuyc.tools.commons.thread.conf.typeconverter.String2TimeTupleConverter;
import com.iwuyc.tools.commons.thread.impl.DefaultThreadFactory;
import com.iwuyc.tools.commons.thread.impl.DefaultThreadPoolsServiceImpl;
import com.iwuyc.tools.commons.util.NumberUtils;
import com.iwuyc.tools.commons.util.collection.CollectionUtil;
import com.iwuyc.tools.commons.util.collection.MapUtil;
import com.iwuyc.tools.commons.util.file.FileUtil;
import com.iwuyc.tools.commons.util.file.PropertiesFileUtils;
import com.iwuyc.tools.commons.util.string.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * 线程池的配置项
 *
 * @author @Neil
 * @since @2017年10月15日
 */
@Slf4j
public class ThreadConfig {
    public static final String DEFAULT_CONF = "/thread/thread.properties";
    public static final String CORES_PLACEHOLDER = "cores";
    public static final String MATH_OPERATOR = "*";
    private static final String AUTO_SCAN_KEY = "thread.auto.scan";
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final ScheduledExecutorService AUTO_SCAN_TASK = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory("Thread-Config-Auto-Scan"));
    /**
     * 线程池配置缓存。key是线程池的名字，val为线程池的配置实例。
     */
    private final Map<String, ThreadPoolConfig> threadConfigCache = new ConcurrentHashMap<>();

    /**
     * 使用于配置缓存。key是范围，val为线程池名字。
     */
    private final Map<String, UsingConfig> usingConfigCache = new ConcurrentHashMap<>();
    private final ThreadLocal<ModifiableService<ThreadPoolConfig, Boolean>> modifiableServiceThreadLocal = new ThreadLocal<>();
    private Properties properties;
    private FileInformation fileInformation;

    public ThreadConfig() {
    }

    /**
     * 提供配置文件，直接返回默认的 ThreadPoolsService 实例。
     *
     * @param filePath 可以为空，空则取classpath中/thread/thread.properties的默认配置
     * @return 返回threadPoolService的实例
     */
    public static ThreadPoolsService config(String filePath) {
        File file;
        if (StringUtils.isEmpty(filePath)) {
            file = null;
        } else {
            file = new File(filePath);
        }
        return config(file);
    }

    /**
     * 提供配置文件，直接返回默认的 ThreadPoolsService 实例。
     *
     * @param file 可以为空，空则取classpath中/thread/thread.properties的默认配置
     * @return 返回threadPoolService的实例
     */
    public static ThreadPoolsService config(File file) {

        InputStream in = null;
        try {
            String configPath;
            if (null == file) {
                log.warn("未指定配置文件，将使用默认配置进行配置。[classpath:{}]", DEFAULT_CONF);
                URL defaultConfigFile = ThreadConfig.class.getResource(DEFAULT_CONF);
                log.info("默认配置文件全路径为：[{}]", defaultConfigFile.getFile());
                in = defaultConfigFile.openStream();
                configPath = FileUtil.absoluteLocation("classpath:/thread.properties");
            } else {
                in = new FileInputStream(file);
                configPath = file.getAbsolutePath();
            }

            ThreadConfig config = new ThreadConfig();
            config.load(in);
            config.setConfigFilePath(configPath);

            DefaultThreadPoolsServiceImpl defaultThreadPoolsService = new DefaultThreadPoolsServiceImpl(config);
            ThreadPoolServiceHolder.setThreadPoolsService(defaultThreadPoolsService);

            AUTO_SCAN_TASK.schedule(config::autoScanProperties, 1, TimeUnit.MINUTES);
            log.info("初始化线程池框架完成。");
            return ThreadPoolServiceHolder.getThreadPoolsService();
        } catch (IOException e) {
            log.error("Config thread pool service raise an error:", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {

                }
            }
        }

        return null;
    }

    private TimeTuple enableAutoScanTime() {
        String autoScanDelay = this.properties.getProperty(AUTO_SCAN_KEY, "1m");
        return String2TimeTupleConverter.converter(autoScanDelay);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void autoScanProperties() {
        TimeTuple timeTuple = enableAutoScanTime();
        if (timeTuple.getTime() <= 0) {
            return;
        }
        try {
            final ThreadPoolsService threadPoolsService = ThreadPoolServiceHolder.getThreadPoolsService();
            if (!(threadPoolsService instanceof ModifiableService)) {
                return;
            }
            modifiableServiceThreadLocal.set((ModifiableService) threadPoolsService);
            String filePath = this.fileInformation.getPath();
            final File file = new File(filePath);
            if (!file.exists() || file.lastModified() != this.fileInformation.getLastModifiedTime()) {
                return;
            }
            Properties newProperties = PropertiesFileUtils.propertiesReader(file, (ReadWriteLock) null);
            compareConfig(newProperties);
        } finally {
            // 扫描间隔至少在三十秒以上
            long millionTime = timeTuple.getTimeUnit().toMillis(timeTuple.getTime());
            long delayTime = Math.max(millionTime, 30_000);
            AUTO_SCAN_TASK.schedule(this::autoScanProperties, delayTime, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 更新thread.conf开头的配置项
     *
     * @param newProperties 新配置文件
     */
    private void compareConfig(Properties newProperties) {
        Map<Object, Object> oldConfigProperties = MapUtil
                .findEntryByPrefixKey(this.properties, ThreadConfigConstant.THREAD_CONFIG_PRENAME);
        Map<Object, Object> newConfigProperties = MapUtil
                .findEntryByPrefixKey(this.properties, ThreadConfigConstant.THREAD_CONFIG_PRENAME);

        final MapDifference<Object, Object> difference = Maps.difference(oldConfigProperties, newConfigProperties);
        // 无变更
        if (difference.areEqual()) {
            return;
        }

        // 新增配置
        Map<Object, Object> newly = difference.entriesOnlyOnRight();
        if (MapUtil.isNotEmpty(newly)) {
            newlyConfig(newly);
        }

        // 配置变更
        final Map<Object, MapDifference.ValueDifference<Object>> changed = difference.entriesDiffering();
        if (MapUtil.isNotEmpty(changed)) {
            changedConfig(changed, newConfigProperties);
        }

        // 删除配置
        Map<Object, Object> deleted = difference.entriesOnlyOnLeft();
        if (MapUtil.isNotEmpty(deleted)) {
            deletedConfig(deleted, oldConfigProperties);
        }

    }

    private void newlyConfig(Map<Object, Object> newly) {

        final Map<String, ThreadPoolConfig> newlyConfig = config(newly);
        if (MapUtil.isEmpty(newlyConfig)) {
            return;
        }
        this.threadConfigCache.putAll(newlyConfig);
    }

    private void changedConfig(Map<Object, MapDifference.ValueDifference<Object>> changed, Map<Object, Object> newConfigProperties) {
        Map<String, ThreadPoolConfig> newThreadConfigMap = extractThreadPoolConfig(changed, newConfigProperties);

        final ModifiableService<ThreadPoolConfig, Boolean> modifiableService = modifiableServiceThreadLocal.get();
        modifiableService.update(newThreadConfigMap.values());
    }

    private Map<String, ThreadPoolConfig> extractThreadPoolConfig(Map<?, ?> changed, Map<Object, Object> fullProperties) {
        final HashMap<?, ?> changeInfos = new HashMap<>(changed);

        Map<String, ThreadPoolConfig> newThreadConfigMap = new HashMap<>(changeInfos.size() / 2);
        Set<?> keys = changeInfos.keySet();
        while (!keys.isEmpty()) {
            String key = String.valueOf(keys.iterator().next());
            final String keyPrefix = findThreadNameIncludePrefix(key);

            final Map<Object, Object> groupConfig = MapUtil.findEntryByPrefixKey(fullProperties, keyPrefix);
            final ThreadPoolConfig threadPoolConfig = threadPoolFactoryConfig(keyPrefix, groupConfig);
            newThreadConfigMap.put(threadPoolConfig.getThreadPoolsName(), threadPoolConfig);

            groupConfig.forEach(changeInfos::remove);
            keys = changeInfos.keySet();
        }
        return newThreadConfigMap;
    }

    /**
     * 删除配置
     *
     * @param deleteProperties    在原来配置文件中存在，但新的配置文件中不存在的项
     * @param oldConfigProperties 原来的配置文件数据项
     */
    private void deletedConfig(Map<Object, Object> deleteProperties, Map<Object, Object> oldConfigProperties) {
        final Map<String, ThreadPoolConfig> threadPoolConfigMap = extractThreadPoolConfig(deleteProperties, oldConfigProperties);
        Collection<ThreadPoolConfig> threadPoolConfigs = new HashSet<>();
        for (Entry<String, ThreadPoolConfig> item : threadPoolConfigMap.entrySet()) {
            if ("default".equals(item.getKey())) {
                continue;
            }
            threadPoolConfigs.add(item.getValue());
        }
        if (CollectionUtil.isEmpty(threadPoolConfigs)) {
            return;
        }
        final ModifiableService<ThreadPoolConfig, Boolean> modifiableService = modifiableServiceThreadLocal.get();
        modifiableService.delete(threadPoolConfigs);
    }

    public void setConfigFilePath(String configPath) {
        File file = new File(configPath);
        this.fileInformation = new FileInformation(configPath);
        if (file.exists()) {
            this.fileInformation.setLastModifiedTime(file.lastModified());
        } else {
            this.fileInformation.setLastModifiedTime(0);
        }
    }


    private Map<String, UsingConfig> usingConfig(Map<Object, Object> usingInfo) {
        String key;
        String domain;
        String config;
        UsingConfig usingConfig;
        Map<String, UsingConfig> usingConfigCache = new HashMap<>(usingInfo.size());
        for (Entry<Object, Object> item : usingInfo.entrySet()) {
            key = String.valueOf(item.getKey());
            domain = key.substring(ThreadConfigConstant.THREAD_USING_PRENAME.length() + 1);

            config = String.valueOf(item.getValue());
            usingConfig = UsingConfig.create(domain, config);
            usingConfigCache.put(usingConfig.getDomain(), usingConfig);
        }
        return usingConfigCache;
    }

    private Map<String, ThreadPoolConfig> config(Map<Object, Object> configInfo) {

        Set<Object> keys = configInfo.keySet();

        String threadPoolsNamePrefix;
        String key;
        Map<Object, Object> threadPoolFactoryGroupConfig;
        Map<String, ThreadPoolConfig> threadConfigCache = new HashMap<>(configInfo.size() / 2);
        while (!keys.isEmpty()) {
            key = String.valueOf(keys.iterator().next());
            threadPoolsNamePrefix = findThreadNameIncludePrefix(key);
            threadPoolFactoryGroupConfig = MapUtil.findEntryByPrefixKey(configInfo, threadPoolsNamePrefix + '.');

            threadPoolFactoryGroupConfig.forEach(configInfo::remove);

            final ThreadPoolConfig threadPoolConfig = threadPoolFactoryConfig(threadPoolsNamePrefix, threadPoolFactoryGroupConfig);
            threadConfigCache.put(threadPoolConfig.getThreadPoolsName(), threadPoolConfig);

            keys = configInfo.keySet();
        }
        return threadConfigCache;
    }

    private ThreadPoolConfig threadPoolFactoryConfig(String prefix, Map<Object, Object> threadPoolConfig) {
        final Map<String, Object> injectFieldVal = new HashMap<>(threadPoolConfig.size());
        threadPoolConfig.forEach((k, v) -> {
            String newKey = String.valueOf(k).substring(prefix.length() + 1);
            String val = String.valueOf(v);

            Object valResult;
            if (val.contains(MATH_OPERATOR) || val.contains(CORES_PLACEHOLDER)) {
                valResult = calculatorExp(val);
            } else {
                valResult = v;
            }
            injectFieldVal.put(newKey, valResult);

        });
        final String threadPoolsName = prefix.substring(ThreadConfigConstant.THREAD_CONFIG_PRENAME.length() + 1);
        injectFieldVal.put("threadPoolsName", threadPoolsName);
        ThreadPoolConfig config = new ThreadPoolConfig();
        Map<Object, Object> otherSetting = ClassUtils.injectFields(config, injectFieldVal);
        config.setOtherSetting(otherSetting);
        return config;
    }

    private int calculatorExp(String val) {
        String[] expArr = val.split("[*]");
        Integer[] expNums = new Integer[expArr.length];
        for (int i = 0; i < expArr.length; i++) {
            String item = expArr[i].trim();

            Integer num;
            if (NumberUtils.isNumber(item)) {
                num = NumberUtils.parse(item, int.class);
            } else if (CORES_PLACEHOLDER.equals(item)) {
                num = AVAILABLE_PROCESSORS;
            } else {
                throw new IllegalArgumentException("表达式错误，只支持乘法，示例[cores*2]，相当于availableProcessors*2，运行宿主机中可用处理器的两倍，其中“cores”字符串表示获取当前运行环境的可用处理器。exp[" + val + "]");
            }
            expNums[i] = num;
        }

        if (expNums.length == 0) {
            return 0;
        }

        long result = expNums[0];
        for (int i = 1; i < expNums.length; i++) {
            result *= expNums[i];
            if (result > 65535 || result <= 0) {
                throw new IllegalArgumentException("表达式最终的结果范围值不允许超过(0,65535]");
            }
        }

        return (int) result;
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
        final Properties config = new Properties();
        config.load(in);
        load(config);
    }

    /**
     * 可以重复调用多次，增加新的配置项，或者修改配置项
     *
     * @param config 配置
     * @throws IOException 流读取异常
     */
    public void load(Properties config) throws IOException {
        if (null == this.properties) {
            log.debug("未初始化过，进行初始化。");
            this.properties = new Properties();

            // 加载默认配置。
            try (InputStream defaultSettings = DefaultThreadPoolsServiceImpl.class
                    .getResourceAsStream(DEFAULT_CONF)) {
                this.properties.load(defaultSettings);
                this.properties.putAll(InitDefaultProperties.DEFAULT_SETTING);
                log.info("加载默认的配置完成。[{}]", this.properties);
            }
        }
        if (null != config) {
            properties.putAll(config);
        }

        Map<Object, Object> configInfo = MapUtil
                .findEntryByPrefixKey(this.properties, ThreadConfigConstant.THREAD_CONFIG_PRENAME);
        final Map<String, ThreadPoolConfig> threadConfigCache = config(configInfo);
        this.threadConfigCache.putAll(threadConfigCache);

        Map<Object, Object> usingInfo = MapUtil
                .findEntryByPrefixKey(this.properties, ThreadConfigConstant.THREAD_USING_PRENAME);
        final Map<String, UsingConfig> usingConfigMap = usingConfig(usingInfo);
        this.usingConfigCache.putAll(usingConfigMap);
    }

    private static class InitDefaultProperties {
        private final static Map<String, String> DEFAULT_SETTING;
        private static final String DEFAULT_CORE_POOL_SIZE = "thread.conf.default.corePoolSize";
        private static final String DEFAULT_MAX_POOL_SIZE = "thread.conf.default.maximumPoolSize";

        static {
            int availableProcessors = AVAILABLE_PROCESSORS;
            HashMap<String, String> temp = new HashMap<>();
            temp.put(DEFAULT_CORE_POOL_SIZE, String.valueOf(availableProcessors));
            temp.put(DEFAULT_MAX_POOL_SIZE, String.valueOf(availableProcessors * 4));
            temp.put(AUTO_SCAN_KEY, "1m");
            DEFAULT_SETTING = Collections.unmodifiableMap(temp);
        }
    }

}
