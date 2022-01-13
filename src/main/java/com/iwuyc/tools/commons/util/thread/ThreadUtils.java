package com.iwuyc.tools.commons.util.thread;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 线程工具
 *
 * @author Neil
 * @since 2019-01-25 10:06:41
 */
@Slf4j
public class ThreadUtils {
    private ThreadUtils() {
    }

    private static final String CURRENT_CLASS_NAME = ThreadUtils.class.getName();

    /**
     * 获取调用方法的父代码位置。类A的方法a的第n行调用类B的b方法，方法b调用com.iot.util.concurrent.ThreadUtils#callLocationInfo() 方法，
     * callLocationInfo将返回方法A的栈追踪信息，{declaringClass:A,methodName:a,lineNumber:n}
     *
     * @return 调用的栈追踪信息
     */
    public static Optional<StackTraceElement> callLocationInfo() {
        RuntimePermission permission = new RuntimePermission("getStackTrace");
        return AccessController.doPrivileged(new StackTraceElePrivilegedAction(), null, permission);
    }

    /**
     * Thread Sleep的工具方法，不会抛异常
     *
     * @param sleepTime 休眠的时长
     * @param unit      时间的单位
     * @param logger    日志记录实例
     */
    public static void sleep(long sleepTime, TimeUnit unit, Logger logger) {
        try {
            unit.sleep(sleepTime);
        } catch (InterruptedException e) {
            if (null == logger) {
                logger = log;
            }
            logger.warn("线程休眠过程中引发错误。休眠信息为：sleepTime:{};timeUnit:{}.Cause:{}", sleepTime, unit, e.getMessage());
            logger.debug("Error:", e);
        }
    }

    /**
     * Thread Sleep的工具方法，不会抛异常
     *
     * @param sleepTime 休眠的时长
     * @param unit      时间的单位
     */
    public static void sleep(long sleepTime, TimeUnit unit) {
        sleep(sleepTime, unit, null);
    }

    /**
     * Thread Sleep的工具方法，单位：毫秒
     *
     * @param sleepTime 休眠的时间
     * @param logger    日志实例
     */
    public static void sleep(long sleepTime, Logger logger) {
        sleep(sleepTime, TimeUnit.MILLISECONDS, logger);
    }

    /**
     * Thread Sleep的工具方法，单位：毫秒
     *
     * @param sleepTime 休眠的时间
     */
    public static void sleep(long sleepTime) {
        sleep(sleepTime, (Logger) null);
    }

    private static class StackTraceElePrivilegedAction implements PrivilegedAction<Optional<StackTraceElement>> {

        @Override
        public Optional<StackTraceElement> run() {
            StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
            for (int i = 0; i < stackTraces.length; i++) {
                final StackTraceElement item = stackTraces[i];
                if (CURRENT_CLASS_NAME.equals(item.getClassName())) {
                    return Optional.ofNullable(stackTraces[i + 1]);
                }
            }
            return Optional.empty();
        }
    }

}
