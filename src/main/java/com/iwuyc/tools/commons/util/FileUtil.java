package com.iwuyc.tools.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 文件处理工具类
 * @author @iwuyc
 * @since @Sep 3, 2017
 */
public class FileUtil {

    /**
     * 将知道长度的0字节数组写入到指定流里
     * @author @iwuyc
     * @param out
     * @param length
     * @throws Exception
     */
    private static void fillEmptyByte(OutputStream out, int length) throws Exception {
        final byte[] EMPYT_BYTES = new byte[1024];
        int cursor = 0;
        int fillSize = 1024;
        while (cursor < length) {
            fillSize = fillSize < length - cursor ? fillSize : length;
            out.write(EMPYT_BYTES, 0, fillSize);
            cursor += fillSize;
        }
    }

    /**
     * 安全删除文件
     * @author @iwuyc
     * @param file 将要删除的文件
     * @return 删除成功，则返回true，如果出错，则返回false
     */
    public static boolean safeDelete(File file) {
        // Return false if file not exists or not a file;
        if (!file.isFile()) {
            return false;
        }
        try (FileInputStream in = new FileInputStream(file); FileOutputStream out = new FileOutputStream(file);) {
            int availableSize = 0;
            while ((availableSize = in.available()) != 0) {
                fillEmptyByte(out, availableSize);
                in.skip(availableSize);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 安全删除文件，防止恢复
     * @author @iwuyc
     * @param path 将要删除的文件的路径
     * @return 删除成功，则返回true，否则返回false
     */
    public static boolean safeDelete(String path) {
        return safeDelete(new File(path));
    }
}