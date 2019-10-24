package com.iwuyc.tools.commons.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;

/**
 * 线程工具
 *
 * @author Neil
 * @since 2019-01-25 10:06:41
 */
public class ThreadUtils {

    /**
     * 获取调用方法的父代码位置。类A的方法a的第n行调用类B的b方法，方法b调用com.iot.util.concurrent.ThreadUtils#callLocationInfo() 方法，
     * callLocationInfo将返回方法A的栈追踪信息，{declaringClass:A,methodName:a,lineNumber:n}
     *
     * @return 调用的栈追踪信息
     */
    public static Optional<StackTraceElement> callLocationInfo() {
        RuntimePermission permission = new RuntimePermission("getStackTrace");
        Optional<StackTraceElement> result = AccessController.doPrivileged((PrivilegedAction<Optional<StackTraceElement>>) () -> {
            StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
            if (stackTraces.length < 7) {
                return Optional.empty();
            }
            return Optional.ofNullable(stackTraces[6]);
        }, null, permission);
        return result;
    }
}
