package com.iwuyc.tools.commons.thread;

/**
 * 刷新引用对象
 *
 * @author Neil
 */
public interface RefreshDelegate<T> {
    /**
     * 刷新代理类,替换原来的代理类
     *
     * @param newDelegate 新的代理类
     * @return 如果成功刷新, 则返回true, 否则返回false
     */
    boolean refresh(T newDelegate);

    /**
     * 释放并回收旧的代理资源
     *
     * @return 如果成功关闭，则返回true，否则返回false
     */
    boolean release();
}
