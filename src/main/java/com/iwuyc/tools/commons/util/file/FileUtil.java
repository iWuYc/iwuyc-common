package com.iwuyc.tools.commons.util.file;

import com.iwuyc.tools.commons.util.string.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * 文件处理工具类
 *
 * @author Neil
 * @since @Sep 3, 2017
 */
@Slf4j
public class FileUtil {

    public static final String DEFAULT_CHARSET_ENCODING = "UTF-8";
    private static final String CLASSPATH_PREFIX = "classpath:";

    /**
     * 将指定的byte数组以指定长度，填充到指定的流中。
     *
     * @param out      指定的流。
     * @param length   需要填充的长度，可大于bytes数组的长度。
     * @param bytesArr bytes数组
     * @throws IOException 文件流异常，对文件进行写操作的时候抛出的文件流异常
     * @author Neil
     */
    private static void fillEmptyByte(OutputStream out, int length, byte[] bytesArr) throws IOException {

        int cursor = 0;
        int fillSize = bytesArr.length;
        while (cursor < length) {
            fillSize = fillSize < length - cursor ? fillSize : length;
            out.write(bytesArr, 0, fillSize);
            cursor += fillSize;
        }
    }

    /**
     * 安全删除文件
     *
     * @param file 将要删除的文件
     * @return 删除成功，则返回true，如果出错，则返回false
     * @author Neil
     */
    public static boolean safeDelete(File file) {
        boolean result = false;
        // Return false if file not exists or not a file;
        if (!file.isFile()) {
            return result;
        }
        try (FileInputStream in = new FileInputStream(file); FileOutputStream out = new FileOutputStream(file)) {
            final byte[] emptyBytes = new byte[1024];

            int availableSize;
            // Fill zero bytes into file.
            while ((availableSize = in.available()) != 0) {
                fillEmptyByte(out, availableSize, emptyBytes);
                long skipResult = in.skip(availableSize);
                log.debug("skipResult:{}", skipResult);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // Delete file.
        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }

    /**
     * 安全删除文件，防止恢复
     *
     * @param path 将要删除的文件的路径
     * @return 删除成功，则返回true，否则返回false
     * @author Neil
     */
    public static boolean safeDelete(String path) {
        return safeDelete(new File(path));
    }

    /**
     * 读取整个文件的数据，调用该方法应当提前校验过文件的大小，不建议将大文件一次性读取出来
     *
     * @param filePath 文件路径
     * @return 文件中的内容
     */
    public static String readAll(String filePath) {
        return readAll(filePath, DEFAULT_CHARSET_ENCODING);
    }

    /**
     * 读取整个文件的数据，调用该方法应当提前校验过文件的大小，不建议将大文件一次性读取出来
     *
     * @param filePath    文件路径
     * @param charsetName 编码类型
     * @return 文件中的内容
     */
    public static String readAll(String filePath, String charsetName) {

        String fileAbsolutePath = absoluteLocation(filePath);
        File file = new File(fileAbsolutePath);
        try (FileInputStream fr = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fr, charsetName))) {
            StringBuilder sb = new StringBuilder();
            String tmp;
            while ((tmp = reader.readLine()) != null) {
                sb.append(tmp).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Read file raise an error.Cause:{}", e.getMessage());
            log.debug("Error Msg Detail:", e);
        }
        return StringUtils.NIL_STRING;
    }

    /**
     * 获取文件的绝对路径
     *
     * @param location 路径，“classpath:”前缀将查找
     * @return 绝对路径
     */
    public static String absoluteLocation(String location) {
        if (StringUtils.isEmpty(location)) {
            throw new IllegalArgumentException("Wrong Argument.The location can't be nil.");
        }

        if (location.startsWith(CLASSPATH_PREFIX)) {
            String classpathLocation = location.substring(CLASSPATH_PREFIX.length());
            URL resource = FileUtil.class.getResource(classpathLocation);
            if (resource == null) {
                resource = FileUtil.class.getResource("/");
                try {
                    String classpath = Paths.get(resource.toURI()).toAbsolutePath().toString();
                    classpathLocation = Paths.get(classpath, classpathLocation).toAbsolutePath().toString();
                    return classpathLocation;
                } catch (URISyntaxException e) {
                    //do nothing
                    log.warn("URI:{};Wrong:{}", location, e.getMessage());
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
            }
            classpathLocation = resource.getFile();
            return classpathLocation;
        }
        return new File(location).getAbsolutePath();
    }

    public static boolean fileExists(String location) {
        if (StringUtils.isEmpty(location)) {
            return false;
        }
        String absoluteLocation = absoluteLocation(location);
        return new File(absoluteLocation).exists();
    }

}