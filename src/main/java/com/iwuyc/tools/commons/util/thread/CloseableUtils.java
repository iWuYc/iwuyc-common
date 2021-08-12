package com.iwuyc.tools.commons.util.thread;

import com.iwuyc.tools.commons.util.collection.ArrayUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CloseableUtils {
    public static void close(AutoCloseable... closeable) {
        if (ArrayUtil.isEmpty(closeable)) {
            return;
        }
        for (AutoCloseable item : closeable) {
            try {
                item.close();
            } catch (Exception e) {
                log.warn("关闭失败。instance:{};异常信息为：{}", item, e.getMessage());
                log.debug("异常堆栈为：", e);
            }
        }
    }
}
