package com.iwuyc.tools.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 文件处理工具类
 * @author @iwuyc
 * @since @Sep 3, 2017
 */
public class FileUtil {

    /**
     * 将指定的byte数组以指定长度，填充到指定的流中。
     * @author @iwuyc
     * @param out 指定的流。
     * @param length 需要填充的长度，可大于bytes数组的长度。
     * @param bytesArr bytes数组
     * @throws Exception
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
            final byte[] emptyBytes = new byte[1024];

            int availableSize = 0;
            // Fill zero bytes into file.
            while ((availableSize = in.available()) != 0) {
                fillEmptyByte(out, availableSize, emptyBytes);
                in.skip(availableSize);
            }

            // Delete file.
            if (file.exists()) {
                file.delete();
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