package com.iwuyc.tools.commons.util.file;

import com.iwuyc.tools.commons.basic.AbstractMapUtil;
import com.iwuyc.tools.commons.basic.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Map;
import java.util.Properties;

import static com.iwuyc.tools.commons.util.file.FileUtil.DEFAULT_CHARSET_ENCODING;

@Slf4j
public class PropertiesFileUtils {

    private static final Character defaultSplitChar = '=';

    /**
     * 替换指定properties文件中的数据
     *
     * @param path       properties文件的路径
     * @param properties 待替换的数据
     * @return 是否替换成功
     */
    public static boolean replacePropertiesFile(String path, Map<?, ?> properties) {
        return replacePropertiesFile(path, properties, "UTF-8");
    }

    /**
     * 替换指定properties文件中的数据
     *
     * @param path       properties文件的路径
     * @param properties 待替换的数据
     * @return 是否替换成功
     */
    public static boolean replacePropertiesFile(String path, Map<?, ?> properties, String charsetEncoding) {
        if (StringUtils.isEmpty(path) || AbstractMapUtil.isEmpty(properties)) {
            throw new IllegalArgumentException("Argument can't be empty.");
        }

        String fileLocation = FileUtil.absoluteLocation(path);
        File file = new File(fileLocation);

        StringBuilder newContent = new StringBuilder();
        Character splitChar = null;
        try (FileInputStream fileReader = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileReader, charsetEncoding))) {
            String line = null;

            while ((line = reader.readLine()) != null) {
                if (StringUtils.isEmpty(line) || line.charAt(0) == '#') {
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
                if (!properties.containsKey(key)) {
                    newContent.append(line).append('\n');
                    continue;
                }
                newContent.append(key).append(splitChar).append(properties.remove(key)).append('\n');
            }
        } catch (IOException e) {
            log.error("读取{}出错，请检查后重试。", path);
            System.exit(-1);
        }

        splitChar = splitChar == null ? defaultSplitChar : splitChar;

        for (Map.Entry<?, ?> item : properties.entrySet()) {
            newContent.append(item.getKey()).append(splitChar).append(item.getValue()).append('\n');
        }
        try (FileOutputStream fileWriter = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileWriter, charsetEncoding))) {
            writer.write(newContent.toString());
            writer.flush();
        } catch (IOException e) {
            log.error("写入{}出错，请检查后重试。", path);
            System.exit(-1);
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
        return propertiesReader(propertiesLocation, DEFAULT_CHARSET_ENCODING);
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
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(new File(absolutePropertiesPath));
            InputStreamReader reader = new InputStreamReader(fileInputStream, charsetEncoding)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new IllegalArgumentException("Read properties make a mistake.", e);
        }
        return properties;
    }
}
