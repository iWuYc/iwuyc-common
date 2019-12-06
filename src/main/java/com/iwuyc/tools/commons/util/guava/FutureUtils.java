package com.iwuyc.tools.commons.util.guava;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;

import java.util.concurrent.ExecutionException;

/**
 * future的工具类
 * @author 吴宇春
 * @since 2018年6月2日10:17:24
 */
public class FutureUtils {

    /**
     * 主要用于对于异常情况无须处理的情况，直接获取future中的值。
     * @param future 待获取值的future
     * @param logger 日志记录类的实例。
     * @param <V>    future中值的类型
     * @return 从future中获取到的值
     */
    public static <V> Optional<V> getFuture(ListenableFuture<V> future, Logger logger) {
        try {
            return Optional.fromNullable(future.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("获取数据异常。原因是：{}", e);
            return Optional.absent();
        }
    }


}
