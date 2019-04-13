package com.iwuyc.tools.commons.util.file;

import com.iwuyc.tools.commons.basic.AbstractMapUtil;
import com.iwuyc.tools.commons.basic.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class PropertiesFileUtils {

    /**
     * 替换指定properties文件中的数据
     *
     * @param path       properties文件的路径
     * @param properties 待替换的数据
     * @return 是否替换成功
     */
    public static boolean replacePropertiesFile(String path, Map<String, Object> properties) {
        if (StringUtils.isEmpty(path) || AbstractMapUtil.isEmpty(properties)) {
            throw new IllegalArgumentException("Argument can't be empty.");
        }

        Optional<String> filePathOpt = FileUtil.absoluteLocation(path);
        if (!filePathOpt.isPresent()) {
            return false;
        }
        String fileLocation = filePathOpt.get();
        File file = new File(fileLocation);

        StringBuilder newContent = new StringBuilder();
        Character splitChar = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;

            while ((line = reader.readLine()) != null) {
                if (StringUtils.isEmpty(line) || line.charAt(0) == '#') {
                    newContent.append(line);
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

        splitChar = splitChar == null ? '=' : splitChar;

        for (Map.Entry<String, Object> item : properties.entrySet()) {
            newContent.append(item.getKey()).append(splitChar).append(item.getValue()).append('\n');
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(newContent.toString());
            writer.flush();
        } catch (IOException e) {
            log.error("写入{}出错，请检查后重试。", path);
            System.exit(-1);
        }
        return true;
    }
}
