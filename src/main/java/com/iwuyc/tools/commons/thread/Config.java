package com.iwuyc.tools.commons.thread;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.iwuyc.tools.commons.basic.MapUtil;
import com.iwuyc.tools.commons.basic.MultiMap;
import com.iwuyc.tools.commons.classtools.ClassUtils;
import com.iwuyc.tools.commons.classtools.typeconverter.String2Int;
import com.iwuyc.tools.commons.classtools.typeconverter.TypeConverter;
import com.iwuyc.tools.commons.thread.conf.ConfigConstant;
import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;
import com.iwuyc.tools.commons.thread.conf.UsingConfig;
import com.iwuyc.tools.commons.thread.conf.typeconverter.String2TimeTupleConverter;

public class Config
{

    private static final MultiMap<Class<? extends Object>, TypeConverter<? extends Object, ? extends Object>> typeConverters = new MultiMap<>();

    static
    {
        typeConverters.add(String.class, new String2Int());
        typeConverters.add(String.class, new String2TimeTupleConverter());
    }
    /**
     * 线程池配置缓存。key是线程池的名字，val为线程池的配置实例。
     */
    private final Map<String, ThreadPoolConfig> threadConfigCache = new HashMap<>();

    /**
     * 使用于配置缓存。key是范围，val为线程池名字。
     */
    private final Map<String, UsingConfig> usingConfigCache = new HashMap<>();

    private Properties propertis;

    public Config()
    {
    }

    public void load(InputStream in) throws IOException
    {
        this.propertis = new Properties();
        propertis.load(in);

        Map<Object, Object> configInfo = MapUtil.findEntryByPrefixKey(this.propertis,
                ConfigConstant.THREAD_CONFIG_PRENAME);
        config(configInfo);

        Map<Object, Object> usingInfo = MapUtil.findEntryByPrefixKey(this.propertis,
                ConfigConstant.THREAD_USING_PRENAME);
        usingConfig(usingInfo);
    }

    private void usingConfig(Map<Object, Object> usingInfo)
    {
        String key = null;
        String prefixUsingDomain = null;
        String config = null;
        UsingConfig usingConfig = null;
        for (Entry<Object, Object> item : usingInfo.entrySet())
        {
            key = String.valueOf(item.getKey());
            prefixUsingDomain = key.substring(ConfigConstant.THREAD_USING_PRENAME.length() + 1);

            config = String.valueOf(item.getValue());
            usingConfig = UsingConfig.create(prefixUsingDomain, config);
            usingConfigCache.put(usingConfig.getDomain(), usingConfig);
        }

    }

    private void config(Map<Object, Object> configInfo)
    {

        Set<Object> keys = configInfo.keySet();

        String threadPoolsNamePrefix = null;
        String key = null;
        Map<Object, Object> threadPoolFacotryConfig = null;

        while (!keys.isEmpty())
        {
            key = String.valueOf(keys.iterator().next());
            threadPoolsNamePrefix = findThreadNameIncludePrefix(key);
            threadPoolFacotryConfig = MapUtil.findEntryByPrefixKey(configInfo, threadPoolsNamePrefix);

            threadPoolFacotryConfig.forEach((K, V) ->
            {
                configInfo.remove(K);
            });

            threadPoolFactoryConfig(threadPoolsNamePrefix, threadPoolFacotryConfig);

            keys = configInfo.keySet();
        }
    }

    private void threadPoolFactoryConfig(String prefix, Map<Object, Object> threadPoolConfig)
    {
        final Map<String, Object> injectFieldVal = new HashMap<>();
        threadPoolConfig.forEach((K, V) ->
        {
            String newKey = String.valueOf(K).substring(prefix.length() + 1);
            injectFieldVal.put(newKey, V);
        });
        final String threadPoolsName = prefix.substring(ConfigConstant.THREAD_CONFIG_PRENAME.length() + 1);
        injectFieldVal.put("threadPoolsName", threadPoolsName);

        ThreadPoolConfig config = new ThreadPoolConfig();
        Map<Object, Object> otherSetting = ClassUtils.injectFields(config, injectFieldVal, typeConverters);
        config.setOtherSetting(otherSetting);

        threadConfigCache.put(config.getThreadPoolsName(), config);
        System.out.println("");
    }

    private String findThreadNameIncludePrefix(String key)
    {
        int prefixLength = ConfigConstant.THREAD_CONFIG_PRENAME.length();
        int endIndex = key.indexOf('.', prefixLength + 1);
        return key.substring(0, endIndex);
    }

    public UsingConfig findUsingSetting(String domain)
    {
        UsingConfig config = null;
        int lastDotIndex = -1;
        do
        {
            config = usingConfigCache.get(domain);
            if (null != config)
            {
                return config;
            }
            lastDotIndex = domain.lastIndexOf('.');
            if (lastDotIndex < 0)
            {
                break;
            }
            domain = domain.substring(0, lastDotIndex);
        }
        while (true);

        config = usingConfigCache.get("root");

        return config;
    }

    public ThreadPoolConfig findThreadPoolConfig(String threadPoolsName)
    {
        ThreadPoolConfig config = this.threadConfigCache.get(threadPoolsName);
        if (null == config)
        {
            config = this.threadConfigCache.get("default");
        }
        return config;
    }

}
