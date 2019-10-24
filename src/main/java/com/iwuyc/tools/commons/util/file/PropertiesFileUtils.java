package com.iwuyc.tools.commons.util.file;

import com.iwuyc.tools.commons.util.Constants;
import com.iwuyc.tools.commons.util.collection.CollectionUtil;
import com.iwuyc.tools.commons.util.collection.MapUtil;
import com.iwuyc.tools.commons.util.string.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;

@Slf4j
public class PropertiesFileUtils {

    private static final Character defaultSplitChar = '=';

    private static final String CLASSPATH_PREFIX = "classpath:";

    /**
     * 删除指定properties文件中的数据
     *
     * @param file       properties文件
     * @param deleteKeys 待删除的数据
     * @return 是否替换成功
     */
    public static boolean delPropertiesFile(File file, Set<Object> deleteKeys, ReadWriteLock lock) {
        return replacePropertiesFile(file, Collections.emptyMap(), deleteKeys, lock);
    }

    /**
     * 替换指定properties文件中的数据，会将配置文件中不存在的配置追加到文件最后
     *
     * @param path       properties文件的路径
     * @param properties 待替换的数据
     * @return 是否替换成功
     */
    public static boolean replacePropertiesFile(String path, Map<?, ?> properties) {
        return replacePropertiesFile(path, properties, null);
    }

    public static boolean replacePropertiesFile(String path, Map<?, ?> properties, ReadWriteLock lock) {
        if (StringUtils.isEmpty(path) || MapUtil.isEmpty(properties)) {
            throw new IllegalArgumentException("Argument can't be empty.");
        }

        String fileLocation = FileUtil.absoluteLocation(path);
        File file = new File(fileLocation);
        return replacePropertiesFile(file, properties, lock);
    }

    public static boolean replacePropertiesFile(File file, Map<?, ?> properties, ReadWriteLock lock) {
        return replacePropertiesFile(file, properties, Collections.emptySet(), lock);
    }

    /**
     * 替换指定properties文件中的数据，会将配置文件中不存在的配置追加到文件最后
     *
     * @param file       properties文件
     * @param properties 待替换的数据
     * @return 是否替换成功
     */
    public static boolean replacePropertiesFile(File file, Map<?, ?> properties) {
        return replacePropertiesFile(file, properties, Collections.emptySet(), Constants.UTF8_STR);
    }

    /**
     * 替换指定properties文件中的数据，会将配置文件中不存在的配置追加到文件最后
     *
     * @param file       properties文件
     * @param properties 待替换的数据
     * @param deleteKeys 待删除行的key值
     * @param lock       对文件写操作的时候加写锁
     * @return 是否替换成功
     */
    public static boolean replacePropertiesFile(File file, Map<?, ?> properties, Set<Object> deleteKeys, ReadWriteLock lock) {
        try {
            if (null != lock) {
                lock.writeLock().lock();
            }
            return replacePropertiesFile(file, properties, deleteKeys, Constants.UTF8_STR);
        } finally {
            if (null != lock) {
                lock.writeLock().unlock();
            }
        }
    }

    public static boolean replacePropertiesFile(File file, Map<?, ?> properties, Set<Object> deleteKeys, String charsetEncoding) {
        return replacePropertiesFile(file, properties, deleteKeys, charsetEncoding, true);
    }

    /**
     * 替换指定properties文件中的数据，会将配置文件中不存在的配置追加到文件最后
     *
     * @param file            properties文件
     * @param properties      待替换的数据
     * @param deleteKeys      待删除行的key值
     * @param charsetEncoding 文件的编码格式，如果不提供，则默认使用UTF8。
     * @param appendNewLine   是否将文件中没有的
     * @return 是否替换成功
     */
    public static boolean replacePropertiesFile(File file, Map<?, ?> properties, Set<Object> deleteKeys, String charsetEncoding,
                                                boolean appendNewLine) {
        if (StringUtils.isEmpty(charsetEncoding)) {
            charsetEncoding = Constants.UTF8_STR;
        }

        HashSet<Object> deleteKeysInner = new HashSet<>(deleteKeys);
        StringBuilder newContent = new StringBuilder();
        Character splitChar = null;
        try (FileInputStream fileReader = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fileReader, charsetEncoding))) {
            String line = null;
            boolean isDeleteKey = CollectionUtil.isNotEmpty(deleteKeysInner);
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isBlank(line) || line.charAt(0) == '#') {
                    newContent.append(line).append('\n');
                    continue;
                }
                int splitIndex = line.indexOf('=');
                splitIndex = splitIndex > 0 ? splitIndex : line.indexOf(':');
                if (splitIndex < 0) {
                    continue;
                }
                splitChar = line.charAt(splitIndex);
                String key = line.substring(0, splitIndex);
                if (isDeleteKey && deleteKeysInner.contains(key)) {
                    continue;
                }

                if (!properties.containsKey(key)) {
                    newContent.append(line).append('\n');
                    continue;
                }
                Object val = properties.remove(key);
                newContent.append(key).append(splitChar).append(val).append('\n');
            }
        } catch (IOException e) {
            log.error("读取{}出错，请检查后重试。", file);
        }

        splitChar = splitChar == null ? defaultSplitChar : splitChar;
        if (appendNewLine) {
            for (Map.Entry<?, ?> item : properties.entrySet()) {
                newContent.append(item.getKey()).append(splitChar).append(item.getValue()).append('\n');
            }

        }
        try (FileOutputStream fileWriter = new FileOutputStream(file);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileWriter, charsetEncoding))) {

            writer.write(newContent.toString());
            writer.flush();
        } catch (IOException e) {
            log.error("写入{}出错，请检查后重试。", file);
        }
        return true;
    }

    /**
     * 将指定位置的文件读取为properties类，默认按UTF-8读取文件
     *
     * @param propertiesLocation properties文件的位置
     * @return properties的实例
     */
    public static Properties propertiesReader(String propertiesLocation) {
        return propertiesReader(propertiesLocation, Constants.UTF8_STR);
    }

    /**
     * 将指定位置的文件读取为properties类
     *
     * @param propertiesFile properties文件的位置
     * @param ignoredKey     忽略的key
     * @param ignoredVal     忽略的val
     * @return properties的实例
     */
    public static Properties propertiesReader(File propertiesFile, Collection<Object> ignoredKey, Collection<Object> ignoredVal) {
        return propertiesReader(propertiesFile, Constants.UTF8_STR, ignoredKey, ignoredVal, Constants.NIL_STRING);
    }

    public static Properties propertiesReader(File propertiesFile, ReadWriteLock lock) {
        return propertiesReader(propertiesFile, Constants.UTF8_STR, Collections.emptyList(), Collections.emptyList(), lock, null);
    }

    /**
     * 将指定位置的文件读取为properties类
     *
     * @param propertiesFile  properties文件的位置
     * @param charsetEncoding 编码格式
     * @param ignoredKey      忽略的key
     * @param ignoredVal      忽略的val
     * @param lock            对文件读取的时候是否进行加锁
     * @return properties的实例
     */
    public static Properties propertiesReader(File propertiesFile, String charsetEncoding, Collection<Object> ignoredKey,
                                              Collection<Object> ignoredVal, ReadWriteLock lock, Object defaultVal) {
        try {
            if (null != lock) {
                lock.readLock().lock();
            }
            return propertiesReader(propertiesFile, charsetEncoding, ignoredKey, ignoredVal, defaultVal);
        } finally {
            if (null != lock) {
                lock.readLock().unlock();
            }
        }
    }

    /**
     * 将指定位置的文件读取为properties类
     *
     * @param propertiesFile  properties文件的位置
     * @param charsetEncoding 编码格式
     * @param ignoredKey      忽略的key
     * @param ignoredVal      忽略的val
     * @param fillVal         如果不为null，则将以这个值填充properties对象中所有的value值。
     * @return properties的实例
     */
    public static Properties propertiesReader(File propertiesFile, String charsetEncoding, Collection<Object> ignoredKey,
                                              Collection<Object> ignoredVal, Object fillVal) {
        if (StringUtils.isEmpty(charsetEncoding)) {
            charsetEncoding = Constants.UTF8_STR;
        }

        IgnorableProperties properties = new IgnorableProperties();
        properties.setFillVal(fillVal);
        if (CollectionUtil.isNotEmpty(ignoredKey)) {
            properties.addIgnoreKey(ignoredKey.toArray());
        }
        if (CollectionUtil.isNotEmpty(ignoredVal)) {
            properties.addIgnoreVal(ignoredVal.toArray());
        }

        try (FileInputStream fileInputStream = new FileInputStream(propertiesFile);
             InputStreamReader reader = new InputStreamReader(fileInputStream, charsetEncoding)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new IllegalArgumentException("Read properties make a mistake.", e);
        }
        return properties;
    }

    /**
     * 将指定位置的文件读取为properties类
     *
     * @param propertiesFile  properties文件的位置
     * @param charsetEncoding 编码格式
     * @return properties的实例
     */
    public static Properties propertiesReader(File propertiesFile, String charsetEncoding) {
        return propertiesReader(propertiesFile, charsetEncoding, Collections.emptyList(), Collections.emptyList(), null);
    }

    /**
     * 将指定位置的文件读取为properties类
     *
     * @param propertiesLocation properties文件的位置
     * @param charsetEncoding    编码格式
     * @return properties的实例
     */
    public static Properties propertiesReader(String propertiesLocation, String charsetEncoding) {
        String absolutePropertiesPath = FileUtil.absoluteLocation(propertiesLocation);
        File propertiesFile = new File(absolutePropertiesPath);

        return propertiesReader(propertiesFile, charsetEncoding, Collections.emptyList(), Collections.emptyList(), Constants.NIL_STRING);
    }

}
