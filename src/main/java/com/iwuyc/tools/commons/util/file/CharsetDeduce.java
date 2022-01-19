package com.iwuyc.tools.commons.util.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * 文件的字符编码推断
 *
 * @author Neil
 * @date 2022-01-18
 * @since 2022.1
 */
@Slf4j
public class CharsetDeduce {
    /**
     * 判断文本文件的字符集，文件开头三个字节表明编码格式。
     *
     * @param path 文件路径
     * @return 编码格式名称
     */
    public static String charset(String path) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path))) {
            final String charset = charset(bis);
            log.debug("文件-> [{}] 采用的字符集为: [{}]", path, charset);
            return charset;
        } catch (IOException e) {
            log.warn("读取文件异常。", e);
        }
        return "";
    }

    /**
     * 判断文本文件的字符集，文件开头三个字节表明编码格式。
     *
     * @param inputStream 文件输入流
     * @return 编码格式名称
     */
    public static String charset(InputStream inputStream) {
        if (null == inputStream) {
            return "";
        }
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        BufferedInputStream bis;
        if (inputStream instanceof BufferedInputStream) {
            bis = (BufferedInputStream) inputStream;
        } else {
            bis = new BufferedInputStream(inputStream);
        }
        try {

            boolean checked = false;

            bis.mark(0); // 读者注： bis.mark(0);修改为 bis.mark(100);我用过这段代码，需要修改上面标出的地方。
            // Wagsn注：不过暂时使用正常，遂不改之
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                bis.close();
                return charset; // 文件编码为 ANSI
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE"; // 文件编码为 Unicode
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE"; // 文件编码为 Unicode big endian
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8"; // 文件编码为 UTF-8
                checked = true;
            }
            bis.reset();
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0) {
                        break;
                    }
                    // 单独出现BF以下的，也算是GBK
                    if (0x80 <= read && read <= 0xBF) {
                        break;
                    }
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        // 双字节 (0xC0 - 0xDF)
                        if (0x80 > read || read > 0xBF) {
                            break;
                        }
                        // (0x80 - 0xBF),也可能在GB编码内
                        // 也有可能出错，但是几率较小
                    } else if (0xE0 <= read) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                            }
                        }
                        break;
                    }
                }
            }
            bis.close();
        } catch (IOException e) {
            log.warn("读取文件异常。", e);
        } finally {
            IOUtils.closeQuietly(bis);
        }
        return charset;
    }
}
